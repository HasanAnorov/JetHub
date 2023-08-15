package com.hasan.jetfasthub.screens.main.commits

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.CommitRepository
import com.hasan.jetfasthub.data.download.Downloader
import com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model.CommitCommentsModel
import com.hasan.jetfasthub.screens.main.commits.models.commit_model.CommitModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommitViewModel(
    private val repository: CommitRepository,
    private val downloader: Downloader
) : ViewModel() {

    private var _state: MutableStateFlow<CommitScreenState> = MutableStateFlow(CommitScreenState())
    val state = _state.asStateFlow()

    fun init(owner: String, repo: String, branch: String){
        _state.update {
            it.copy(
                CommitOwner = owner,
                CommitRepo = repo,
                CommitSha = branch
            )
        }
    }

    fun downloadCommit(url: String, message: String) {
        viewModelScope.launch {
            downloader.downloadCommit(url, message)
        }
    }

    fun getCommit(
        token: String,
        owner: String,
        repo: String,
        branch: String,
    ) {
        viewModelScope.launch {
            try {
                repository.getCommit(token, owner, repo, branch).let { commit ->
                    if (commit.isSuccessful) {
                        _state.update {
                            it.copy(Commit = Resource.Success(commit.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(Commit = Resource.Failure(commit.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Commit = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun postCommitComment(
        token: String,
        owner: String,
        repo: String,
        branch: String,
        body: String
    ): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.postCommit(token, owner, repo, branch, body).let { response ->
                    if (response.code() == 201) {
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
            Log.d("ahi3646", "postCommitComment: channel closed ")
        }
    }

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

    fun getCommitComments(
        token: String,
        owner: String,
        repo: String,
        branch: String,
    ) {
        viewModelScope.launch {
            try {
                repository.getCommitComments(token, owner, repo, branch).let { comments ->
                    if (comments.isSuccessful) {
                        _state.update {
                            it.copy(CommitComments = Resource.Success(comments.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                CommitComments = Resource.Failure(
                                    comments.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(CommitComments = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun onBottomSheetChanged(bottomSheet: CommitScreenSheets) {
        _state.update {
            it.copy(CurrentSheet = bottomSheet)
        }
    }

}

data class CommitScreenState(
    val CommitOwner: String = "",
    val CommitSha: String = "",
    val CommitRepo: String = "",
    val Commit: Resource<CommitModel> = Resource.Loading(),
    val CommitComments: Resource<CommitCommentsModel> = Resource.Loading(),
    val CurrentSheet: CommitScreenSheets = CommitScreenSheets.CommitInfoSheet
)

sealed interface CommitScreenSheets {

    object CommitInfoSheet : CommitScreenSheets

    class CommitDeleteRequestSheet(val commentId: Int) : CommitScreenSheets

}