package com.hasan.jetfasthub.screens.main.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.home.received_events_model.ReceivedEventsModelItem
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Constants.chooseFromEvents
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val token = PreferenceHelper.getToken(requireContext())

        homeViewModel.getAuthenticatedUser(token)
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { authenticatedUser ->
                PreferenceHelper.saveAuthenticatedUser(requireContext(), authenticatedUser.login)

                homeViewModel.getUser(token, authenticatedUser.login)
                homeViewModel.getReceivedEvents(token, authenticatedUser.login)
            }
            .launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val state by homeViewModel.state.collectAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scaffoldState = rememberScaffoldState(drawerState = drawerState)
                val drawerScope = rememberCoroutineScope()

                activity?.onBackPressedDispatcher?.addCallback(
                    viewLifecycleOwner,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            if (scaffoldState.drawerState.isOpen) {
                                drawerScope.launch {
                                    drawerState.close()
                                }
                            } else {
                                isEnabled = false
                                activity?.onBackPressedDispatcher!!.onBackPressed()
                            }
                        }
                    }
                )

                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onBottomBarItemSelected = homeViewModel::onBottomBarItemSelected,
                        onNavigate = { dest, data, extra ->
                            when (dest) {

                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                R.id.action_homeFragment_to_notificationsFragment -> {
                                    findNavController().navigate(dest)
                                }

                                R.id.action_homeFragment_to_searchFragment -> {
                                    findNavController().navigate(dest)
                                }

                                R.id.action_homeFragment_to_repositoryFragment -> {
                                    val bundle = Bundle()
                                    bundle.putString("repository_owner", data)
                                    bundle.putString("repository_name", extra)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_homeFragment_to_profileFragment -> {
                                    Log.d("ahi3646", "onCreateView: $data -- $extra ")
                                    val bundle = Bundle()
                                    bundle.putString("username", data)
                                    bundle.putString("start_index", extra)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_homeFragment_to_pinnedFragment -> {
                                    findNavController().navigate(dest)
                                }

                                R.id.action_homeFragment_to_gistsFragment -> {
                                    val bundle = bundleOf("gist_data" to data)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_homeFragment_to_faqFragment -> {
                                    findNavController().navigate(dest)
                                }

                                R.id.action_homeFragment_to_settingsFragment -> {
                                    findNavController().navigate(dest)
                                }

                                R.id.action_homeFragment_to_aboutFragment -> {
                                    findNavController().navigate(dest)
                                }

                            }
                        },
                        scaffoldState = scaffoldState,
                        onNavigationClick = {
                            drawerScope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainContent(
    state: HomeScreenState,
    onBottomBarItemSelected: (AppScreens) -> Unit,
    onNavigate: (Int, String?, String?) -> Unit,
    scaffoldState: ScaffoldState,
    onNavigationClick: () -> Unit
) {
    val sheetScope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetContent = {
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Are you sure ?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { sheetScope.launch { sheetState.collapse() } }) {
                        Text(text = "Ok")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = { sheetScope.launch { sheetState.collapse() } }) {
                        Text(text = "No")
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    sheetScope.launch {
                        if (sheetState.isExpanded) {
                            sheetState.collapse()
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Scaffold(
            modifier = Modifier
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (sheetState.isExpanded) {
                                sheetScope.launch {
                                    sheetState.collapse()
                                }
                            }
                        }
                    )
                },
            scaffoldState = scaffoldState,
            topBar = { AppBar(onNavigationClick, onNavigate) },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = {
                DrawerHeader(user = state.user)
                DrawerBody(
                    closeDrawer = onNavigationClick,
                    username = state.user.data?.login ?: "",
                    onLogout = {
                        onNavigationClick()
                        sheetScope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            } else {
                                sheetState.collapse()
                            }
                        }
                    },
                    onNavigate = onNavigate
                )
            },
            content = { contentPadding ->
                when (state.selectedBottomBarItem) {
                    AppScreens.Feeds -> FeedsScreen(
                        contentPadding,
                        state.receivedEventsState,
                        onNavigate
                    )

                    AppScreens.Issues -> IssuesScreen()
                    AppScreens.PullRequests -> PullRequestScreen()
                }
            },
            bottomBar = {
                BottomNav(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(58.dp),
                    onBottomBarItemSelected = onBottomBarItemSelected,
                )
            },
        )
    }
}

@Composable
fun BottomNav(
    modifier: Modifier,
    onBottomBarItemSelected: (AppScreens) -> Unit,
) {
    Surface(
        elevation = 16.dp,
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAppBar(contentColor = MaterialTheme.colorScheme.surface) {
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_github),
                            contentDescription = "Feed Screen",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Feeds", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onBottomBarItemSelected(AppScreens.Feeds) },
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_issues),
                            contentDescription = "Issues Screen",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Issues", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onBottomBarItemSelected(AppScreens.Issues) },
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_pull_requests),
                            contentDescription = "PullRequest Screen",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Pull Requests", color = MaterialTheme.colorScheme.onSurface) },
                    selected = false,
                    onClick = { onBottomBarItemSelected(AppScreens.PullRequests) },
                )
            }
        }
    }
}

@Composable
fun FeedsScreen(
    contentPaddingValues: PaddingValues,
    receivedEventsState: ReceivedEventsState,
    onNavigate: (Int, String?, String?) -> Unit
) {
    when (receivedEventsState) {

        is ReceivedEventsState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is ReceivedEventsState.Success -> {
            val events = receivedEventsState.events
            if (events.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(contentPaddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(events) { eventItem ->
                        ItemEventCard(eventItem, onNavigate)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No feeds")
                }
            }
        }

        is ReceivedEventsState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
            }
        }

    }
}

@Composable
fun IssuesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Issues Screen", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PullRequestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pull Requests Screen", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
private fun ItemEventCard(
    eventItem: ReceivedEventsModelItem,
    onNavigate: (Int, String?, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                val uri = Uri.parse(eventItem.repo.url).lastPathSegment
                val parentUsername = Uri.parse(eventItem.repo.url).pathSegments[1]

                when (eventItem.type) {
                    "ForkEvent" -> {
                        onNavigate(
                            R.id.action_homeFragment_to_repositoryFragment,
                            eventItem.actor.login,
                            eventItem.payload.forkee.name,
                        )
                    }

                    "ReleaseEvent" -> {
                        onNavigate(
                            R.id.action_homeFragment_to_repositoryFragment,
                            parentUsername,
                            uri,
                        )
                    }

                    else -> {
                        onNavigate(
                            R.id.action_homeFragment_to_repositoryFragment,
                            parentUsername,
                            uri,
                        )
                    }
                }
            },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {

            GlideImage(
                failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                imageModel = {
                    eventItem.actor.avatar_url
                }, // loading a network image using an URL.
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .size(48.dp, 48.dp)
                    .clip(CircleShape)
                    .clickable {
                        Log.d("ahi3646", "ItemEventCard: user login - ${eventItem.actor.login} ")
                        onNavigate(
                            R.id.action_homeFragment_to_profileFragment,
                            eventItem.actor.login,
                            null
                        )
                    },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterStart,
                    contentDescription = "Actor Avatar"
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(eventItem.actor.login)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(
                                " " + stringResource(id = chooseFromEvents(eventItem.type).action).lowercase(
                                    Locale.getDefault()
                                ) + " "
                            )
                        }
                        append(eventItem.repo.name)
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = chooseFromEvents(eventItem.type).icon),
                        contentDescription = stringResource(
                            id = chooseFromEvents(eventItem.type).action
                        ),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ParseDateFormat.getTimeAgo(eventItem.created_at).toString(),
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = androidx.compose.material.MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
