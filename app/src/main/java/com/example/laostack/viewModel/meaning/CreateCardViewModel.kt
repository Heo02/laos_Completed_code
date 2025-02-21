package com.example.laostack.viewModel.meaning

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.laostack.ErrorResponse
import com.example.laostack.Workbook
import com.example.laostack.api.APIInterface
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

// CreateCardViewModel.kt
// Create a ViewModel class to be connected to CreateCardView
class CreateCardViewModel(
    private val context: Context,
    private val apiService: APIInterface
) : ViewModel() {

    // Workbook list
    private val _workbooks = MutableStateFlow<List<Workbook>>(emptyList())
    val workbooks: StateFlow<List<Workbook>> = _workbooks
    // Store the result of workbook deletion
    private val _deleteResult = MutableStateFlow<Result<String>?>(null)
    val deleteResult: StateFlow<Result<String>?> = _deleteResult
    // Store the PDF file path
    private val _pdfFilePath = MutableStateFlow<String?>(null)
    val pdfFilePath: StateFlow<String?> = _pdfFilePath
    // Store error messages
    private val _error = MutableStateFlow<String?>(null)

    // Refresh when ViewModel is initialized
    init {
        fetchWorkbooks()
    }

    // Fetch workbook list
    private fun fetchWorkbooks() {
        viewModelScope.launch {
            try {
                Log.d("CreateCardViewModel", "Starting to fetch workbooks")
                val response = apiService.getAllWorkbooks()
                Log.d("CreateCardViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let { workbookResponse ->
                        _workbooks.value = workbookResponse.data
                    }
                    Log.d("CreateCardViewModel", "Successfully fetched workbooks: ${_workbooks.value.size} workbooks")
                } else if (response.code() == 400) {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    _error.value = errorResponse.message
                    Log.e("CreateCardViewModel", "Failed to fetch workbooks: ${errorResponse.message}")
                } else {
                    _error.value = "An unknown error occurred."
                    Log.e("CreateCardViewModel", "Failed to fetch workbooks: Unknown error")
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                Log.e("CreateCardViewModel", "Exception occurred while fetching workbooks", e)
            }
        }
    }

    // Delete a workbook
    fun deleteWorkbook(wbId: Int) {
        viewModelScope.launch {
            try {
                Log.d("CreateCardViewModel", "Starting to delete workbook: wbId = $wbId")
                val response = apiService.deleteWorkbook(wbId)
                Log.d("CreateCardViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _deleteResult.value = Result.success(body.message)
                        _workbooks.value = _workbooks.value.filter { it.wb_id != wbId }
                        Log.d("CreateCardViewModel", "Successfully deleted workbook: ${body.message}")
                    } else {
                        _deleteResult.value = Result.failure(Exception("Response body is empty."))
                        Log.e("CreateCardViewModel", "Failed to delete workbook: Response body is empty")
                    }
                } else {
                    _deleteResult.value = Result.failure(Exception("An unknown error occurred."))
                    Log.e("CreateCardViewModel", "Failed to delete workbook: Unknown error")
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
                Log.e("CreateCardViewModel", "Exception occurred while deleting workbook", e)
            }
        }
    }

    // Download Problem sheet PDF
    fun downloadWorkbook(wbId: Int) {
        viewModelScope.launch {
            try {
                Log.d("CreateCardViewModel", "Downloading workbook PDF: wbId = $wbId")
                val response = apiService.downloadWorkbook(wbId.toString())
                Log.d("CreateCardViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val filePath = saveFile(it, "workbook_$wbId.pdf")
                        _pdfFilePath.value = filePath
                    }
                } else {
                    Log.e("CreateCardViewModel", "Download failed: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCardViewModel", "Error occurred during download", e)
            }
        }
    }

    // Download answer sheet PDF
    fun downloadAnswer(wbId: Int) {
        viewModelScope.launch {
            try {
                Log.d("CreateCardViewModel", "Downloading answer sheet PDF: wbId = $wbId")
                val response = apiService.downloadAnswer(wbId.toString())
                Log.d("CreateCardViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val filePath = saveFile(it, "answer_$wbId.pdf")
                        _pdfFilePath.value = filePath
                    }
                } else {
                    Log.e("CreateCardViewModel", "Download failed: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCardViewModel", "Error occurred during download", e)
            }
        }
    }

    // Save the downloaded PDF file locally
    private fun saveFile(body: ResponseBody, fileName: String): String {
        val file = File(context.filesDir, fileName)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096) // Buffer to read data in chunks
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush() // Ensure all data is written
            return file.absolutePath // Return the saved file path
        } catch (e: IOException) {
            Log.e("WorkbookViewModel", "Error occurred while saving file", e)
            return ""
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    // Reset the stored PDF file path
    fun resetPdfFilePath() {
        _pdfFilePath.value = null
    }

    /* companion object: A special object in Kotlin that acts like a static member of the class
       - Can be accessed without an instance of the class */
    companion object {
        fun provideFactory(
            context: Context,
            apiService: APIInterface
        ): ViewModelProvider.Factory = object :
        // ViewModelProvider.Factory: A factory pattern for safely creating ViewModel instances
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateCardViewModel(context, apiService) as T
                // Create and return an instance of the ViewModel
            }
        }
    }
}