package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.file_view.file_model.FileModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FileViewService {

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContentFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Query("ref") ref: String
    ): Response<FileModel>

}