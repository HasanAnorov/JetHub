package com.hasan.jetfasthub.screens.main.issue

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.IssueRepository
import com.hasan.jetfasthub.screens.main.issue.comments_model.IssueCommentsModel
import com.hasan.jetfasthub.screens.main.issue.issue_model.IssueModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Exception

class IssueViewModel(private val repository: IssueRepository) : ViewModel() {

    private var _state: MutableStateFlow<IssueScreenState> = MutableStateFlow(IssueScreenState())
    val state = _state.asStateFlow()

    fun deleteComment(
        token: String,
        owner: String,
        repo: String,
        commentId: Int
    ): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.deleteComment(token, owner, repo, commentId).let { response ->
                    if (response.code() == 204) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                trySend(false)
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "deleteComment: channel closed ")
        }
    }

    fun postComment(
        token: String,
        owner: String,
        repo: String,
        issueNumber: String,
        body: String
    ): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.postCommit(token, owner, repo, issueNumber, body).let {response ->
                    if(response.code() == 201){
                        trySend(true)
                    }else{
                        Log.d("ahi3646", "postComment: ${response.errorBody().toString()} ")
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "postComment: ${e.message.toString()} ")
                trySend(false)
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "postComment: channel closed ")
        }
    }

    fun getComments(token: String, repo: String, owner: String) {
        viewModelScope.launch {
            try {
                repository.getComments(token, owner, repo).let { commentsResponse ->
                    if (commentsResponse.isSuccessful) {
                        _state.update {
                            it.copy(
                                Comments = Resource.Success(commentsResponse.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Comments = Resource.Failure(commentsResponse.errorBody().toString())
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        Comments = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getIssue(token: String, owner: String, repo: String, issueNumber: String) {
        viewModelScope.launch {
            try {
                repository.getIssue(token, owner, repo, issueNumber).let { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(Issue = Resource.Success(response.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(Issue = Resource.Failure(response.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Issue = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun changeCurrentSheet(sheet: IssueScreenSheets) {
        _state.update {
            it.copy(CurrentSheet = sheet)
        }
    }

    fun init(owner: String, repo: String, issueNumber: String) {
        _state.update {
            it.copy(
                IssueOwner = owner,
                IssueRepo = repo,
                IssueNumber = issueNumber
            )
        }
    }

}

data class IssueScreenState(
    val Issue: Resource<IssueModel> = Resource.Loading(),
    val Comments: Resource<IssueCommentsModel> = Resource.Loading(),
    val CurrentSheet: IssueScreenSheets = IssueScreenSheets.IssueInfoSheet,
    val IssueOwner: String = "",
    val IssueRepo: String = "",
    val IssueNumber: String = ""
)

sealed interface IssueScreenSheets {
    object IssueInfoSheet : IssueScreenSheets

    class UnlockIssueSheet(val issueNumber: String) : IssueScreenSheets

    class CommentDeleteSheet(val commentId: Int) : IssueScreenSheets
}
