package com.hasan.jetfasthub.data

import android.content.Context
import android.util.Log
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentPostResponse
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.gists.gist_comment_response.GistCommentResponse
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface CommentRepository {

    suspend fun editComment(
        token: String,
        owner: String,
        repo: String,
        commentId: Int,
        body: String
    ): Response<CommentPostResponse>

    suspend fun editGistComment(
        token: String,
        gistId: String,
        commentId: Int,
        body: String
    ): Response<GistCommentResponse>
}


class CommentRepositoryImpl(private val context: Context) : CommentRepository {

    override suspend fun editGistComment(
        token: String,
        gistId: String,
        commentId: Int,
        body: String
    ): Response<GistCommentResponse> {
        return RestClient(context).commentService.editGistComment(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            gistId = gistId,
            commentId = commentId,
            body = CommentRequestModel(body = body)
        )
    }

    override suspend fun editComment(
        token: String,
        owner: String,
        repo: String,
        commentId: Int,
        body: String
    ): Response<CommentPostResponse> {
        Log.d("ahi3646", "editComment: comment repo impl ")
        return RestClient(context).commentService.editComment(
            token = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
            commentId = commentId,
            body = CommentRequestModel(body)
        )
    }

}