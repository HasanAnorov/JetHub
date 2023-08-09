package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchService {

    @Headers("Accept: application/vnd.github+json")
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<RepositoryModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/users")
    suspend fun searchUsers(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<UserModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/issues")
    suspend fun searchIssues(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<IssuesModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("search/code")
    suspend fun searchCodes(
        @Header("Authorization") authToken: String,
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Response<CodeModel>

}