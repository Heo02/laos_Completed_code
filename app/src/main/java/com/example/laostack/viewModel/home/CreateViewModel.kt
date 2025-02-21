package com.example.laostack.viewModel.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.example.laostack.CreateTextMessage
import com.example.laostack.ErrorResponse
import com.example.laostack.api.APIInterface
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URL

// CreateViewModel.kt
// Create a ViewModel class to be connected to CreateView
class CreateViewModel (
    private val context: Context,
    private val apiService: APIInterface,
) : ViewModel() {

    // Variable to store the result of the regeneration process
    private val _reCreateResult = MutableStateFlow<Result<CreateTextMessage>?>(null)
    val reCreateResult: StateFlow<Result<CreateTextMessage>?> = _reCreateResult

    // Variable to store the progress status of the regeneration process
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Variable to store the upload result
    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus

    // Coroutine Job variable for handling the regeneration process
    private var recreateJob: Job? = null

    // Image cache variable
    private val imageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .build()

    // Recreate the Workbook
    // Boolean parameter `createToOCR`, which determines the API call
    fun recreateText(createToOCR: Boolean){
        recreateJob = viewModelScope.launch {
            try {
                _isLoading.value = true // Indicates that the recreation process is in progress
                val response = if (createToOCR) {
                    apiService.recreateText()
                } else {
                    apiService.recreateCategoryText()
                }
                Log.d("CreateViewModel", "response code: ${response.code()}")

                when (response.code()) {
                    // API request successful
                    200 -> {
                        val body = response.body()
                        if (body != null) {
                            // Store the successful recreation result
                            _reCreateResult.value = Result.success(body.message)
                            Log.d("CreateViewModel", "Recreated workbook info: ${body.message}")
                        } else {
                            _reCreateResult.value = Result.failure(Exception("Response body is empty."))
                        }
                    }
                    // API request failed
                    400 -> {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            Json.decodeFromString<ErrorResponse>(errorBody ?: "").message
                        } catch (e: Exception) {
                            "An unknown error has occurred."
                        }
                        // Store the failed recreation result
                        _reCreateResult.value = Result.failure(Exception(errorMessage))
                    }
                    else -> _reCreateResult.value = Result.failure(Exception("An unknown error has occurred."))
                }
            } catch (e: Exception) { // Error occurred during API request
                _reCreateResult.value = Result.failure(e)
                Log.e("CreateViewModel", "Error occurred during API call: ${e.message}: ${e.message}")
            } finally {
                // Indicates that the recreation process has ended
                _isLoading.value = false
            }
        }
    }

    // Cancel the recreation process
    fun cancelReCreate() {
        recreateJob?.cancel()
        _isLoading.value = false
    }

    // Function to create problem and answer PDFs
    fun createPDFs(context: Context, problemContents: List<ProblemContent>, answerContent: String, wbId: Int) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Loading // Set upload status to Loading
            val problemUri = createPDF(context, "${wbId}_problem", problemContents) // Generate problem PDF
            Log.d("CreateViewModel", "Problem PDF created: $problemUri")
            val answerUri = createPDF(context, "${wbId}_answer", listOf(ProblemContent(answerContent))) // Generate answer PDF
            Log.d("CreateViewModel", "Answer PDF created: $answerUri")
            problemUri?.let { uploadPdf(it, true, wbId) } // Upload problem PDF if created
            answerUri?.let { uploadPdf(it, false, wbId) } // Upload answer PDF if created
        }
    }

    // Function to create a PDF
    // A suspend function that creates a PDF asynchronously
    private suspend fun createPDF(
        context: Context, fullFileName: String, contents: List<ProblemContent>
    ): Uri? = withContext(Dispatchers.IO) {  // Executes on the IO thread for non-blocking I/O operations
        try {
            Log.d("CreateViewModel", "PDF creation started: $fullFileName")
            val file = File(context.filesDir, "$fullFileName.pdf")  // Creates the PDF file in the app's internal file directory
            Log.d("CreateViewModel", "PDF file path: ${file.absolutePath}")

            // Loading fonts from the assets folder
            val koreanFontStream = context.assets.open("fonts/NotoSansKR.ttf")  // Korean font
            val englishFontStream = context.assets.open("fonts/NotoSans.ttf")    // English font
            val laoFontStream = context.assets.open("fonts/NotoSansLao.ttf")     // Lao font
            // Creating PdfFont objects from the font streams
            val koreanFont = PdfFontFactory.createFont(koreanFontStream.readBytes(), "Identity-H")
            val englishFont = PdfFontFactory.createFont(englishFontStream.readBytes(), "Identity-H")
            val laoFont = PdfFontFactory.createFont(laoFontStream.readBytes(), "Identity-H")

            PdfWriter(file).use { writer ->  // Using PdfWriter to create the PDF file
                val pdf = PdfDocument(writer)  // Creating a PdfDocument object
                val document = Document(pdf)  // Creating a Document object to add content to

                contents.forEach { content ->  // Iterating through each ProblemContent object
                    content.text.split("\n").forEach { line ->  // Splitting the text content by line breaks
                        val paragraph = Paragraph()  // Creating a Paragraph object for each line
                        line.forEach { char ->  // Iterating through each character in the line
                            val text = when {
                                char.code in 0xAC00..0xD7A3 -> Text(char.toString()).setFont(koreanFont)  // Use Korean font for Korean characters
                                char.code in 0x2460..0x24FF -> Text(char.toString()).setFont(koreanFont)  // Use Korean font for the original character range
                                char.code in 0x0E80..0x0EFF -> Text(char.toString()).setFont(laoFont)  // Use Lao font for Lao characters
                                else -> Text(char.toString()).setFont(englishFont)  // Use English font for other characters
                            }
                            paragraph.add(text)  // Adding the text object to the paragraph
                        }
                        document.add(paragraph)  // Adding the completed paragraph to the document
                    }
                    // Adding images if available
                    // If an image URL exists, it will be loaded and added to the PDF
                    content.imageUrl?.let { url ->
                        try {
                            // Creating an ImageRequest to load the image from the URL
                            val request = ImageRequest.Builder(context)
                                .data(url)
                                .size(Size.ORIGINAL) // Load the image in its original size
                                .build()
                            val result = imageLoader.execute(request)  // Executing the image load request
                            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap  // Converting the loaded image to Bitmap

                            bitmap?.let {
                                // Compressing the Bitmap into a byte array and adding it to the PDF
                                val stream = ByteArrayOutputStream()
                                it.compress(Bitmap.CompressFormat.PNG, 100, stream)  // Compressing the image into PNG format
                                val imageData = ImageDataFactory.create(stream.toByteArray())  // Creating ImageData from the byte array
                                val image = Image(imageData).setWidth(100f)  // Setting the image width
                                document.add(image)  // Adding the image to the document
                                document.add(Paragraph("\n"))
                            }
                        } catch (e: Exception) {
                            Log.e("CreatePDF", "Image loading failed: $url", e)  // Logging an error if image loading fails
                            val placeholderText = Paragraph("Placeholder for image").setFont(englishFont)  // Adding a placeholder text if the image is missing
                            document.add(placeholderText)  // Adding the placeholder text to the document
                            document.add(Paragraph("\n"))
                        }
                    }
                }
                document.close()  // Closing the document after adding all the content
            }

            Log.d("CreateViewModel", "PDF file size: ${file.length()} bytes")

            // Returning the URI for the created PDF file
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",  // Using FileProvider to get a URI for the file
                file
            ).also {
                Log.d("CreateViewModel", "Created PDF URI: $it")
            }
        } catch (e: Exception) {
            Log.e("CreateViewModel", "PDF creation failed", e)
            null
        }
    }

    // PDF upload (save) function
    // Uploads the PDF to the server based on whether it's a workbook or answer PDF
    private suspend fun uploadPdf(uri: Uri, isWorkbook: Boolean, wbId: Int) {
        try {
            // Open the input stream to read the file from the URI
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()  // Read the file content into a byte array
            inputStream?.close()  // Close the input stream

            // Check if the byteArray is null (couldn't read the file)
            if (byteArray == null) {
                Log.e("CreateViewModel", "Unable to read the file.")
                _uploadStatus.value = UploadStatus.Error("Unable to read the file.")
                return
            }
            // Create a request body with the PDF file content
            val requestBody = byteArray.toRequestBody("application/pdf".toMediaTypeOrNull())

            // Extract the file name from the URI, default to "file.pdf" if unavailable
            val fileName = uri.lastPathSegment ?: "file.pdf"

            // Prepare the MultipartBody.Part for the file upload
            val body = MultipartBody.Part.createFormData("file", fileName, requestBody)

            // Upload the PDF file using the appropriate API endpoint based on the type (workbook or answer)
            val response = if (isWorkbook) {
                apiService.uploadWorkbookPdf(wbId, body)  // Upload workbook PDF
            } else {
                apiService.uploadAnswerPdf(wbId, body)  // Upload answer PDF
            }

            when (response.code()) {
                200 -> {
                    Log.d("CreateViewModel", "PDF upload successful: ${response.body()?.message}")
                    _uploadStatus.value = UploadStatus.Success  // Set success status
                }
                else -> {
                    Log.e("CreateViewModel", "PDF upload failed: ${response.errorBody()?.string()}")
                    _uploadStatus.value = UploadStatus.Error("Upload failed.")  // Handle general upload failure
                }
            }
        } catch (e: Exception) {
            // Catch any exceptions during the upload process and set error status
            Log.e("CreateViewModel", "Error during PDF upload", e)
            _uploadStatus.value = UploadStatus.Error("An error occurred during upload.")
        }
    }


    /* companion object: A special object in Kotlin that acts like a static member of the class
       - Can be accessed without an instance of the class */
    companion object {
        fun provideFactory(
            context: Context,
            apiService: APIInterface,
        ): ViewModelProvider.Factory = object :
        // ViewModelProvider.Factory: A factory pattern for safely creating ViewModel instances
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateViewModel(context, apiService) as T
                // Create and return an instance of the ViewModel
            }
        }
    }
}

// Sealed class representing upload status
// Uses sealed class for better type safety and cleaner state management
sealed class UploadStatus {
    data object Idle : UploadStatus() // Upload is idle
    data object Loading : UploadStatus() // Upload in progress
    data object Success : UploadStatus() // Upload successful
    data class Error(val message: String) : UploadStatus() // Upload failed with an error message
}

// Data class for adding images to PDF
data class ProblemContent(
    val text: String, // Text content
    val imageUrl: String? = null // Optional image URL
)