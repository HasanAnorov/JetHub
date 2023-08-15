package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.networking.RestClient
import com.hasan.jetfasthub.screens.main.commits.models.comment_post_model.CommentRequestModel
import com.hasan.jetfasthub.screens.main.issue.comment_response_model.CommentResponseModel
import com.hasan.jetfasthub.screens.main.issue.comments_model.IssueCommentsModel
import com.hasan.jetfasthub.screens.main.issue.issue_model.IssueModel
import com.hasan.jetfasthub.utility.Constants
import retrofit2.Response

interface IssueRepository {

    suspend fun getIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: String
    ): Response<IssueModel>

    suspend fun getComments(
        token: String,
        owner: String,
        repo: String,
    ): Response<IssueCommentsModel>

    suspend fun postCommit(
        token: String,
        owner: String,
        repo: String,
        issueNumber: String,
        body: String
    ): Response<CommentResponseModel>

    suspend fun deleteComment(
        token: String, owner: String, repo: String, commentId: Int
    ): Response<Boolean>

}

class IssueRepositoryImpl(private val context: Context) : IssueRepository {

    override suspend fun deleteComment(
        token: String,
        owner: String,
        repo: String,
        commentId: Int
    ): Response<Boolean> {
        return RestClient(context).issueService.deleteComment(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
            commentId = commentId
        )
    }

    override suspend fun postCommit(
        token: String,
        owner: String,
        repo: String,
        issueNumber: String,
        body: String
    ): Response<CommentResponseModel> {
        return RestClient(context).issueService.postComment(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
            issueNumber = issueNumber,
            body = CommentRequestModel(body)
        )
    }

    override suspend fun getComments(
        token: String,
        owner: String,
        repo: String
    ): Response<IssueCommentsModel> {
        return RestClient(context).issueService.getComments(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
        )
    }

    override suspend fun getIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: String
    ): Response<IssueModel> {
        return RestClient(context).issueService.getIssue(
            authToken = "Bearer ${Constants.PERSONAL_ACCESS_TOKEN}",
            owner = owner,
            repo = repo,
            issueNumber = issueNumber
        )
    }

}