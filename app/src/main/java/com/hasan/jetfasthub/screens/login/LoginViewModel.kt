package com.hasan.jetfasthub.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.AuthRepository
import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private var _accessTokenModel = MutableLiveData<Resource<Response<AccessTokenModel>>>()
    val accessTokenModel: LiveData<Resource<Response<AccessTokenModel>>> get() = _accessTokenModel

    suspend fun getAccessToken(code: String) {
        viewModelScope.launch {
            _accessTokenModel.value = Resource.Loading()
            repository.getAccessToken(code).let {
                if (it.isSuccessful) {
                    _accessTokenModel.value = Resource.Success(it)
                } else {
                    _accessTokenModel.value = Resource.Failure(it.errorBody().toString())
                }
            }
        }
    }

}