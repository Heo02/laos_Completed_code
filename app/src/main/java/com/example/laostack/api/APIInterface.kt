package com.example.laostack.api

import com.example.laostack.CreateTextRequest
import com.example.laostack.CreateTextResponse
import com.example.laostack.DeleteWorkbookResponse
import com.example.laostack.UploadResponse
import com.example.laostack.WorkbooksResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {

    // 텍스트 기반 문제집 생성
    @POST("/api/workbook/processText")
    suspend fun createToText(
        @Query("language") language: String,
        @Body requestBody: CreateTextRequest
    ): Response<CreateTextResponse>

    // 카테고리 기반 문제집 생성
    @POST("/api/workbook/processCategory")
    suspend fun createToCategory(
        @Query("category") category: String,
        @Query("language") language: String
    ): Response<CreateTextResponse>

    // 텍스트 문제집 재생성
    @POST("/api/workbook/retext")
    suspend fun recreateText(
    ): Response<CreateTextResponse>

    // 카테고리 문제집 재생성
    @POST("/api/workbook/reCategorytext")
    suspend fun recreateCategoryText(
    ): Response<CreateTextResponse>

    // 문제지 PDF 저장
    @Multipart
    @POST("/api/workbook/upload")
    suspend fun uploadWorkbookPdf(
        @Query("wb_id") wbId: Int,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    // 답안지 PDF 저장
    @Multipart
    @POST("/api/workbook/answer/upload")
    suspend fun uploadAnswerPdf(
        @Query("wb_id") wbId: Int,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    // 저장된 문제집 전체 조회
    @GET("/api/workbook/all")
    suspend fun getAllWorkbooks(): Response<WorkbooksResponse>

    // 문제지 PDF 다운
    @GET("/api/workbook/download/{wb_id}")
    suspend fun downloadWorkbook(
        @Path("wb_id") wbId: String,
    ): Response<ResponseBody>

    // 답안지 PDF 다운
    @GET("/api/workbook/answer/download/{wb_id}")
    suspend fun downloadAnswer(
        @Path("wb_id") wbId: String,
    ): Response<ResponseBody>

    // 문제집 삭제
    @DELETE("/api/workbook/delete")
    suspend fun deleteWorkbook(
        @Query("wb_id") wbId: Int
    ): Response<DeleteWorkbookResponse>
}