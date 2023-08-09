package com.hasan.jetfasthub.screens.main.commits

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.CommentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class EditCommentViewModel(private val repository: CommentRepository) : ViewModel() {

    fun editGistComment(
        token: String,
        commentId: Int,
        gistId: String,
        body: String
    ): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.editGistComment(token, gistId, commentId, body).let { response ->
                    if (response.code() == 200) {
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
            Log.d("ahi3646", "editGistComment : channel closed ")
        }
    }

    fun edit(
        token: String,
        owner: String,
        repo: String,
        commentId: Int,
        body: String
    ): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.editComment(token, owner, repo, commentId, body).let { response ->
                    if (response.code() == 200) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                trySend(false)
            }
            Log.d("ahi3646", "just: ")
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "editComment : channel closed ")
        }
    }

}