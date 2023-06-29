package com.hasan.jetfasthub.screens.main.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.NotificationRepository
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repository: NotificationRepository) : ViewModel() {

    private var _state: MutableStateFlow<NotificationsScreenState> = MutableStateFlow(
        NotificationsScreenState()
    )
    val state = _state.asStateFlow()

    fun getAllNotifications(token: String){
        viewModelScope.launch {
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
        }
    }

    fun getUnreadNotifications(token: String, date: String){
        viewModelScope.launch {
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
        }
    }
}


data class NotificationsScreenState(
//    val allNotifications: AllNotifications = AllNotifications.Loading
    val unreadNotifications: Resource<Notification> = Resource.Loading(),
    val allNotifications: Resource<Notification> = Resource.Loading(),
    val jetHubNotifications: Resource<Notification> = Resource.Loading()
)


//sealed interface AllNotifications {
//    object Loading : AllNotifications
//    data class Success(val notification: Notification) : AllNotifications
//    data class Failure(val errorMessage: String) : AllNotifications
//}