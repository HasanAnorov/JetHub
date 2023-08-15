package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentPostResponse
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model.CommitCommentsModel
import com.hasan.jetfasthub.screens.main.commits.models.commit_model.CommitModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface  CommitService {

    @Headers("Accept: application/vnd.github+json, application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    @GET("repos/{owner}/{repo}/commits/{ref}")
    suspend fun getCommit(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") branch: String,
    ): Response<CommitModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/commits/{commit_sha}/comments")
    suspend fun getCommitComments(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("commit_sha") ref: String,
    ): Response<CommitCommentsModel>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("repos/{owner}/{repo}/comments/{comment_id}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Int,
    ): Response<Boolean>

    @Headers("Accept: application/vnd.github+json, application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    @POST("repos/{owner}/{repo}/commits/{commit_sha}/comments")
    suspend fun postCommitComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("commit_sha") ref: String,
        @Body body: CommentRequestModel
    ): Response<CommentPostResponse>

}