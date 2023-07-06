package com.hasan.jetfasthub.screens.main.gists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.GistsRepository
import com.hasan.jetfasthub.screens.main.gists.model.StarredGistModel
import com.hasan.jetfasthub.screens.main.gists.public_gist_model.PublicGistsModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GistsViewModel(private val repository: GistsRepository) : ViewModel() {

    private var _state: MutableStateFlow<GistsScreenState> = MutableStateFlow(GistsScreenState())
    val state = _state.asStateFlow()

    fun getStarredGists(token: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getStarredGists(token, page).let { gists ->
                    if (gists.isSuccessful) {
                        _state.update {
                            it.copy(StarredGists = Resource.Success(gists.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                StarredGists = Resource.Failure(
                                    gists.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(StarredGists = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getUserGists(token: String, username: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getUserGists(token, username, page).let { gists ->
                    if (gists.isSuccessful) {
                        _state.update {
                            it.copy(UserGists = Resource.Success(gists.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserGists = Resource.Failure(
                                    gists.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(UserGists = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getPublicGists(token: String, perPage: Int, page: Int) {
        viewModelScope.launch {
            try {
                repository.getPublicGists(token, perPage, page).let { gists ->
                    if (gists.isSuccessful) {
                        _state.update {
                            it.copy(PublicGists = Resource.Success(gists.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                PublicGists = Resource.Failure(
                                    gists.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(PublicGists = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

}

data class GistsScreenState(
    val UserGists: Resource<GistModel> = Resource.Loading(),
    val StarredGists: Resource<StarredGistModel> = Resource.Loading(),
    val PublicGists: Resource<PublicGistsModel> = Resource.Loading()
)

