package com.example.laostack

import kotlinx.serialization.Serializable

// 문제 생성 API
data class CreateTextRequest(
    val problemText: String
)

data class CreateTextResponse(
    val message: CreateTextMessage
)

@Serializable
data class CreateTextMessage(
    val wb_id: Int,
    val wb_title: String,
    val question: String? = null,
    val answer: String,
    val imageQuestions: List<ImageQuestion>,
    val textQuestions: String
)

@Serializable
data class ImageQuestion(
    val question: String,
    val imageUrl: String
)


// 오류 메세지
data class ErrorResponse(
    val message: String
)

// PDF 저장
data class UploadResponse(
    val message: String
)


// 문제집 전체 조회
@Serializable
data class WorkbooksResponse(
    val data: List<Workbook>
)

@Serializable
data class Workbook(
    val wb_id: Int,
    val wb_title: String,
    val wb_create: String,
    val wb_content: String,
    val workbook_pdf: WorkbookPdf
)
@Serializable
data class WorkbookPdf(
    val filename: String,
    val pdf_path: String
)

// 문제집 삭제 결과
data class DeleteWorkbookResponse(
    val message: String
)