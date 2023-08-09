package com.hasan.jetfasthub.networking.services

import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentPostResponse
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.gists.gist_comment_response.GistCommentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CommentService {

    @Headers("Accept: application/vnd.github+json")
    @PATCH("repos/{owner}/{repo}/comments/{comment_id}")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Int,
        @Body body: CommentRequestModel
    ): Response<CommentPostResponse>

    @Headers("Accept: application/vnd.github+json")
    @PATCH("gists/{gist_id}/comments/{comment_id}")
    suspend fun editGistComment(
        @Header("Authorization") token: String,
        @Path("gist_id") gistId: String,
        @Path("comment_id") commentId: Int,
        @Body body: CommentRequestModel
    ): Response<GistCommentResponse>

}