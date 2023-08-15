package com.hasan.jetfasthub.screens.main.file_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.FileViewRepository
import com.hasan.jetfasthub.data.download.Downloader
import com.hasan.jetfasthub.screens.main.file_view.file_model.FileModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FileViewVM(
    private val repository: FileViewRepository,
    private val downloader: Downloader
): ViewModel() {

    private var _state: MutableStateFlow<FileViewScreenState> = MutableStateFlow(
        FileViewScreenState()
    )
    val state = _state.asStateFlow()

    fun downloadFile(url: String, message: String) {
        viewModelScope.launch {
            downloader.downloadFile(url, message)
        }
    }

    fun getContentFile(token: String, owner: String, repo: String, path: String, ref: String) {
        viewModelScope.launch {
            try {
                repository.getContentFile(token, owner, repo, path, ref).let { contentFiles ->
                    if (contentFiles.isSuccessful) {
                        _state.update {
                            it.copy(
                                files = Resource.Success(contentFiles.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                files = Resource.Failure(
                                    contentFiles.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        files = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

}

data class FileViewScreenState(
    val files: Resource<FileModel> = Resource.Loading()
)