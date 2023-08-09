package com.hasan.jetfasthub.screens.main.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEvents
import com.hasan.jetfasthub.screens.main.profile.model.event_model.UserEventsItem
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModel
import com.hasan.jetfasthub.screens.main.profile.model.followers_model.FollowersModelItem
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModel
import com.hasan.jetfasthub.screens.main.profile.model.following_model.FollowingModelItem
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistsModel
import com.hasan.jetfasthub.screens.main.profile.model.gist_model.GistModelItem
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModel
import com.hasan.jetfasthub.screens.main.profile.model.org_model.OrgModelItem
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.RepositoryModelItem
import com.hasan.jetfasthub.screens.main.profile.model.repo_model.UserRepositoryModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModel
import com.hasan.jetfasthub.screens.main.profile.model.starred_repo_model.StarredRepoModelItem
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Constants.chooseFromEvents
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var token: String
    private var startIndex: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        token = PreferenceHelper.getToken(requireContext())
        val authUser = PreferenceHelper.getAuthenticatedUsername(context)

        val username = arguments?.getString("username")
        val extra = arguments?.getString("start_index") ?: "0"

        if (username != null) {

            profileViewModel.setUserName(username, authUser)

            profileViewModel.getUser(token, username)
            profileViewModel.getUserOrganisations(token, username)
            profileViewModel.getUserEvents(token, username)
            profileViewModel.getUserRepositories(token, username)
            profileViewModel.getUserStarredRepos(token, username, 1)
            profileViewModel.getUserFollowings(token, username, 1)
            profileViewModel.getUserFollowers(token, username, 1)
            profileViewModel.getUserGists(token, username, 1)
            profileViewModel.getFollowStatus(token, username)

            startIndex = try {
                extra.toInt()
            } catch (e: Exception) {
                0
            }

        } else {
            Toast.makeText(context, "Can't identify a user!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                val state by profileViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        startIndex = startIndex,
                        state = state,
                        onAction = { action, data ->
                            when (action) {
                                "share" -> {
                                    val context = requireContext()
                                    val type = "text/plain"
                                    val subject = "Your subject"
                                    val shareWith = "ShareWith"

                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = type
                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                                    intent.putExtra(Intent.EXTRA_TEXT, "https://github.com/$data")

                                    ContextCompat.startActivity(
                                        context, Intent.createChooser(intent, shareWith), null
                                    )
                                }

                                "browser" -> {
                                    var webpage = Uri.parse(data)

                                    if (!data.startsWith("http://") && !data.startsWith("https://")) {
                                        webpage = Uri.parse("http://$data")
                                    }
                                    val urlIntent = Intent(
                                        Intent.ACTION_VIEW, webpage
                                    )
                                    requireContext().startActivity(urlIntent)
                                }

                                "isUserBlocked" -> {
                                    profileViewModel.isUserBlocked(token, state.Username)
                                }

                                "block" -> {
                                    profileViewModel.blockUser(token, state.Username)
                                        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                        .onEach {
                                            if (it) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Blocked",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Unable to process action",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }.launchIn(lifecycleScope)
                                }

                                "unblock" -> {
                                    profileViewModel.unblockUser(token, state.Username)
                                        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                        .onEach {
                                            if (it) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Unblocked",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Unable to process action",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }.launchIn(lifecycleScope)
                                }
                            }
                        },
                        onNavigate = { dest, data, extra ->
                            when (dest) {
                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                R.id.action_profileFragment_to_gistFragment -> {
                                    val bundle = Bundle()
                                    bundle.putString("gist_id", data)
                                    bundle.putString("gist_owner", extra)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_profileFragment_to_repositoryFragment -> {
                                    val bundle = Bundle()
                                    bundle.putString("repository_name", data)
                                    bundle.putString("repository_owner", extra)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_profileFragment_self -> {
                                    val bundle = bundleOf("username" to data)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_profileFragment_to_organisationsFragment -> {
                                    val bundle = bundleOf("organisation" to data)
                                    findNavController().navigate(dest, bundle)
                                }
                            }
                        },
                        onFollowClicked = {
                            profileViewModel.followUser(token, state.Username)
                                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
                                    if (it) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Followed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Unable to process action",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.launchIn(lifecycleScope)
                        },
                        onUnfollowClicked = {
                            profileViewModel.unfollowUser(token, state.Username)
                                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
                                    if (it) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Unfollowed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Unable to process action",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.launchIn(lifecycleScope)
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    startIndex: Int,
    state: ProfileScreenState,
    onAction: (String, String) -> Unit,
    onNavigate: (Int, String?, String?) -> Unit,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        text = state.Username,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate(-1, null, null)
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onAction("share", state.Username)
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }

                    if (!state.isMe()) {
                        IconButton(onClick = {
                            onAction("isUserBlocked", state.Username)
                            showMenu = !showMenu
                        }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "more option")
                        }
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {

                        if (state.isUserBlocked) {
                            DropdownMenuItem(onClick = {
                                onAction("unblock", state.Username)
                                showMenu = false
                            }) {
                                Text(
                                    text = "Unblock",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            DropdownMenuItem(onClick = {
                                onAction("block", state.Username)
                                showMenu = false
                            }) {
                                Text(
                                    text = "Block",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                    }
                }
            )
        },
    ) { contentPadding ->
        TabScreen(
            startIndex = startIndex,
            contentPaddingValues = contentPadding,
            state = state,
            onAction = onAction,
            onNavigate = onNavigate,
            onFollowClicked = onFollowClicked,
            onUnfollowClicked = onUnfollowClicked
        )
    }
}

@Composable
fun TabScreen(
    startIndex: Int,
    contentPaddingValues: PaddingValues,
    state: ProfileScreenState,
    onAction: (String, String) -> Unit,
    onNavigate: (Int, String?, String?) -> Unit,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
) {

    var tabIndex by remember { mutableIntStateOf(startIndex) }
    val tabs =
        listOf("OVERVIEW", "FEED", "REPOSITORIES", "STARRED", "GISTS", "FOLLOWERS", "FOLLOWING")

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            tabs.forEachIndexed { index, title ->
                if (title == "STARRED") {
                    val count = state.UserStarredRepositories.data?.size.toString()
                    val tabName =
                        if (count.isNotEmpty() || count != "null") "$title ($count)" else title
                    Tab(
                        text = {
                            if (tabIndex == index) {
                                Text(tabName, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            } else {
                                Text(tabName, color = MaterialTheme.colorScheme.outline)
                            }
                        },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                    )
                } else {
                    Tab(
                        text = {
                            if (tabIndex == index) {
                                Text(title, color = MaterialTheme.colorScheme.onPrimaryContainer)
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
        }

        when (tabIndex) {
            0 -> OverviewScreen(
                state = state,
                isFollowing = state.isFollowing,
                organisation = state.UserOrganisations,
                onFollowClicked = onFollowClicked,
                onUnfollowClicked = onUnfollowClicked,
                onTabChange = { index -> tabIndex = index },
                onNavigate = onNavigate,
                onAction = onAction
            )

            1 -> FeedScreen(
                state.UserEvents, onNavigate = onNavigate
            )

            2 -> RepositoriesScreen(
                state.UserRepositories, onNavigate = onNavigate
            )

            3 -> StarredScreen(
                state.UserStarredRepositories, onNavigate = onNavigate
            )

            4 -> GistsScreen(
                state.UserGists, onNavigate = onNavigate
            )

            5 -> FollowersScreen(
                state.UserFollowers, onNavigate = onNavigate
            )

            6 -> FollowingScreen(
                state.UserFollowings, onNavigate = onNavigate
            )

        }
    }

}

@Composable
fun OverviewScreen(
    state: ProfileScreenState,
    isFollowing: Boolean,
    organisation: Resource<OrgModel>,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onTabChange: (Int) -> Unit,
    onNavigate: (Int, String?, String?) -> Unit,
    onAction: (String, String) -> Unit
) {

    when (state.OverviewScreenState) {
        is UserOverviewScreen.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "Loading ...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is UserOverviewScreen.Content -> {

            //var offset by remember { mutableFloatStateOf(0f) }
            val user = state.OverviewScreenState.user

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    GlideImage(
                        imageModel = {
                            user.avatar_url
                        }, // loading a network image using an URL.
                        modifier = Modifier
                            .size(80.dp, 80.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.CenterStart,
                            contentDescription = "Actor Avatar"
                        )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                        Text(
                            text = user.name ?: "",
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = androidx.compose.material.MaterialTheme.typography.subtitle1
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = user.login,
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = androidx.compose.material.MaterialTheme.typography.caption
                        )

                    }
                }

                if (user.bio != null) {
                    Text(
                        text = user.bio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Box(modifier = Modifier.clickable {
                        onTabChange(6)
                    }) {
                        Text(
                            text = "Following - ${user.following}",
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .height(16.dp)
                            .width(2.dp),
                    )

                    Box(modifier = Modifier.clickable {
                        onTabChange(5)
                    }) {
                        Text(
                            text = "Followers - ${user.followers}",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                if (!state.isMe()) {
                    if (isFollowing) {
                        Button(
                            onClick = { onUnfollowClicked() },
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.secondaryContainer),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_unfollow),
                                contentDescription = "unfollow button",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unfollow",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    } else {
                        Button(
                            onClick = { onFollowClicked() },
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_follow),
                                contentDescription = "follow button",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Follow",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                if (user.company != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = "Corporation",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = user.company,
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                    )
                }

                if (user.location != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Corporation",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )

                        Text(
                            text = user.location,
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                    )
                }

                if (user.email != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(text = user.email.toString(),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable(indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onAction("share", user.email.toString())
                                },
                            color = Color.Blue
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                    )
                }

                if (user.blog != null && user.blog.toString()
                        .isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_insert_link),
                            contentDescription = "Corporation",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(text = user.blog.toString(),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable(indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onAction("browser", user.blog.toString())
                                },

                            color = Color.Blue
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                    )
                }

                if (user.created_at != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "Corporation",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = ParseDateFormat.getTimeAgo(user.created_at)
                                .toString(),
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                when (organisation) {
                    is Resource.Success -> {
                        if (organisation.data!!.isNotEmpty()) {
                            Text(
                                text = "Organisations",
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.Start),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )

                            LazyHorizontalGrid(
                                rows = GridCells.Fixed(1),
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                horizontalArrangement = Arrangement.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                items(organisation.data) { organization ->
                                    OrganisationItem(organization, onNavigate)
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "Cannot load organisations!",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                /**
                Text(
                text = "Pinned",
                modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
                fontSize = 18.sp
                )

                LazyColumn(
                modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
                ) {
                itemsIndexed(arrayOf("a", "b")) { index, item ->

                }
                }

                Text(
                text = "Contributions",
                modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start),
                fontSize = 18.sp
                )
                 */

            }

        }

        is UserOverviewScreen.Error -> {
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
fun OrganisationItem(organisation: OrgModelItem, onNavigate: (Int, String?, String?) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                onNavigate(
                    R.id.action_profileFragment_to_organisationsFragment,
                    organisation.login,
                    null
                )
            }) {

        GlideImage(
            imageModel = { organisation.avatar_url }, // loading a network image using an URL.
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp, 48.dp)
                .clip(RoundedCornerShape(16.dp)),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterStart,
                contentDescription = "Actor Avatar"
            )
        )
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            text = organisation.login,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun FeedScreen(userEvents: Resource<UserEvents>, onNavigate: (Int, String, String?) -> Unit) {
    when (userEvents) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(userEvents.data!!) { index, UserEventsItem ->
                    FeedsItem(onFeedsItemClicked = onNavigate, UserEventsItem)
                    if (index < userEvents.data.lastIndex) {
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                        )
                    }
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
fun FeedsItem(
    onFeedsItemClicked: (Int, String, String?) -> Unit, userEventsItem: UserEventsItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onFeedsItemClicked(0, userEventsItem.actor.login, null)
            })
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = buildAnnotatedString {
                        append(userEventsItem.actor.login)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(
                                " " + stringResource(id = chooseFromEvents(userEventsItem.type).action).lowercase(
                                    Locale.getDefault()
                                ) + " "
                            )
                        }
                        append(userEventsItem.repo.name)
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (userEventsItem.payload.commits != null) {
                    Text(
                        text = userEventsItem.payload.commits.size.toString() + " commits",
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = androidx.compose.material.MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEventsItem.payload.commits[0].message,
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = androidx.compose.material.MaterialTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = chooseFromEvents("eventItem.type").icon),
                        contentDescription = stringResource(
                            id = chooseFromEvents(userEventsItem.type).action
                        ),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = ParseDateFormat.getTimeAgo(userEventsItem.created_at).toString(),
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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
fun RepositoriesScreen(
    userRepositories: Resource<UserRepositoryModel>, onNavigate: (Int, String, String?) -> Unit
) {
    when (userRepositories) {
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
            val repositories = userRepositories.data!!
            if (repositories.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(repositories) { index, UserEventsItem ->
                        RepositoryItem(
                            UserEventsItem, onRepositoryItemClicked = onNavigate
                        )
                        if (index < repositories.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
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
                    Text(
                        text = "No repositories found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
fun RepositoryItem(
    repository: RepositoryModelItem, onRepositoryItemClicked: (Int, String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onRepositoryItemClicked(
                        R.id.action_profileFragment_to_repositoryFragment,
                        repository.name,
                        repository.owner.login
                    )
                }
            )
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = buildAnnotatedString {
                        if (repository.fork) {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Blue, fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Forked / ")
                            }
                        }
                        append(repository.name)
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
                        painter = painterResource(id = R.drawable.ic_star_small),
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = repository.stargazers_count.toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_fork_small),
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = repository.forks_count.toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_small),
                        contentDescription = "time icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = ParseDateFormat.getTimeAgo(repository.updated_at).toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_storage_small),
                        contentDescription = "storage icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = FileSizeCalculator.humanReadableByteCountBin(repository.size.toLong()),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Text(
                        text = repository.language ?: "",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StarredScreen(
    userStarredRepoModel: Resource<StarredRepoModel>,
    onNavigate: (Int, String, String?) -> Unit
) {
    when (userStarredRepoModel) {
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
            val starredRepos = userStarredRepoModel.data!!

            if (starredRepos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(starredRepos) { index, StarredUserRepo ->
                        StarredRepositoryItem(
                            StarredUserRepo, onStarredRepositoryItemClicked = onNavigate
                        )
                        if (index < starredRepos.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
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
                    Text(
                        text = "No starred repositories found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
fun StarredRepositoryItem(
    repository: StarredRepoModelItem, onStarredRepositoryItemClicked: (Int, String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onStarredRepositoryItemClicked(
                    R.id.action_profileFragment_to_repositoryFragment,
                    repository.name,
                    repository.owner.login
                )
            })
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = buildAnnotatedString {
                        if (repository.fork) {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Blue, fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Forked / ")
                            }
                        }
                        append(repository.name)
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
                        painter = painterResource(id = R.drawable.ic_star_small),
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = repository.stargazers_count.toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_fork_small),
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = repository.forks_count.toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_small),
                        contentDescription = "time icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = ParseDateFormat.getTimeAgo(repository.updated_at).toString(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_storage_small),
                        contentDescription = "storage icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = FileSizeCalculator.humanReadableByteCountBin(repository.size.toLong()),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Text(
                        text = repository.language ?: "",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GistsScreen(userGists: Resource<GistsModel>, onNavigate: (Int, String, String?) -> Unit) {
    when (userGists) {
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
            val gists = userGists.data!!
            if (gists.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(gists) { index, gist ->
                        GistItemCard(
                            gistModelItem = gist,
                            onGistItemClick = onNavigate
                        )
                        if (index < gists.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
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
                    Text(
                        text = "No gists",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
fun GistItemCard(
    gistModelItem: GistModelItem,
    onGistItemClick: (Int, String, String?) -> Unit
) {

//    val fileKeys = gistModelItem.files.keys
    val fileValues = gistModelItem.files.values

    val fileName = if (gistModelItem.description == "" || gistModelItem.description == null) {
        fileValues.elementAt(0).filename
    } else {
        gistModelItem.description
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onGistItemClick(
                    R.id.action_profileFragment_to_gistFragment,
                    gistModelItem.id,
                    gistModelItem.owner.login
                )
            })
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)

        ) {
            Text(
                text = fileName,
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ParseDateFormat.getTimeAgo(gistModelItem.created_at).toString(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp)
            )

        }
    }
}

@Composable
fun FollowersScreen(
    userFollowers: Resource<FollowersModel>, onNavigate: (Int, String, String?) -> Unit
) {
    when (userFollowers) {
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
            val followers = userFollowers.data!!
            if (followers.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(followers) { index, StarredUserRepo ->
                        FollowersItemCard(
                            StarredUserRepo, onItemClicked = onNavigate
                        )
                        if (index < followers.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
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
                    Text(text = "No followers", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun FollowersItemCard(
    followersModelItem: FollowersModelItem, onItemClicked: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(
                    R.id.action_profileFragment_self,
                    followersModelItem.login,
                    null
                )
            })
            .padding(4.dp),
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
                    followersModelItem.avatar_url
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
                    text = followersModelItem.login,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun FollowingScreen(
    userFollowings: Resource<FollowingModel>, onNavigate: (Int, String, String?) -> Unit
) {
    when (userFollowings) {
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
            val followings = userFollowings.data!!
            if (followings.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(followings) { index, StarredUserRepo ->
                        FollowingsItemCard(
                            StarredUserRepo, onNavigate = onNavigate
                        )
                        if (index < followings.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
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
                    Text(text = "No followings", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun FollowingsItemCard(
    followingModelItem: FollowingModelItem, onNavigate: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onNavigate(
                    R.id.action_profileFragment_self,
                    followingModelItem.login,
                    null
                )
            })
            .padding(4.dp),
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
                    followingModelItem.avatar_url
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
                    text = followingModelItem.login,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}