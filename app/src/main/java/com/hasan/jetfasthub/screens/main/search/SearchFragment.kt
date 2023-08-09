package com.hasan.jetfasthub.screens.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeItem
import com.hasan.jetfasthub.screens.main.search.models.code_model.CodeModel
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesItem
import com.hasan.jetfasthub.screens.main.search.models.issues_model.IssuesModel
import com.hasan.jetfasthub.screens.main.search.models.repository_model.Item
import com.hasan.jetfasthub.screens.main.search.models.repository_model.RepositoryModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UserModel
import com.hasan.jetfasthub.screens.main.search.models.users_model.UsersItem
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.ui.theme.RippleCustomTheme
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var token: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        token = PreferenceHelper.getToken(requireContext())
        val query = arguments?.getString("repo_topic")

        if (query != null && query != "") {
            val initialQuery = "topic:\"$query\""
            viewModel.setInitialQuery(initialQuery)
            viewModel.searchRepositories(token, initialQuery, 1L)
            viewModel.searchUsers(token, initialQuery, 1L)
            viewModel.searchIssues(token, initialQuery, 1L)
            viewModel.searchCodes(token, initialQuery, 1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onListItemClick = { dest, data, extra ->
                            when (dest) {
                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                R.id.action_searchFragment_to_profileFragment -> {
                                    val bundle = bundleOf("username" to data)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_searchFragment_to_repositoryFragment -> {
                                    val bundle = Bundle()
                                    bundle.putString("repository_owner", data)
                                    bundle.putString("repository_name", extra)
                                    findNavController().navigate(dest, bundle)
                                }

                            }
                        },
                        onSearchClick = { query ->
                            if (query.isEmpty() || query.length < 2)
                                Toast.makeText(
                                    requireContext(),
                                    "Enter minimum 2 characters !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else {
                                viewModel.searchRepositories(token, query, 1L)
                                viewModel.searchUsers(token, query, 1L)
                                viewModel.searchIssues(token, query, 1L)
                                viewModel.searchCodes(token, query, 1L)
                            }
                        },
                        onBackPressed = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    state: SearchScreenState,
    onListItemClick: (Int, String, String?) -> Unit,
    onSearchClick: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CompositionLocalProvider(LocalRippleTheme provides RippleCustomTheme) {
                TopAppBar(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    elevation = 0.dp,
                    content = {
                        TopAppBarContent(
                            initialQuery = state.InitialQuery,
                            onBackPressed = onBackPressed,
                            onSearchItemClick = onSearchClick
                        )
                    },
                )
            }
        },
    ) { contentPadding ->
        TabScreen(contentPadding, onListItemClick, state)
    }
}

