package com.hasan.jetfasthub.screens.login.basic_auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BasicAuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BasicAuthUiState())
    val uiState: StateFlow<BasicAuthUiState> = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update {
            it.copy(userName = newUsername)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword)
        }
    }

    fun onPasswordVisibilityChange(newVisibility: Boolean) {
        _uiState.update {
            it.copy(passwordVisibility = newVisibility)
        }
    }

}

data class BasicAuthUiState(
    val userName: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false
)