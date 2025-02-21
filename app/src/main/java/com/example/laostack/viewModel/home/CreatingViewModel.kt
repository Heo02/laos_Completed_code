package com.example.laostack.viewModel.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.laostack.CreateTextMessage
import com.example.laostack.CreateTextRequest
import com.example.laostack.api.APIInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CreatingViewModel(
    private val apiService: APIInterface,
) : ViewModel() {
    // OCR 결과 저장하는 변수
    private val _ocrResult = MutableStateFlow<OcrResult>(OcrResult.Loading)

    // 생성 결과 저장하는 변수
    private val _createTextResult = MutableStateFlow<CreateTextResult>(CreateTextResult.Idle)
    val createTextResult: StateFlow<CreateTextResult> = _createTextResult

    // 뭐로 만든 것인지 저장하는 변수
    private val _createToOCR = MutableStateFlow(false)
    val createToOCR: StateFlow<Boolean> = _createToOCR.asStateFlow()

    // OCR 파일 형식에 따라 동작 분류
    fun performOCR(context: Context, language: String, uri: Uri) {
        viewModelScope.launch {
            try {
                _ocrResult.value = OcrResult.Loading
                val mimeType = context.contentResolver.getType(uri)
                val text = when {
                    mimeType?.startsWith("image/") == true -> performImageOCR(context, uri)

                    mimeType == "application/pdf" -> {
                        val result = performPdfOCR(context, uri)
                        result
                    }

                    else -> throw IllegalArgumentException("지원하지 않는 파일 형식: $mimeType")
                }
                _ocrResult.value = OcrResult.Success(text)
                Log.d("CreatingViewModel","변환된 텍스트: $text")
                processText(language ,text)
            } catch (e: Exception) {
                _ocrResult.value = OcrResult.Error("OCR 처리 중 오류 발생: ${e.message}")
                Log.e("CreatingViewModel", "OCR 변환 중 오류 발생", e)
            }
        }
    }

    // 이미지 파일에서 텍스트 추출(OCR_Image)
    private suspend fun performImageOCR(context: Context, uri: Uri): String {
        Log.d("CreatingViewModel","이미지 파일 OCR 시작")
        return withContext(Dispatchers.IO) {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image).await().text
        }
    }

    // PDF 파일에서 텍스트 추출(OCR_Pdf)
    private suspend fun performPdfOCR(context: Context, pdfUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("CreatingViewModel", "PDF 파일 OCR 시작")
                val pdfFile = copyUriToFile(context, pdfUri)
                Log.d("CreatingViewModel", "PDF 파일 경로 복사")
                val images = pdfToImages(pdfFile)
                Log.d("CreatingViewModel", "PDF 파일 이미지로 변환")
                val fullText = StringBuilder()

                images.forEach { bitmap ->
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val result = recognizer.process(image).await()
                    fullText.append(result.text).append("\n")
                    bitmap.recycle()
                }

                pdfFile.delete() // 임시 파일 삭제
                fullText.toString()

            } catch (e: Exception) {
                Log.e("CreatingViewModel", "PDF OCR 처리 중 오류 발생", e)
                throw e
            }
        }
    }

    // PDF 경로 복사(OCR_Pdf)
    private suspend fun copyUriToFile(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    }

    // OCR을 위해 PDF 이미지로 변환(OCR_Pdf)
    private suspend fun pdfToImages(pdfFile: File): List<Bitmap> = withContext(Dispatchers.IO) {
        val images = mutableListOf<Bitmap>()
        val renderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))

        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            images.add(bitmap)
            page.close()
        }

        renderer.close()
        images
    }

    // OCR로 문제 생성
    private fun processText(language: String, text: String) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                _createTextResult.value = CreateTextResult.Loading
                val convertedLanguage = convertLanguage(language)
                Log.d("CreatingViewModel", "language: $convertedLanguage")
                Log.d("CreatingViewModel", "text: $text")
                val response = apiService.createToText(convertedLanguage, CreateTextRequest(text))
                Log.d("CreatingViewModel", "response code: ${response.code()}")
                when (response.code()) {
                    200 -> {
                        val body = response.body()
                        if (body != null) {
                            _createTextResult.value = CreateTextResult.Success(body.message)
                            Log.d("CreatingViewModel", "생성된 문제집 정보: ${body.message}")
                            _createToOCR.value = true
                        } else {
                            _createTextResult.value = CreateTextResult.Error("응답 본문이 비어있습니다.")
                        }
                    }
                    400 -> _createTextResult.value = CreateTextResult.Error("요약된 텍스트가 없습니다.")
                    401 -> _createTextResult.value = CreateTextResult.Error("인증에 실패했습니다.")
                    else -> _createTextResult.value = CreateTextResult.Error("알 수 없는 오류가 발생했습니다.")
                }
            } catch (e: Exception) {
                _createTextResult.value = CreateTextResult.Error("API 호출 중 오류 발생")
                Log.e("CreatingViewModel", "API 호출 중 오류 발생: ${e.message}")
            }
        }
    }

    // 카테고리로 문제 생성
    fun processCategory(category: String, language: String) {
        viewModelScope.launch {
            try {
                _createTextResult.value = CreateTextResult.Loading
                val convertedLanguage = convertLanguage(language)
                Log.d("CreatingViewModel", "category: $category")
                Log.d("CreatingViewModel", "language: $convertedLanguage")
                val response = apiService.createToCategory(category, convertedLanguage)
                Log.d("CreatingViewModel", "response code: ${response.code()}")
                when (response.code()) {
                    200 -> {
                        val body = response.body()
                        if (body != null) {
                            _createTextResult.value = CreateTextResult.Success(body.message)
                            Log.d("CreatingViewModel", "생성된 문제집 정보: ${body.message}")
                            _createToOCR.value = false
                        } else {
                            _createTextResult.value = CreateTextResult.Error("응답 본문이 비어있습니다.")
                        }
                    }
                    400 -> _createTextResult.value = CreateTextResult.Error("카테고리가 잘못 설정되었습니다.")
                    401 -> _createTextResult.value = CreateTextResult.Error("인증에 실패했습니다.")
                    else -> _createTextResult.value = CreateTextResult.Error("알 수 없는 오류가 발생했습니다.")
                }
            } catch (e: Exception) {
                _createTextResult.value = CreateTextResult.Error("API 호출 중 오류 발생")
                Log.e("CreatingViewModel", "API 호출 중 오류 발생: ${e.message}")
            }
        }
    }

    // 명세서 조건에 맞게 language 수정하는 함수
    private fun convertLanguage(language: String): String {
        return when (language) {
            "ko" -> "Korea"
            "en" -> "English"
            "lo" -> "Thai"
            else -> "Thai"
        }
    }


    // 문제 생성 뷰 전용 팩토리
    companion object {
        fun provideFactory(
            apiService: APIInterface,
        ): ViewModelProvider.Factory = object :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreatingViewModel(apiService) as T
            }
        }
    }
}

// OCR 작업의 결과를 저장하는 클래스
sealed class OcrResult {
    data object Loading : OcrResult()
    data class Success(val text: String) : OcrResult()
    data class Error(val message: String) : OcrResult()
}

// 생성 결과를 저장하는 클래스
sealed class CreateTextResult {
    data object Idle : CreateTextResult()
    data object Loading : CreateTextResult()
    data class Success(val message: CreateTextMessage) : CreateTextResult()
    data class Error(val errorMessage: String) : CreateTextResult()
}