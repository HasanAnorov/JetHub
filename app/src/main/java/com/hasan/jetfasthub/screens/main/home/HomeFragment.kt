package com.hasan.jetfasthub.screens.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.home.received_model.ReceivedEventsItem
import com.hasan.jetfasthub.screens.main.home.user_model.GitHubUser
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Constants.chooseFromEvents
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val destinations = HashMap<String, Int>()
        destinations["about_fragment"] = R.id.action_homeFragment_to_aboutFragment
        destinations["profile_fragment"] = R.id.action_homeFragment_to_profileFragment
        destinations["settings_fragment"] = R.id.action_homeFragment_to_settingsFragment
        destinations["search_fragment"] = R.id.action_homeFragment_to_searchFragment
        destinations["faq_fragment"] = R.id.action_homeFragment_to_faqFragment
        destinations["gists_fragment"] = R.id.action_homeFragment_to_gistsFragment
        destinations["notifications_fragment"] = R.id.action_homeFragment_to_notificationsFragment

        val token = PreferenceHelper.getToken(requireContext())
        val username = "HasanAnorov"

        homeViewModel.getUser(token, username)
        homeViewModel.getReceivedEvents(token, username)

        return ComposeView(requireContext()).apply {
            setContent {
                val state by homeViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onBottomBarItemSelected = homeViewModel::onBottomBarItemSelected,
                        onNavigate = { dest, data ->
                            if (data != null) {
                                val bundle = Bundle()
                                bundle.putString("home_data", data)
                                findNavController().navigate(destinations[dest]!!, bundle)
                            } else {
                                findNavController().navigate(destinations[dest]!!)
                            }
                        },
                        onToolbarItemCLick = { destination ->
                            findNavController().navigate(
                                destination
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    state: HomeScreenState,
    onBottomBarItemSelected: (AppScreens) -> Unit,
    onNavigate: (String, String?) -> Unit,
    onToolbarItemCLick: (Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                content = {
                    TopAppBarContent(scaffoldState, scope, onToolbarItemCLick)
                },
            )
        },
        drawerContent = { DrawerContent(state.user, scaffoldState, scope, onNavigate) },
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


@Composable
private fun TopAppBarContent(
    state: ScaffoldState,
    scope: CoroutineScope,
    onToolbarItemCLick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            scope.launch {
                state.drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
            Log.d("ahi3646", "TopAppBarContent:${state.drawerState.currentValue} ")
        }) {
            Icon(Icons.Rounded.Menu, contentDescription = "Localized description")
        }

        Text(
            color = Color.Black,
            modifier = Modifier
                .weight(1F)
                .padding(start = 10.dp, end = 10.dp),
            text = "JetHub",
            style = MaterialTheme.typography.titleLarge,
        )

        IconButton(onClick = {
            onToolbarItemCLick(R.id.action_homeFragment_to_notificationsFragment)
        }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notification")
        }

        IconButton(onClick = {
            onToolbarItemCLick(R.id.action_homeFragment_to_searchFragment)
        }) {
            Icon(Icons.Rounded.Search, contentDescription = "Search")
        }

    }
}

//Bottom Nav stuffs
@Composable
fun BottomNav(
    modifier: Modifier,
    onBottomBarItemSelected: (AppScreens) -> Unit,
) {
    Surface(elevation = 16.dp) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAppBar(containerColor = Color.White) {
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_github),
                            contentDescription = "Feed Screen"
                        )
                    },
                    label = { Text("Feeds") },
                    selected = false,
                    onClick = { onBottomBarItemSelected(AppScreens.Feeds) },
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_issues),
                            contentDescription = "Issues Screen"
                        )
                    },
                    label = { Text("Issues") },
                    selected = false,
                    onClick = { onBottomBarItemSelected(AppScreens.Issues) },
                )

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_pull_requests),
                            contentDescription = "PullRequest Screen"
                        )
                    },
                    label = { Text("Pull Requests") },
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
    receivedEventsState: ReceivedEventsState, onNavigate: (String, String?) -> Unit
) {
    when (receivedEventsState) {

        is ReceivedEventsState.Loading -> {
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

        is ReceivedEventsState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(receivedEventsState.events) { eventItem ->
                    ItemEventCard(eventItem) {
                        onNavigate(
                            "profile_fragment",
                            eventItem.actor.login
                        )
                    }
                }
            }
        }

        is ReceivedEventsState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong - ${receivedEventsState.message}")
            }
        }
    }
}


