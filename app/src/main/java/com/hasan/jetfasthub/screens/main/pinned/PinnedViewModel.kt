package com.hasan.jetfasthub.screens.main.pinned

import androidx.lifecycle.ViewModel
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PinnedViewModel: ViewModel() {

    private var _state : MutableStateFlow<PinnedScreenState> = MutableStateFlow(PinnedScreenState())
    val state = _state.asStateFlow()



}

data class PinnedScreenState(
    val PinnedRepositories: Resource<Int> = Resource.Loading(),
    val PinnedIssues: Resource<Int> = Resource.Loading(),
    val PinnedPullRequests: Resource<Int> = Resource.Loading(),
    val PinnedGists: Resource<Int> = Resource.Loading()
)