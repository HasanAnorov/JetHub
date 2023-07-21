package com.hasan.jetfasthub.screens.main.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.ProfileRepository
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private var _state: MutableStateFlow<ProfileScreenState> =
        MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    fun getUser(token: String, username: String) {
        viewModelScope.launch {
            repository.getUser(token, username).let { user ->
                if (user.isSuccessful) {
                    _state.update {
                        it.copy(OverviewScreenState = UserOverviewScreen.Content(user.body()!!))
                    }
                } else {
                    _state.update {
                        it.copy(
                            OverviewScreenState = UserOverviewScreen.Error(
                                user.errorBody().toString()
                            )
                        )
                    }
                }
            }
        }
    }

    fun getUserOrganisations(token: String, username: String) {
        viewModelScope.launch {
            repository.getUserOrganisations(token, username).let { organisations ->
                if (organisations.isSuccessful) {
                    _state.update {
                        it.copy(UserOrganisations = Resource.Success(organisations.body()!!))
                    }
                } else {
                    _state.update {
                        it.copy(
                            UserOrganisations = Resource.Failure(
                                organisations.errorBody().toString()
                            )
                        )
                    }
                }
            }
        }
    }

    fun getUserEvents(token: String, username: String) {
        viewModelScope.launch {
            try {
                repository.getUserEvents(token, username).let { userEvents ->
                    if (userEvents.isSuccessful) {
                        _state.update {
                            it.copy(
                                UserEvents = Resource.Success(userEvents.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserEvents = Resource.Failure(userEvents.errorBody()!!.toString())
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        UserEvents = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getUserRepositories(token: String, username: String) {
        viewModelScope.launch {
            try {
                repository.getUserRepository(token, username).let { userRepositories ->
                    if (userRepositories.isSuccessful) {
                        _state.update {
                            it.copy(
                                UserRepositories = Resource.Success(userRepositories.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserRepositories = Resource.Failure(
                                    userRepositories.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        UserRepositories = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getUserStarredRepos(token: String, username: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getUserStarredRepos(token, username, page).let { userStarredRepos ->
                    if (userStarredRepos.isSuccessful) {
                        _state.update {
                            it.copy(
                                UserStarredRepositories = Resource.Success(userStarredRepos.body()!!)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserStarredRepositories = Resource.Failure(
                                    userStarredRepos.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        UserStarredRepositories = Resource.Failure(e.message.toString())
                    )
                }
            }
        }
    }

    fun getUserStarredReposCount(token: String, username: String, per_page: Int) {
        viewModelScope.launch {
            try {
                repository.getUserStarredReposCount(token, username, per_page).let { response ->
                    if (response.isSuccessful) {
                        var headers = response.headers()
                        var body = response.body()

                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "getUserStarredReposCount: ${e.message} '")
            }
        }
    }

    fun getUserFollowings(token: String, username: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getUserFollowings(token, username, page).let { userFollowings ->
                    if (userFollowings.isSuccessful) {
                        _state.update {
                            it.copy(UserFollowings = Resource.Success(userFollowings.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserFollowings = Resource.Failure(
                                    userFollowings.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(UserFollowings = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getUserFollowers(token: String, username: String, page: Int) {
        viewModelScope.launch {
            try {
                repository.getUserFollowers(token, username, page).let { userFollowers ->
                    if (userFollowers.isSuccessful) {
                        _state.update {
                            it.copy(UserFollowers = Resource.Success(userFollowers.body()!!))
                        }
                    } else {
                        _state.update {
                            it.copy(
                                UserFollowers = Resource.Failure(
                                    userFollowers.errorBody().toString()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(UserFollowers = Resource.Failure(e.message.toString()))
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

    fun getFollowStatus(token: String, username: String) {
        viewModelScope.launch {
            try {
                repository.getFollowStatus(token, username).let { response ->
                    if (response.code() == 204) {
                        _state.update {
                            it.copy(
                                isFollowing = true,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "unfollowUser: exception - ${e.message} ")
            }
        }
    }

    fun unfollowUser(token: String, username: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.unfollowUser(token, username).let { response ->
                    if (response.code() == 204) {

                        trySend(false)

                        _state.update {
                            it.copy(
                                isFollowing = false,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "unfollowUser: exception - ${e.message} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("callback_ahi", "callback stop : ")
        }
    }

    fun followUser(token: String, username: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.followUser(token, username).let { res ->
                    if (res.code() == 204) {

                        trySend(true)

                        _state.update {
                            it.copy(
                                isFollowing = true,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "followUser:  ${e.message}")
            }
        }
        awaitClose {
            channel.close()
            Log.d("callback_ahi", "callback stop : ")
        }
    }

    fun isUserBlocked(token: String, username: String) {
        viewModelScope.launch {
            try {
                repository.isUserBlocked(token, username).let { response ->
                    if (response.code() == 204) {
                        _state.update {
                            it.copy(isUserBlocked = true)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "followUser:  ${e.message}")
            }
        }
    }

    fun blockUser(token: String, username: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.blockUser(token, username).let { response ->
                    trySend(true)

                    if (response.code() == 204) {
                        _state.update {
                            it.copy(isUserBlocked = true)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "blockUser: ${e.message} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("callback_ahi", "callback stop : ")
        }

    }

    fun unblockUser(token: String, username: String): Flow<Boolean> = callbackFlow {
        viewModelScope.launch {
            try {
                repository.unblockUser(token, username).let { response ->
                    trySend(false)

                    if (response.code() == 204) {
                        _state.update {
                            it.copy(isUserBlocked = false)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ahi3646", "blockUser: ${e.message} ")
            }
        }
        awaitClose {
            channel.close()
            Log.d("callback_ahi", "callback stop : ")
        }

    }

}


data class ProfileScreenState(
    val OverviewScreenState: UserOverviewScreen = UserOverviewScreen.Loading,
    val UserOrganisations: Resource<OrgModel> = Resource.Loading(),
    val UserEvents: Resource<UserEvents> = Resource.Loading(),
    val UserRepositories: Resource<UserRepositoryModel> = Resource.Loading(),
    val UserStarredRepositories: Resource<StarredRepoModel> = Resource.Loading(),
    val UserFollowings: Resource<FollowingModel> = Resource.Loading(),
    val UserFollowers: Resource<FollowersModel> = Resource.Loading(),
    val UserGists: Resource<GistModel> = Resource.Loading(),
    var isFollowing: Boolean = false,
    var isUserBlocked: Boolean = false
)

sealed interface UserOverviewScreen {

    object Loading : UserOverviewScreen
    data class Content(val user: GitHubUser) : UserOverviewScreen
    data class Error(val message: String) : UserOverviewScreen

}
