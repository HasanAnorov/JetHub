package com.hasan.jetfasthub.screens.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.jetfasthub.data.HomeRepository
import com.hasan.jetfasthub.screens.main.home.events.models.Events
import com.hasan.jetfasthub.screens.main.home.events.received_model.ReceivedEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun getReceivedEvents(token: String, username: String) {
        viewModelScope.launch {
            repository.getReceivedUserEvents(token, username).let { receivedEvents ->
                if (receivedEvents.isSuccessful) {
                    _state.update {
                        it.copy(receivedEventsState = ReceivedEventsState.Content(receivedEvents.body()!!))
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
                        it.copy(eventsState = EventsState.Content(events.body()!!))
                    }
                } else {
                    _state.update {
                        it.copy(eventsState = EventsState.Error(events.errorBody().toString()))
                    }
                }
            }
        }
    }
}

data class HomeScreenState(
    val selectedBottomBarItem: AppScreens = AppScreens.Feeds,
    val eventsState: EventsState = EventsState.Loading,
    val receivedEventsState: ReceivedEventsState = ReceivedEventsState.Loading
)

sealed interface AppScreens {
    object Feeds: AppScreens
    object Issues: AppScreens
    object PullRequests: AppScreens
}

sealed interface EventsState {
    object Loading : EventsState
    data class Content(val events: Events) : EventsState
    data class Error(val message: String) : EventsState
}

sealed interface ReceivedEventsState {
    object Loading : ReceivedEventsState
    data class Content(val events: ReceivedEvents) : ReceivedEventsState
    data class Error(val message: String) : ReceivedEventsState
}