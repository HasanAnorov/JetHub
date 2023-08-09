package com.hasan.jetfasthub.screens.main.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.NotificationRepository
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Exception

class NotificationsViewModel(private val repository: NotificationRepository) : ViewModel() {

    private var _state: MutableStateFlow<NotificationsScreenState> = MutableStateFlow(
        NotificationsScreenState()
    )
    val state = _state.asStateFlow()

    fun getAllNotifications(token: String){
        viewModelScope.launch {
            try {
                repository.getAllNotifications(token).let { allNotifications ->
                    if (allNotifications.isSuccessful){
                        _state.update {
                            it.copy(allNotifications = Resource.Success(allNotifications.body()!!))
                        }
                    }else{
                        _state.update {
                            it.copy(allNotifications = Resource.Failure(allNotifications.errorBody().toString()))
                        }
                    }
                }
            }catch (e: Exception){
                _state.update {
                    it.copy(allNotifications = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun getUnreadNotifications(token: String, date: String){
        viewModelScope.launch {
            try {
                repository.getUnreadNotifications(token, date).let { unreadNotifications ->
                    if (unreadNotifications.isSuccessful){
                        _state.update {
                            it.copy(unreadNotifications = Resource.Success(unreadNotifications.body()!!))
                        }
                    }else{
                        _state.update {
                            it.copy(unreadNotifications = Resource.Failure(unreadNotifications.errorBody().toString()))
                        }
                    }
                }
            }catch (e: Exception){
                _state.update {
                    it.copy(unreadNotifications = Resource.Failure(e.message.toString()))
                }
            }
        }
    }

    fun markAsRead(token: String, threadId: String){
        viewModelScope.launch {
            try {
                repository.markAsRead(token, threadId).let { resetContent ->
                    if (resetContent.isSuccessful){
                        getUnreadNotifications(token, "it will get a week before by default")
                        Log.d("ahi3646", "markAsRead: ${resetContent.body()} ")
                    }else{
                        getUnreadNotifications(token, "it will get a week before by default")
                        Log.d("ahi3646", "markAsRead: ${resetContent.errorBody()} ")
                    }
                }
            }catch (e: Exception){
                getUnreadNotifications(token, "it will get a week before by default")
                Log.d("ahi3646", "markAsRead: ${e.message.toString()} ")
            }
        }
    }

}


data class NotificationsScreenState(
    val unreadNotifications: Resource<Notification> = Resource.Loading(),
    val allNotifications: Resource<Notification> = Resource.Loading(),
    val jetHubNotifications: Resource<Notification> = Resource.Loading()
)
