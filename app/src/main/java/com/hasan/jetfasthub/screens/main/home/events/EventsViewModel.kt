package com.hasan.jetfasthub.screens.main.home.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.model.Resource
import com.hasan.jetfasthub.screens.main.home.HomeRepository
import com.hasan.jetfasthub.screens.main.home.events.models.Events
import kotlinx.coroutines.launch
import retrofit2.Response

class EventsViewModel(private val repository: HomeRepository) : ViewModel() {

    private var _events = MutableLiveData<Resource<Response<Events>>>()
    val events: LiveData<Resource<Response<Events>>> get() = _events

    fun getEvents(token: String, username: String,
                 // page: Int
    ) {
        viewModelScope.launch {
            _events.value = Resource.Loading()
            repository.getUserEvents(
                token = token,
                username = username,
                //page = page
            ).let {
                if (it.isSuccessful) {
                    _events.value = Resource.Success(it)
                } else {
                    _events.value = Resource.DataError(it.code())
                }
            }
        }
    }

}