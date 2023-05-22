package com.hasan.jetfasthub.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.model.Resource
import com.hasan.jetfasthub.screens.main.AppScreens
import com.hasan.jetfasthub.screens.main.home.events.models.Events
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    init {

    }

    fun onBottomBarItemSelected(appScreens: AppScreens) {
        _state.update {
            it.copy(selectedBottomBarItem = appScreens)
        }
    }

    fun getEvents(token: String, username: String) {
        viewModelScope.launch {
            repository.getUserEvents(
                token = token,
                username = username
            ).let { events ->
                if (events.isSuccessful) {
                     _state.update {
                         it.copy(eventsState = EventsState.Content(events.body()!!) )
                     }
                } else {
                    _state.update {
                        it.copy(eventsState = EventsState.Error(events.errorBody().toString()) )
                    }
                }
            }
        }
    }

}

data class HomeScreenState(
    val selectedBottomBarItem: AppScreens = AppScreens.Feeds,
    val eventsState: EventsState = EventsState.Loading
)

sealed interface EventsState {
    object Loading: EventsState
    data class Content(val events: Events): EventsState
    data class Error(val message: String): EventsState
}