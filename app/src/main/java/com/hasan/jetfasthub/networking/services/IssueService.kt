package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.issue.comment_response_model.CommentResponseModel
import com.hasan.jetfasthub.screens.main.issue.comments_model.IssueCommentsModel
import com.hasan.jetfasthub.screens.main.issue.issue_model.IssueModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface IssueService {

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssue(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: String,
    ): Response<IssueModel>

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/{owner}/{repo}/issues/comments")
    suspend fun getComments(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<IssueCommentsModel>

    @Headers("Accept: application/vnd.github+json")
    @POST("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun postComment(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: String,
        @Body body: CommentRequestModel
    ): Response<CommentResponseModel>

    @Headers("Accept: application/vnd.github+json")
    @DELETE("repos/{owner}/{repo}/issues/comments/{comment_id}")
    suspend fun deleteComment(
        @Header("Authorization") authToken: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Int,
    ): Response<Boolean>

}