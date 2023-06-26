package com.hasan.jetfasthub.screens.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.ProfileRepository
import com.hasan.jetfasthub.screens.main.profile.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                        it.copy(userScreenState = UserScreenState.Content(user.body()!!))
                    }
                } else {
                    _state.update {
                        it.copy(
                            userScreenState = UserScreenState.Error(
                                user.errorBody().toString()
                            )
                        )
                    }
                }
            }
        }
    }
}

data class ProfileScreenState(
    val userScreenState: UserScreenState = UserScreenState.Loading
)

sealed interface UserScreenState {

    object Loading : UserScreenState
    data class Content(val user: User) : UserScreenState
    data class Error(val message: String) : UserScreenState

}
