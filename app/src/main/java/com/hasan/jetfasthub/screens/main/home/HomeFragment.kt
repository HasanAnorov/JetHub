package com.hasan.jetfasthub.screens.main.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.home.domain.ReceivedEventsModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesItem
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.ui.theme.RippleCustomTheme
import com.hasan.jetfasthub.utility.Constants.chooseFromEvents
import com.hasan.jetfasthub.utility.IssueState
import com.hasan.jetfasthub.utility.MyIssuesType
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.RepoQueryProvider
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var token: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        token = PreferenceHelper.getToken(context)
        val authenticatedUser = PreferenceHelper.getAuthenticatedUsername(context)

        if(token != "" && authenticatedUser != ""){
                homeViewModel.getUser(token, authenticatedUser)
                homeViewModel.getEvents()

                val createdIssuesState = if(homeViewModel.state.value.issueScreenState[0]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getIssuesWithCount(
                    token = token,
                    query = getUrlForIssues(
                        MyIssuesType.CREATED,
                        createdIssuesState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.CREATED
                )


                val assignedIssuesState = if(homeViewModel.state.value.issueScreenState[1]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getIssuesWithCount(
                    token = token,
                    query = getUrlForIssues(
                        MyIssuesType.ASSIGNED,
                        assignedIssuesState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.ASSIGNED
                )


                val mentionedIssuesState = if(homeViewModel.state.value.issueScreenState[2]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getIssuesWithCount(
                    token = token,
                    query = getUrlForIssues(
                        MyIssuesType.MENTIONED,
                        mentionedIssuesState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.MENTIONED
                )


                val participatedIssuesState = if(homeViewModel.state.value.issueScreenState[3]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getIssuesWithCount(
                    token = token,
                    query = getUrlForIssues(
                        MyIssuesType.PARTICIPATED,
                        participatedIssuesState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.PARTICIPATED
                )


                val createdPullsState = if(homeViewModel.state.value.pullScreenState[0]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getPullsWithCount(
                    token = token,
                    query = getUrlForPulls(
                        MyIssuesType.CREATED,
                        createdPullsState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.CREATED
                )


                val assignedPullsState = if(homeViewModel.state.value.pullScreenState[1]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getPullsWithCount(
                    token = token,
                    query = getUrlForPulls(
                        MyIssuesType.ASSIGNED,
                        assignedPullsState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.ASSIGNED
                )


                val mentionedPullsState = if(homeViewModel.state.value.pullScreenState[2]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getPullsWithCount(
                    token = token,
                    query = getUrlForPulls(
                        MyIssuesType.MENTIONED,
                        mentionedPullsState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.MENTIONED
                )


                val reviewPullsState = if(homeViewModel.state.value.pullScreenState[3]){
                    IssueState.Open
                }else{
                    IssueState.Closed
                }
                homeViewModel.getPullsWithCount(
                    token = token,
                    query = getUrlForPulls(
                        MyIssuesType.REVIEW,
                        reviewPullsState,
                        authenticatedUser
                    ),
                    page = 1,
                    issuesType = MyIssuesType.REVIEW
                )

            }else{
            Toast.makeText(context, "Can't load user data. Please try to resign in!", Toast.LENGTH_SHORT).show()
        }

    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val state by homeViewModel.state.collectAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scaffoldState = rememberScaffoldState(drawerState = drawerState)
                val drawerScope = rememberCoroutineScope()

                val isRefreshing by homeViewModel.isRefreshing.collectAsState()
                val pullRefreshState = rememberPullRefreshState(isRefreshing, { homeViewModel.refresh() })


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
                        isRefreshing = isRefreshing,
                        pullRefreshState = pullRefreshState,
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
                        onIssueItemClicked = { dest, owner, repo, issueNumber ->
                            Log.d("ahi3646", "onCreateView: $owner  $repo  $issueNumber ")
                            val bundle = Bundle()
                            bundle.putString("issue_owner", owner)
                            bundle.putString("issue_repo", repo)
                            bundle.putString("issue_number", issueNumber)
                            findNavController().navigate(dest, bundle)
                        },
                        onIssuesStateChanged = { index, issuesType, issueState ->
                            val isOpen = when(issueState){
                                IssueState.Open -> true
                                IssueState.Closed -> false
                                IssueState.All -> true
                            }
                            homeViewModel.updateIssueScreen(index, isOpen)
                            homeViewModel.getIssuesWithCount(
                                token = token,
                                query = getUrlForIssues(
                                    issuesType,
                                    issueState,
                                    state.user.data!!.login
                                ),
                                page = 1,
                                issuesType = issuesType
                            )
                        },
                        onPullsStateChanged = { index, myIssuesType, issueState ->
                            val isOpen = when(issueState){
                                IssueState.Open -> true
                                IssueState.Closed -> false
                                IssueState.All -> true
                            }
                            homeViewModel.updatePullScreen(index, isOpen)
                            homeViewModel.getPullsWithCount(
                                token = token,
                                query = getUrlForPulls(
                                    myIssuesType,
                                    issueState,
                                    state.user.data!!.login
                                ),
                                page = 1,
                                issuesType = myIssuesType
                            )
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

    private fun getUrlForPulls(
        issueType: MyIssuesType,
        issueState: IssueState,
        login: String
    ): String {
        when (issueType) {
            MyIssuesType.CREATED -> return RepoQueryProvider.getMyIssuesPullRequestQuery(
                login,
                issueState,
                true
            )

            MyIssuesType.ASSIGNED -> return RepoQueryProvider.getAssigned(login, issueState, true)
            MyIssuesType.MENTIONED -> return RepoQueryProvider.getMentioned(login, issueState, true)
            MyIssuesType.REVIEW -> return RepoQueryProvider.getReviewRequests(login, issueState)
            else -> {}
        }
        return RepoQueryProvider.getMyIssuesPullRequestQuery(login, issueState, false)
    }

    private fun getUrlForIssues(
        issueType: MyIssuesType,
        issueState: IssueState,
        login: String
    ): String {
        when (issueType) {
            MyIssuesType.CREATED -> return RepoQueryProvider.getMyIssuesPullRequestQuery(
                login,
                issueState,
                false
            )

            MyIssuesType.ASSIGNED -> return RepoQueryProvider.getAssigned(login, issueState, false)
            MyIssuesType.MENTIONED -> return RepoQueryProvider.getMentioned(
                login,
                issueState,
                false
            )

            MyIssuesType.PARTICIPATED -> return RepoQueryProvider.getParticipated(
                login,
                issueState,
                false
            )

            else -> {}
        }
        return RepoQueryProvider.getMyIssuesPullRequestQuery(login, issueState, false)
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainContent(
    state: HomeScreenState,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    onBottomBarItemSelected: (AppScreens) -> Unit,
    onNavigate: (Int, String?, String?) -> Unit,
    onIssueItemClicked: (Int, String, String, String) -> Unit,
    onIssuesStateChanged: (Int, MyIssuesType, IssueState) -> Unit,
    onPullsStateChanged: (Int, MyIssuesType, IssueState) -> Unit,
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
            bottomBar = {
                BottomNav(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(58.dp),
                    onBottomBarItemSelected = onBottomBarItemSelected,
                )
            },
        ) {

            when (state.selectedBottomBarItem) {
                AppScreens.Feeds -> FeedsScreen(
                    contentPaddingValues = it,
                    isRefreshing = isRefreshing,
                    pullRefreshState = pullRefreshState,
                    onNavigate = onNavigate,
                    events = state.events.collectAsLazyPagingItems()
                )

                AppScreens.Issues -> IssuesScreen(
                    contentPaddingValues = it,
                    state = state,
                    onIssueItemClicked = onIssueItemClicked,
                    onIssuesStateChanged = onIssuesStateChanged
                )

                AppScreens.PullRequests -> PullRequestScreen(
                    contentPaddingValues = it,
                    state = state,
                    onNavigate = onNavigate,
                    onPullsStateChanges = onPullsStateChanged
                )
            }
        }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedsScreen(
    contentPaddingValues: PaddingValues,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState,
    events: LazyPagingItems<ReceivedEventsModel>,
    onNavigate: (Int, String?, String?) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = events.loadState) {
        if (events.loadState.refresh is LoadState.Error) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
            Log.d(
                "ahi3646",
                "FeedsScreen: error - ${(events.loadState.refresh as LoadState.Error).error.message} "
            )
        }
    }

    when (events.loadState.refresh) {

        is LoadState.Loading -> {
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

        is LoadState.NotLoading -> {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                LazyColumn(
                    modifier = Modifier
                        .padding(contentPaddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(events) { eventItem ->
                        if (eventItem != null) {
                            ItemEventCard(eventItem, onNavigate)
                        }
                    }
                    item {
                        if (events.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator()
                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }

        is LoadState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Could not load data!")
            }
        }

    }

}

@Composable
private fun ItemEventCard(
    eventItem: ReceivedEventsModel,
    onNavigate: (Int, String?, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clickable {
                val uri = Uri.parse(eventItem.eventRepoUrl).lastPathSegment
                val parentUsername = Uri.parse(eventItem.eventRepoUrl).pathSegments[1]

                when (eventItem.eventType) {
                    "ForkEvent" -> {
                        onNavigate(
                            R.id.action_homeFragment_to_repositoryFragment,
                            eventItem.eventActorLogin,
                            eventItem.eventPayloadForkeeName,
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
                    eventItem.eventActorAvatarUrl
                }, // loading a network image using an URL.
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .size(48.dp, 48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onNavigate(
                            R.id.action_homeFragment_to_profileFragment,
                            eventItem.eventActorLogin,
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
                        append(eventItem.eventActorLogin)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(
                                " " + stringResource(id = chooseFromEvents(eventItem.eventType).action).lowercase(
                                    Locale.getDefault()
                                ) + " "
                            )
                        }
                        append(eventItem.eventRepoName)
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
                        painter = painterResource(id = chooseFromEvents(eventItem.eventType).icon),
                        contentDescription = stringResource(
                            id = chooseFromEvents(eventItem.eventType).action
                        ),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ParseDateFormat.getTimeAgo(eventItem.eventCreatedAt).toString(),
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

@Composable
fun IssuesScreen(
    contentPaddingValues: PaddingValues,
    state: HomeScreenState,
    onIssueItemClicked: (Int, String, String, String) -> Unit,
    onIssuesStateChanged: (Int, MyIssuesType, IssueState) -> Unit
) {

    when (state.issuesCreated) {

        is Resource.Loading -> {
            var tabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("CREATED", "ASSIGNED", "MENTIONED", "PARTICIPATED")

            Column(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = tabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                if (tabIndex == index) {
                                    Text(
                                        title,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(title, color = MaterialTheme.colorScheme.outline)
                                }
                            },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }

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
        }

        is Resource.Success -> {
            var tabIndex by remember { mutableIntStateOf(0) }
            val issuesCreatedData = state.issuesCreated.data!!
            val issuesAssignedData = state.issuesAssigned.data!!
            val issuesMentionedData = state.issuesMentioned.data!!
            val issuesParticipatedData = state.issuesParticipated.data!!

            Column(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CompositionLocalProvider(LocalRippleTheme provides RippleCustomTheme) {
                    ScrollableTabRow(
                        selectedTabIndex = tabIndex
                    ) {
                        Tab(
                            selected = tabIndex == 0,
                            onClick = { tabIndex = 0 }
                        ) {
                            TabItem(
                                issuesCount = issuesCreatedData.total_count ?: 0,
                                tabIndex = tabIndex,
                                index = 0,
                                isOpened = state.issueScreenState[0],
                                tabName = "CREATED",
                                onItemClick = {
                                    tabIndex = 0
                                },
                                onStateChanged = { index, state ->
                                    onIssuesStateChanged(index, MyIssuesType.CREATED, state)
                                },
                            )
                        }

                        Tab(
                            selected = tabIndex == 0,
                            onClick = { tabIndex = 0 }
                        ) {
                            TabItem(
                                issuesCount = issuesAssignedData.total_count ?: 0,
                                tabIndex = tabIndex,
                                index = 1,
                                isOpened = state.issueScreenState[1],
                                tabName = "ASSIGNED",
                                onItemClick = {
                                    tabIndex = 1
                                },
                                onStateChanged = { index, state ->
                                    onIssuesStateChanged(index, MyIssuesType.ASSIGNED, state)
                                },
                            )
                        }

                        Tab(
                            selected = tabIndex == 0,
                            onClick = { tabIndex = 0 }
                        ) {
                            TabItem(
                                issuesCount = issuesMentionedData.total_count ?: 0,
                                tabIndex = tabIndex,
                                index = 2,
                                isOpened = state.issueScreenState[2],
                                tabName = "MENTIONED",
                                onItemClick = {
                                    tabIndex = 2
                                },
                                onStateChanged = { index, state ->
                                    onIssuesStateChanged(index, MyIssuesType.MENTIONED, state)
                                    Log.d("ahi3646", "IssuesScreen: $state ")
                                },
                            )
                        }

                        Tab(
                            selected = tabIndex == 0,
                            onClick = { tabIndex = 0 }
                        ) {
                            TabItem(
                                issuesCount = issuesParticipatedData.total_count ?: 0,
                                tabIndex = tabIndex,
                                index = 3,
                                isOpened = state.issueScreenState[3],
                                tabName = "PARTICIPATED",
                                onItemClick = {
                                    tabIndex = 3
                                },
                                onStateChanged = { index, state ->
                                    onIssuesStateChanged(index, MyIssuesType.PARTICIPATED, state)
                                    Log.d("ahi3646", "IssuesScreen: $state ")
                                },
                            )
                        }

                    }
                }

                when (tabIndex) {
                    0 -> IssuesCreated(issuesCreatedData, onIssueItemClicked)
                    1 -> IssuesAssigned(issuesAssignedData, onIssueItemClicked)
                    2 -> IssuesMentioned(issuesMentionedData, onIssueItemClicked)
                    3 -> IssuesParticipated(issuesParticipatedData, onIssueItemClicked)
                }
            }
        }

        is Resource.Failure -> {
            var tabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("CREATED", "ASSIGNED", "MENTIONED", "PARTICIPATED")

            Column(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                ScrollableTabRow(
                    selectedTabIndex = tabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                if (tabIndex == index) {
                                    Text(
                                        title,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(title, color = MaterialTheme.colorScheme.outline)
                                }
                            },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Can't load data!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

    }
}

@Composable
fun IssuesItem(
    issue: IssuesItem,
    onIssueItemClicked: (Int, String, String, String) -> Unit
) {
    val repoUrl = Uri.parse(issue.repository_url).pathSegments
    val repoName = repoUrl[repoUrl.lastIndex - 1] + "/" + repoUrl[repoUrl.lastIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onIssueItemClicked(
                        R.id.action_fromFragment_to_issueFragment,
                        repoUrl[repoUrl.lastIndex - 1],
                        repoUrl[repoUrl.lastIndex],
                        issue.number.toString()
                    )
                }
            )
            .padding(8.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = issue.title,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = buildAnnotatedString {
                            append(repoName)
                            append("#${issue.number}")
                            append(" ")
                            append(issue.state)
                            append(" ")
                            if (issue.closed_at != null) {
                                append(
                                    ParseDateFormat.getTimeAgo(issue.closed_at.toString())
                                        .toString()
                                )
                            }
                        },
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.weight(1F),
                        fontSize = 12.sp
                    )

                    if (issue.comments != 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment_small),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = issue.comments.toString(),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun TabItem(
    issuesCount: Int,
    tabIndex: Int,
    index: Int,
    isOpened: Boolean,
    tabName: String,
    onItemClick: () -> Unit,
    onStateChanged: (Int, IssueState) -> Unit,
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

//    var isOpened by rememberSaveable {
//        mutableStateOf(true)
//    }

    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Column(
        modifier = Modifier.onSizeChanged { itemHeight = with(density) { it.height.toDp() } }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick()
                    isContextMenuVisible = !isContextMenuVisible
                }
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
//                use this to handle long click, swipe, double tap and many other
//                .pointerInput( -- your state -- ){}
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_issue_opened_small),
                    contentDescription = null,
                    tint = if (isOpened) colorResource(id = R.color.material_green_700) else colorResource(
                        id = R.color.material_red_700
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "$tabName ($issuesCount)",
                    color = if (tabIndex == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown_icon),
                    contentDescription = null,
                    tint = if (tabIndex == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline
                )

            }

            DropdownMenu(
                expanded = isContextMenuVisible,
                onDismissRequest = { isContextMenuVisible = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onStateChanged(index, IssueState.Open)
                        //isOpened = true
                        isContextMenuVisible = false
                    }
                ) {
                    Text(text = "Opened", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                DropdownMenuItem(
                    onClick = {
                        onStateChanged(index, IssueState.Closed)
                        //isOpened = false
                        isContextMenuVisible = false
                    }
                ) {
                    Text(text = "Closed", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

        }
    }

}

@Composable
private fun IssuesCreated(
    issuesModel: IssuesModel,
    onIssueItemClicked: (Int, String, String, String) -> Unit
) {
    val issues = issuesModel.items
    if (issues.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(issues) { issue ->
                IssuesItem(issue, onIssueItemClicked)
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
            Text(text = "No issues", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun IssuesAssigned(
    issuesModel: IssuesModel,
    onIssueItemClicked: (Int, String, String, String) -> Unit
) {
    val issues = issuesModel.items
    if (issues.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(issues) { issue ->
                IssuesItem(issue, onIssueItemClicked)
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
            Text(text = "No issues", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun IssuesMentioned(
    issuesModel: IssuesModel,
    onIssueItemClicked: (Int, String, String, String) -> Unit
) {
    val issues = issuesModel.items
    if (issues.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(issues) { issue ->
                IssuesItem(issue, onIssueItemClicked)
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
            Text(text = "No issues", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun IssuesParticipated(
    issuesModel: IssuesModel,
    onIssueItemClicked: (Int, String, String, String) -> Unit
) {
    val issues = issuesModel.items
    if (issues.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(issues) { issue ->
                IssuesItem(issue, onIssueItemClicked)
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
            Text(text = "No issues, color = MaterialTheme.colorScheme.onSurfaceVariant")
        }
    }
}


@Composable
fun PullRequestScreen(
    contentPaddingValues: PaddingValues,
    state: HomeScreenState,
    onNavigate: (Int, String?, String?) -> Unit,
    onPullsStateChanges: (Int, MyIssuesType, IssueState) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val pullsCreated = state.pullsCreated.data?.total_count ?: 0
    val pullsAssigned = state.pullsAssigned.data?.total_count ?: 0
    val pullsMentioned = state.pullsMentioned.data?.total_count ?: 0
    val pullsReview = state.pullsReview.data?.total_count ?: 0

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        ScrollableTabRow(
            selectedTabIndex = tabIndex
        ) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 }
            ) {
                TabItem(
                    issuesCount = pullsCreated,
                    tabIndex = tabIndex,
                    index = 0,
                    tabName = "CREATED",
                    isOpened = state.pullScreenState[0],
                    onItemClick = {
                        tabIndex = 0
                    },
                    onStateChanged = { index, state ->
                        onPullsStateChanges(index, MyIssuesType.CREATED, state)
                    },
                )
            }

            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 }
            ) {
                TabItem(
                    issuesCount = pullsAssigned,
                    tabIndex = tabIndex,
                    index = 1,
                    tabName = "ASSIGNED",
                    isOpened = state.pullScreenState[1],
                    onItemClick = {
                        tabIndex = 1
                    },
                    onStateChanged = { index, state ->
                        onPullsStateChanges(index, MyIssuesType.ASSIGNED, state)
                    },
                )
            }

            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 }
            ) {
                TabItem(
                    issuesCount = pullsMentioned,
                    tabIndex = tabIndex,
                    index = 2,
                    tabName = "MENTIONED",
                    isOpened = state.pullScreenState[2],
                    onItemClick = {
                        tabIndex = 2
                    },
                    onStateChanged = { index, state ->
                        onPullsStateChanges(index, MyIssuesType.MENTIONED, state)
                        Log.d("ahi3646", "IssuesScreen: $state ")
                    },
                )
            }

            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 }
            ) {
                TabItem(
                    issuesCount = pullsReview,
                    tabIndex = tabIndex,
                    index = 3,
                    tabName = "REVIEW REQUESTS",
                    isOpened = state.pullScreenState[3],
                    onItemClick = {
                        tabIndex = 3
                    },
                    onStateChanged = { index, state ->
                        onPullsStateChanges(index, MyIssuesType.REVIEW, state)
                        Log.d("ahi3646", "IssuesScreen: $state ")
                    },
                )
            }
        }

        when (tabIndex) {
            0 -> PullRequestsCreated(state.pullsCreated, onNavigate)
            1 -> PullRequestsAssigned(state.pullsAssigned, onNavigate)
            2 -> PullRequestsMentioned(state.pullsMentioned, onNavigate)
            3 -> PullRequestsReviewedRequests(state.pullsReview, onNavigate)
        }
    }
}

@Composable
private fun PullRequestsCreated(
    data: Resource<IssuesModel>,
    onNavigate: (Int, String?, String?) -> Unit
) {
    when (data) {
        is Resource.Loading -> {
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

        is Resource.Success -> {
            val pulls = data.data!!.items
            if (pulls.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(pulls) { pull ->
                        PullsItem(pull, onNavigate)
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
                    Text(text = "No pulls", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Can't load data!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PullRequestsAssigned(
    issuesModel: Resource<IssuesModel>,
    onNavigate: (Int, String?, String?) -> Unit
) {
    when (issuesModel) {
        is Resource.Loading -> {
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

        is Resource.Success -> {
            val pulls = issuesModel.data!!.items
            if (pulls.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(pulls) { pull ->
                        PullsItem(pull, onNavigate)
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
                    Text(text = "No pulls", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Can't load data!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PullRequestsMentioned(
    issuesModel: Resource<IssuesModel>,
    onNavigate: (Int, String?, String?) -> Unit
) {
    when (issuesModel) {
        is Resource.Loading -> {
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

        is Resource.Success -> {
            val pulls = issuesModel.data!!.items
            if (pulls.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(pulls) { pull ->
                        PullsItem(pull, onNavigate)
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
                    Text(text = "No pulls", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Can't load data!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PullRequestsReviewedRequests(
    issuesModel: Resource<IssuesModel>,
    onNavigate: (Int, String?, String?) -> Unit
) {
    when (issuesModel) {
        is Resource.Loading -> {
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

        is Resource.Success -> {
            val pulls = issuesModel.data!!.items
            if (pulls.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(pulls) { pull ->
                        PullsItem(pull, onNavigate)
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
                    Text(text = "No pulls", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Can't load data!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PullsItem(
    pull: IssuesItem,
    onNavigate: (Int, String?, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onNavigate(R.id.action_fromFragment_to_issueFragment, null, null)
                }
            )
            .padding(8.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Text(
                text = pull.title,
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            val repoUrl = Uri.parse(pull.repository_url).pathSegments
            val repoName = repoUrl[repoUrl.lastIndex - 1] + "/" + repoUrl[repoUrl.lastIndex]

            Row {
                Text(
                    text = buildAnnotatedString {
                        append(repoName)
                        append("#${pull.number}")
                        append(" ")
                        if (pull.state == "closed") {
                            append(pull.state)
                            append(ParseDateFormat.getTimeAgo(pull.closed_at.toString()).toString())
                        } else {
                            append("${pull.state}ed")
                            append(ParseDateFormat.getTimeAgo(pull.created_at).toString())
                        }
                    },
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1F),
                    fontSize = 12.sp
                )
            }
        }
    }
}