@Composable
fun IssuesScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Issues Screen")
    }
}

@Composable
fun PullRequestScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "PullScreen Screen")
    }
}


// events item card
@Composable
fun ItemEventCard(
    eventItem: ReceivedEventsItem, onItemClicked: (eventItem: ReceivedEventsItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(eventItem)
            })
            .padding(4.dp), elevation = 0.dp, backgroundColor = Color.White
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
                    .clip(CircleShape),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterStart,
                    contentDescription = "Actor Avatar"
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
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
                    color = Color.Black,
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
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ParseDateFormat.getTimeAgo(eventItem.created_at).toString(),
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        color = Color.Black,
                        style = androidx.compose.material.MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


//things related to drawer content
@Composable
private fun DrawerContent(
    user: Resource<GitHubUser>,
    state: ScaffoldState,
    scope: CoroutineScope,
    onNavigate: (String, String?) -> Unit
) {
    ModalDrawerSheet {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        ) {
            GlideImage(
                failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                imageModel = { user.data?.avatar_url },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                imageOptions = ImageOptions(
                    contentDescription = "avatar picture",
                    contentScale = ContentScale.Crop,
                )
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 24.dp)
            ) {
                Text(user.data?.name.toString())
                Text(user.data?.login.toString())
            }
        }
        DrawerTabScreen(
            username = user.data?.login ?: "",
            closeDrawer = {
                scope.launch {
                    state.drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            },
            onNavigate = { dest, username ->
                scope.launch {
                    state.drawerState.close()
                }
                onNavigate(dest, username)
            }
        )
    }
}

@Composable
fun DrawerTabScreen(
    username: String,
    closeDrawer: () -> Unit,
    onNavigate: (String, String?) -> Unit
) {

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("MENU", "PROFILE")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            contentColor = Color.Blue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        if (tabIndex == index) {
                            Text(title, color = Color.Blue)
                        } else {
                            Text(title, color = Color.Black)
                        }
                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            0 -> DrawerMenuScreen(username, closeDrawer, onNavigate)
            1 -> DrawerProfileScreen()
        }
    }
}

@Composable
fun DrawerMenuScreen(
    username: String,
    closeDrawer: () -> Unit,
    onNavigate: (String, String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable {
                    closeDrawer()
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_home_24),
                contentDescription = "home icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Home",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable {
                    onNavigate("profile_fragment", username)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = "Profile icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Profile",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_people_alt_24),
                contentDescription = "Organizations icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Organizations",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable {
                    onNavigate("notifications_fragment", null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_notifications_24),
                contentDescription = "Notifications icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Notifications",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_bookmark_24),
                contentDescription = "Pinned icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Pinned",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_trending_up_24),
                contentDescription = "Trending icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Trending",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 4.dp)
                .clickable {
                    onNavigate("gists_fragment", null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_code_24),
                contentDescription = "Gists icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Gists",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.ic_fasthub_mascot),
                contentDescription = "JetHub icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "JetHub",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable { onNavigate("faq_fragment", null) }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "FAQ icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "FAQ",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable {
                    onNavigate("settings_fragment", null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "Setting icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Setting",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable {  }) {
            Image(
                painter = painterResource(id = R.drawable.ic_money),
                contentDescription = "Restore Purchases icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Restore Purchases",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable {
                    onNavigate("about_fragment", null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "About icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "About",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
            )
        }
    }
}

@Composable
fun DrawerProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Logout",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Account icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Add Account",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_book_24),
                contentDescription = "Repositories icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Repositories",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_star_24),
                contentDescription = "Starred icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Starred",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
                contentDescription = "Pinned icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            )
            Text(
                text = "Pinned",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
            )
        }
    }
}