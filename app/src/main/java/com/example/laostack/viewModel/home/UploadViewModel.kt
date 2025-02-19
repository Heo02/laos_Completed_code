package com.example.laostack.viewModel.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.laostack.view.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UploadViewModel : ViewModel() {
    // 첨부된 이미지 상태 관리
    private val _attachedImageUri = MutableStateFlow<Uri?>(null)
    val attachedImageUri: StateFlow<Uri?> = _attachedImageUri

    // 촬영된 이미지 첨부
    fun onImageAttached(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val compressedUri = compressImage(context, uri)
                _attachedImageUri.value = compressedUri
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error compressing image", e)
            }
        }
    }

    // 이미지 URI 압축
    private suspend fun compressImage(context: Context, uri: Uri): Uri = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        var scale = 1
        while ((options.outWidth / scale / 2 >= 1024) && (options.outHeight / scale / 2 >= 1024)) {
            scale *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = scale
        }
        val newInputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(newInputStream, null, decodeOptions)
        newInputStream?.close()

        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val compressedBytes = outputStream.toByteArray()

        val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { it.write(compressedBytes) }

        // FileProvider를 사용하여 URI 생성
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // 사진 파일, PDF 파일 가져오기
    fun onFileSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val mimeType = context.contentResolver.getType(uri)
                when {
                    mimeType?.startsWith("image/") == true -> {
                        // 이미지 파일 처리
                        val compressedUri = compressImage(context, uri)
                        _attachedImageUri.value = compressedUri
                        Log.d("UploadViewModel", "파일 첨부 완료: $mimeType")
                        showToast(context, "사진이 첨부되었습니다.")
                    }
                    mimeType == "application/pdf" -> {
                        // PDF 파일 처리
                        _attachedImageUri.value = uri
                        Log.d("UploadViewModel", "파일 첨부 완료: $mimeType")
                        showToast(context, "PDF가 첨부되었습니다.")
                    }
                    else -> {
                        // 지원하지 않는 파일 형식
                        Log.e("UploadViewModel", "지원하지 않는 파일 형식: $mimeType")
                        showToast(context, "지원하지 않는 파일 형식입니다.")
                    }
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "파일 선택 중 오류 발생", e)
                showToast(context, "파일 처리 중 오류가 발생했습니다.")
            }
        }
    }

    // 이미지 삭제
    fun removeAttachedImage() {
        _attachedImageUri.value = null
    }

    // 뷰모델 팩토리 생성
    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
                    return UploadViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}