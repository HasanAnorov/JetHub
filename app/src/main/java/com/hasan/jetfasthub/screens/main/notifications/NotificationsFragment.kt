package com.hasan.jetfasthub.screens.main.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.notifications.model.Notification
import com.hasan.jetfasthub.screens.main.notifications.model.NotificationItem
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : Fragment() {

    private val notificationsViewModel: NotificationsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val token = PreferenceHelper.getToken(requireContext())
        Log.d("ahi3646", "onCreateView: token - $token")

        notificationsViewModel.getAllNotifications(token)
        notificationsViewModel.getUnreadNotifications(token, "enter_preferred_date")

        return ComposeView(requireContext()).apply {
            setContent {
                val state by notificationsViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onNavigate = { dest -> findNavController().navigate(dest) },
                        onRecyclerItemClick = { id ->
                            notificationsViewModel.markAsRead(token, id)
                            Log.d("ahi3646", "onRecyclerviewItem Click : $id ")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    state: NotificationsScreenState,
    onNavigate: (Int) -> Unit,
    onRecyclerItemClick: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                content = {
                    TopAppBarContent(onNavigate)
                },
            )
        },
    ) { contentPadding ->
        TabScreen(contentPadding, state = state, onRecyclerItemClick)
    }
}

@Composable
private fun TopAppBarContent(
    onBackPressed: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            onBackPressed(R.id.action_notificationsFragment_to_homeFragment)
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
        }

        Text(
            color = Color.Black,
            modifier = Modifier
                .weight(1F)
                .padding(start = 10.dp, end = 10.dp),
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun TabScreen(
    contentPaddingValues: PaddingValues,
    state: NotificationsScreenState,
    onRecyclerItemClick: (String) -> Unit
) {

    var tabIndex by remember { mutableStateOf(0) }
    val tabs =
        listOf("UNREAD", "ALL", "JETHUB")

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = tabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            0 -> UnreadNotifications(state.unreadNotifications, onRecyclerItemClick)
            1 -> AllNotifications(state.allNotifications)
            2 -> JetHubNotifications(state.jetHubNotifications)
        }
    }
}

@Composable
fun UnreadNotifications(unreadNotifications: Resource<Notification>, markAsRead: (String) -> Unit) {
    when (unreadNotifications) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            if (!unreadNotifications.data!!.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(unreadNotifications.data) { notification ->
                        UnreadNotificationsItem(notification, markAsRead)
                    }
                }
            } else {
                Text(
                    text = "No news",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
                Log.d("ahi3646", "Unread: ${unreadNotifications.errorMessage}")
            }
        }
    }
}

@Composable
fun AllNotifications(allNotifications: Resource<Notification>) {
    when (allNotifications) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {

            if (!allNotifications.data!!.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(allNotifications.data) { notification ->
                        AllNotificationsItem(notification)
                    }
                }
            } else {
                Text(
                    text = "No news",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
                Log.d("ahi3646", "Unread: ${allNotifications.errorMessage}")
            }
        }
    }
}

@Composable
fun JetHubNotifications(jetHubNotifications: Resource<Notification>) {
    when (jetHubNotifications) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "Success ...")
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
                Log.d("ahi3646", "Unread: ${jetHubNotifications.errorMessage}")
            }
        }
    }
}

@Composable
fun UnreadNotificationsItem(notification: NotificationItem, markAsRead: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_issues),
            contentDescription = "issues image",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
        Column {
            Text(
                text = notification.subject.title, maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notification.repository.full_name, fontSize = 10.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = ParseDateFormat.getTimeAgo(notification.updated_at).toString(),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(end = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { markAsRead(notification.id) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_done),
                        contentDescription = "mark as read"
                    )
                }
            }
        }
    }
}

@Composable
fun AllNotificationsItem(notification: NotificationItem) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_issues),
            contentDescription = "issues image",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
        Column {
            Text(
                text = notification.subject.title, maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = ParseDateFormat.getTimeAgo(notification.updated_at).toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}