@Composable
private fun TabScreen(
    contentPaddingValues: PaddingValues,
    onListItemClick: (Int, String, String?) -> Unit,
    state: SearchScreenState
) {
    val tabs = listOf("REPOSITORIES", "USERS", "ISSUES", "CODE")
    var tabIndex by remember { mutableIntStateOf(0) }
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
                Tab(
                    text = {
                        when (index) {
                            0 -> {
                                val count = state.Repositories.data?.total_count
                                val tabName = if (count != null) {
                                    "$title ($count)"
                                } else {
                                    title
                                }
                                if (tabIndex == 0) {
                                    Text(
                                        tabName,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(tabName, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            1 -> {
                                val count = state.Users.data?.total_count
                                val tabName = if (count != null)
                                    "$title ($count)"
                                else title

                                if (tabIndex == 1) {
                                    Text(
                                        tabName,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(tabName, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            2 -> {
                                val count = state.Issues.data?.total_count
                                val tabName = if (count != null)
                                    "$title ($count)"
                                else title

                                if (tabIndex == 2) {
                                    Text(
                                        tabName,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(tabName, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            3 -> {
                                val count = state.Codes.data?.total_count
                                Log.d("ahi3646gg", "TabScreen 3 : $tabIndex")
                                val tabName = if (count != null)
                                    "$title ($count)"
                                else title

                                if (tabIndex == 3) {
                                    Text(
                                        tabName,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(tabName, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }

                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
        when (tabIndex) {
            0 -> RepositoriesContent(
                contentPaddingValues = contentPaddingValues,
                repositories = state.Repositories,
                onNavigate = onListItemClick
            )

            1 -> UsersContent(
                contentPaddingValues = contentPaddingValues,
                onUsersItemClicked = onListItemClick,
                users = state.Users
            )

            2 -> IssuesContent(
                contentPaddingValues = contentPaddingValues,
                onIssueItemClicked = onListItemClick,
                issues = state.Issues
            )

            3 -> CodeContent(
                contentPaddingValues = contentPaddingValues,
                onCodeItemClicked = onListItemClick,
                codes = state.Codes
            )
        }
    }
}

@Composable
private fun RepositoriesContent(
    contentPaddingValues: PaddingValues,
    repositories: ResourceWithInitial<RepositoryModel>,
    onNavigate: (Int, String, String?) -> Unit
) {
    when (repositories) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(repositories.data!!.items) { repository ->
                    RepositoryItem(repository, onNavigate)
                }
            }
        }

        is ResourceWithInitial.Failure -> {
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
private fun RepositoryItem(
    repository: Item, onItemClicked: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(
                    R.id.action_searchFragment_to_repositoryFragment,
                    repository.owner.login,
                    repository.name
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
                    repository.owner.avatar_url
                }, // loading a network image using an URL.
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .size(48.dp, 48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onItemClicked(
                            R.id.action_searchFragment_to_profileFragment,
                            repository.owner.login,
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

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = repository.full_name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ///style = MaterialTheme.typography.,
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
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = repository.stargazers_count.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_fork_small),
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = repository.forks_count.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_small),
                        contentDescription = "time icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = ParseDateFormat.getTimeAgo(repository.updated_at).toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_storage_small),
                        contentDescription = "storage icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = FileSizeCalculator.humanReadableByteCountBin(repository.size.toLong()),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Text(
                        text = repository.language ?: "",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UsersContent(
    users: ResourceWithInitial<UserModel>,
    contentPaddingValues: PaddingValues,
    onUsersItemClicked: (Int, String, String?) -> Unit
) {
    when (users) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(users.data!!.items) { user ->
                    UsersItem(user, onUsersItemClicked)
                }
            }
        }

        is ResourceWithInitial.Failure -> {
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
private fun UsersItem(
    userModel: UsersItem, onUsersItemClicked: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onUsersItemClicked(
                        R.id.action_searchFragment_to_profileFragment,
                        userModel.login,
                        null
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
            GlideImage(
                failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                imageModel = {
                    userModel.avatar_url
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
                    text = userModel.login,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    //style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun IssuesContent(
    issues: ResourceWithInitial<IssuesModel>,
    contentPaddingValues: PaddingValues,
    onIssueItemClicked: (Int, String, String?) -> Unit
) {
    when (issues) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(issues.data!!.items) { issue ->
                    IssuesItem(issue, onIssueItemClicked)
                }
            }
        }

        is ResourceWithInitial.Failure -> {
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


@Composable
private fun IssuesItem(
    issuesItem: IssuesItem, onIssueItemClicked: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onIssueItemClicked(
                    0,
                    issuesItem.title,
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
                    if (issuesItem.state == "open") {
                        R.drawable.ic_issue_opened_small
                    } else {
                        R.drawable.ic_issue_closed_small
                    }
                }, // loading a network image using an URL.
                modifier = Modifier
                    .size(24.dp, 24.dp)
                    .size(24.dp, 24.dp)
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
                    text = issuesItem.title,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    //style = MaterialTheme.typography.subtitle1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(issuesItem.user.login + "/")
                            append(issuesItem.title + "#")
                            append(issuesItem.number.toString())
                        },
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1F)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (issuesItem.comments != 0 && issuesItem.comments > 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment_small),
                            contentDescription = "storage icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = issuesItem.comments.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeContent(
    codes: ResourceWithInitial<CodeModel>,
    contentPaddingValues: PaddingValues,
    onCodeItemClicked: (Int, String, String?) -> Unit
) {
    when (codes) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(codes.data!!.items) { index, code ->
                    CodesItem(code, onCodeItemClicked)
                    if (index < codes.data.items.lastIndex) {
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                        )
                    }
                }
            }
        }

        is ResourceWithInitial.Failure -> {
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
private fun CodesItem(
    code: CodeItem, onCodeItemClicked: (Int, String, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .clickable(
                onClick = {
                    onCodeItemClicked(
                        0,
                        code.name,
                        null
                    )
                }
            )
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = code.repository.full_name,
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                //style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = code.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
private fun TopAppBarContent(
    initialQuery: String,
    onSearchItemClick: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var text by remember { mutableStateOf(initialQuery) }

        IconButton(onClick = { onBackPressed() }) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back button",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            textStyle = TextStyle(fontSize = 16.sp),
            label = null,
            placeholder = {
                Text(
                    text = "Search here ...",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            trailingIcon = {
                if (text != "") {
                    IconButton(onClick = { text = "" }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "clear text icon"
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            maxLines = 1,
            modifier = Modifier.weight(1F)
        )

        IconButton(onClick = { onSearchItemClick(text) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

