package com.hasan.jetfasthub.screens.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.SearchRepository
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    private var _state: MutableStateFlow<SearchScreenState> = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    fun searchRepositories(token: String, query: String, page: Long){
        viewModelScope.launch {
            repository.searchRepositories(token, query, page).let { repositoryModel ->
                if(repositoryModel.isSuccessful){
                    _state.update {
                        it.copy(Repositories = Resource.Success(repositoryModel.body()!!))
                    }
                }else{
                    _state.update {
                        it.copy(Repositories = Resource.Failure(repositoryModel.errorBody().toString()))
                    }
                }
            }
        }
    }

    fun searchUsers(token: String, query: String, page:Long){
        viewModelScope.launch {
            try {
                repository.searchUsers(token, query, page).let { userModel ->
                    if (userModel.isSuccessful){
                        _state.update {
                            it.copy(Users = Resource.Success(userModel.body()!!))
                        }
                    }else{
                        _state.update {
                            it.copy(Users = Resource.Failure(userModel.errorBody().toString()))
                        }
                    }
                }
            }catch (e: Exception){
                _state.update {
                    it.copy(Users = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun searchIssues(token: String, query: String, page: Long){
        viewModelScope.launch {
            try {
                repository.searchIssues(token, query, page).let { issuesModel ->
                    if(issuesModel.isSuccessful){
                        _state.update {
                            it.copy(Issues = Resource.Success(issuesModel.body()!!))
                        }
                    }else{
                        _state.update {
                            it.copy(Issues = Resource.Failure(issuesModel.errorBody().toString()))
                        }
                    }
                }
            }catch (e:Exception){
                _state.update {
                    it.copy(Issues = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun searchCodes(token: String, query: String, page: Long){
        viewModelScope.launch {
            //try {
                repository.searchCodes(token, query, page).let { codeModelResponse ->
                    if(codeModelResponse.isSuccessful){
                        _state.update {
                            it.copy(Codes = Resource.Success(codeModelResponse.body()!!))
                        }
                    }else{
                        _state.update {
                            it.copy(Codes = Resource.Failure(codeModelResponse.errorBody().toString()))
                        }
                    }
                }
//            }catch (e: Exception){
//                _state.update {
//                    it.copy(Codes = Resource.Failure(e.message.toString()))
//                }
//            }
        }
    }

}

data class SearchScreenState(
    val Repositories: Resource<RepositoryModel> = Resource.Loading(),
    val Users: Resource<UserModel> = Resource.Loading(),
    val Issues: Resource<IssuesModel> = Resource.Loading(),
    val Codes: Resource<CodeModel> = Resource.Loading()
)