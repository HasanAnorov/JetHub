package com.hasan.jetfasthub.screens.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.HomeRepository
import com.hasan.jetfasthub.screens.main.home.authenticated_user_model.AuthenticatedUser
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.home.received_events_model.ReceivedEventsModel
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    fun onBottomBarItemSelected(appScreens: AppScreens) {
        _state.update {
            it.copy(selectedBottomBarItem = appScreens)
        }
    }

    fun getIssuesWithCount(token: String, query: String, page: Int) {
        Log.d("ahi3646", "getIssuesWithCount: query -  $query")
        viewModelScope.launch {
            try {
                repository.getIssuesWithCount(token, query, page).let { issuesResponse ->
                    if (issuesResponse.isSuccessful) {
                        _state.update {
                            it.copy(IssuesCreated = Resource.Success(issuesResponse.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                IssuesCreated = Resource.Failure(
                                    issuesResponse.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        IssuesCreated = Resource.Failure(
                            e.message.toString()
                        )
                    )
                }
                Log.d("ahi3646", "getIssuesWithCount: ${e.message} ")
            }
        }
    }

    fun getAuthenticatedUser(token: String): Flow<AuthenticatedUser> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.getAuthenticatedUser(token).let { authenticatedUser ->
                    if (authenticatedUser.isSuccessful) {
                        trySend(authenticatedUser.body()!!)
                    } else {
                        _state.update {
                            it.copy(
                                user = Resource.Failure(
                                    authenticatedUser.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(user = Resource.Failure(e.message.toString()))
                }
            }
        }
        awaitClose {
            channel.close()
            Log.d("ahi3646", "getAuthenticatedUser: callback channel stopped ")
        }
    }

    fun getUser(token: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUser(token, username).let { gitHubUser ->
                    if (gitHubUser.isSuccessful) {
                        _state.update {
                            it.copy(user = Resource.Success(gitHubUser.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(user = Resource.Failure(gitHubUser.errorBody().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(user = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getReceivedEvents(token: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getReceivedUserEvents(token, username).let { receivedEvents ->
                    if (receivedEvents.isSuccessful) {
                        _state.update {
                            it.copy(
                                receivedEventsState = ReceivedEventsState.Success(
                                    receivedEvents.body()!!
                                )
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                receivedEventsState = ReceivedEventsState.Error(
                                    receivedEvents.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        receivedEventsState = ReceivedEventsState.Error(
                            e.message.toString()
                        )
                    )
                }
            }
        }
    }
}

data class HomeScreenState(
    val user: Resource<GitHubUser> = Resource.Loading(),
    val selectedBottomBarItem: AppScreens = AppScreens.Feeds,
    val receivedEventsState: ReceivedEventsState = ReceivedEventsState.Loading,
    val IssuesCreated: Resource<IssuesModel> = Resource.Loading()
)

sealed interface AppScreens {
    object Feeds : AppScreens
    object Issues : AppScreens
    object PullRequests : AppScreens
}


sealed interface ReceivedEventsState {
    object Loading : ReceivedEventsState
    data class Success(val events: ReceivedEventsModel) : ReceivedEventsState
    data class Error(val message: String) : ReceivedEventsState
}