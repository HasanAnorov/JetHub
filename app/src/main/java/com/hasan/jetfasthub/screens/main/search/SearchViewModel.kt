package com.hasan.jetfasthub.screens.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.SearchRepository
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    private var _state: MutableStateFlow<SearchScreenState> = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    fun setInitialQuery(query: String){
        _state.update {
            it.copy(InitialQuery = query)
        }
    }

    fun searchRepositories(token: String, query: String, page: Long) {
        _state.update {
            it.copy(Repositories = ResourceWithInitial.Loading())
        }
        viewModelScope.launch {
            try {
                repository.searchRepositories(token, query, page).let { repositoryModel ->
                    if (repositoryModel.isSuccessful) {
                        _state.update {
                            it.copy(Repositories = ResourceWithInitial.Success(repositoryModel.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Repositories = ResourceWithInitial.Failure(
                                    repositoryModel.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Repositories = ResourceWithInitial.Failure(e.message.toString()))
                }
            }
        }
    }

    fun searchUsers(token: String, query: String, page: Long) {
        _state.update {
            it.copy(Users = ResourceWithInitial.Loading())
        }
        viewModelScope.launch {
            try {
                repository.searchUsers(token, query, page).let { userModel ->
                    if (userModel.isSuccessful) {
                        _state.update {
                            it.copy(Users = ResourceWithInitial.Success(userModel.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Users = ResourceWithInitial.Failure(
                                    userModel.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Users = ResourceWithInitial.Failure(e.message.toString()))
                }
            }
        }
    }

    fun searchIssues(token: String, query: String, page: Long) {
        _state.update {
            it.copy(Issues = ResourceWithInitial.Loading())
        }
        viewModelScope.launch {
            try {
                repository.searchIssues(token, query, page).let { issuesModel ->
                    if (issuesModel.isSuccessful) {
                        _state.update {
                            it.copy(Issues = ResourceWithInitial.Success(issuesModel.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Issues = ResourceWithInitial.Failure(
                                    issuesModel.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Issues = ResourceWithInitial.Failure(e.message.toString()))
                }
            }
        }
    }

    fun searchCodes(token: String, query: String, page: Long) {
        _state.update {
            it.copy(Codes = ResourceWithInitial.Loading())
        }
        viewModelScope.launch {
            try {
                repository.searchCodes(token, query, page).let { codeModelResponse ->
                    if (codeModelResponse.isSuccessful) {
                        _state.update {
                            it.copy(Codes = ResourceWithInitial.Success(codeModelResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                Codes = ResourceWithInitial.Failure(
                                    codeModelResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(Codes = ResourceWithInitial.Failure(e.message.toString()))
                }
            }
        }
    }

}

data class SearchScreenState(
    val InitialQuery: String  = "",
    val Repositories: ResourceWithInitial<RepositoryModel> = ResourceWithInitial.Initial(),
    val Users: ResourceWithInitial<UserModel> = ResourceWithInitial.Initial(),
    val Issues: ResourceWithInitial<IssuesModel> = ResourceWithInitial.Initial(),
    val Codes: ResourceWithInitial<CodeModel> = ResourceWithInitial.Initial()
)

sealed class ResourceWithInitial<T>(
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Initial<T> : ResourceWithInitial<T>()
    class Loading<T> : ResourceWithInitial<T>()
    class Success<T>(data:T) : ResourceWithInitial<T>(data)
    class Failure<T>(errorMessage: String?) : ResourceWithInitial<T>(null,errorMessage)

}