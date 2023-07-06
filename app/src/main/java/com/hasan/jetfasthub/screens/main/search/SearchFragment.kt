package com.hasan.jetfasthub.screens.main.search

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val token = PreferenceHelper.getToken(requireContext())
        Log.d("ahi3646", "onCreateView: token - $token")

        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onListItemClick = { stringResource ->
                            Toast.makeText(requireContext(), stringResource, Toast.LENGTH_SHORT)
                                .show()
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
                        onBackPressed = { dest -> findNavController().navigate(dest) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    state: SearchScreenState,
    onListItemClick: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onBackPressed: (Int) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                content = {
                    TopAppBarContent(
                        onBackPressed = onBackPressed, onSearchItemClick = onSearchClick
                    )
                },
            )
        },
    ) { contentPadding ->
        TabScreen(contentPadding, onListItemClick, state)
    }
}

@Composable
fun TabScreen(
    contentPaddingValues: PaddingValues,
    onListItemClick: (String) -> Unit,
    state: SearchScreenState
) {
    val tabs = listOf("REPOSITORIES", "USERS", "ISSUES", "CODE")
    var tabIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxWidth()
    ) {
        ScrollableTabRow(selectedTabIndex = tabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        when (index) {
                            0 -> {
                                val count = state.Repositories.data?.total_count
                                Log.d("ahi3646gg", "TabScreen: $count")
                                if (count != null)
                                    Text("$title ($count)")
                                else Text(title)
                            }

                            1 -> {
                                val count = state.Users.data?.total_count
                                Log.d("ahi3646gg", "TabScreen: $count")
                                if (count != null)
                                    Text("$title ($count)")
                                else Text(title)
                            }

                            2 -> {
                                val count = state.Issues.data?.total_count
                                Log.d("ahi3646gg", "TabScreen: $count")
                                if (count != null)
                                    Text("$title ($count)")
                                else Text(title)
                            }

                            3 -> {
                                val count = state.Codes.data?.total_count
                                Log.d("ahi3646gg", "TabScreen: $count")
                                if (count != null)
                                    Text("$title ($count)")
                                else Text(title)
                            }
                        }

                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            0 -> RepositoriesContent(contentPaddingValues, state.Repositories, onListItemClick)
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
fun RepositoriesContent(
    contentPaddingValues: PaddingValues,
    repositories: Resource<RepositoryModel>,
    onNavigate: (String) -> Unit
) {
    when (repositories) {
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
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(repositories.data!!.items) { repository ->
                    RepositoryItem(repository, onNavigate)
                }
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
                Log.d("ahi3646", "Unread: ${repositories.errorMessage}")
            }
        }
    }
}

@Composable
private fun RepositoryItem(
    repository: Item, onItemClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(repository.full_name)
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
                    repository.owner.avatar_url
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
                    text = repository.full_name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.subtitle1,
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
                        contentDescription = "star icon"
                    )

                    Text(
                        text = repository.stargazers_count.toString(),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_fork_small),
                        contentDescription = "star icon"
                    )

                    Text(
                        text = repository.forks_count.toString(),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time_small),
                        contentDescription = "time icon"
                    )

                    Text(
                        text = ParseDateFormat.getTimeAgo(repository.updated_at).toString(),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_storage_small),
                        contentDescription = "storage icon"
                    )

                    Text(
                        text = FileSizeCalculator.humanReadableByteCountBin(repository.size.toLong()),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Text(
                        text = repository.language ?: "",
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UsersContent(
    users: Resource<UserModel>,
    contentPaddingValues: PaddingValues,
    onUsersItemClicked: (String) -> Unit
) {
    when (users) {
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
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(users.data!!.items) { user ->
                    UsersItem(user, onUsersItemClicked)
                }
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
                Log.d("ahi3646", "Unread: ${users.errorMessage}")
            }
        }
    }
}

@Composable
fun UsersItem(
    userModel: UsersItem, onUsersItemClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onUsersItemClicked(userModel.login)
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
                    color = Color.Black,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun IssuesContent(
    issues: Resource<IssuesModel>,
    contentPaddingValues: PaddingValues,
    onIssueItemClicked: (String) -> Unit
) {
    when (issues) {
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
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(issues.data!!.items) { issue ->
                    IssuesItem(issue, onIssueItemClicked)
                }
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
                Log.d("ahi3646", "Unread: ${issues.errorMessage}")
            }
        }
    }
}


@Composable
fun IssuesItem(
    issuesItem: IssuesItem, onIssueItemClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onIssueItemClicked(issuesItem.title)
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
                    color = Color.Black,
                    style = MaterialTheme.typography.subtitle1,
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
                        color = Color.Black,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1F)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (issuesItem.comments != 0 && issuesItem.comments > 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment_small),
                            contentDescription = "storage icon"
                        )

                        Text(
                            text = issuesItem.comments.toString(),
                            color = Color.Black,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeContent(
    codes: Resource<CodeModel>,
    contentPaddingValues: PaddingValues,
    onCodeItemClicked: (String) -> Unit
) {
    when (codes) {
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
            LazyColumn(
                modifier = Modifier
                    .padding(contentPaddingValues)
                    .fillMaxSize()
                    .background(Color.White),
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

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
                Log.d("ahi3646", "Unread: ${codes.errorMessage}")
            }
        }
    }
}

@Composable
fun CodesItem(
    code: CodeItem, onCodeItemClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .clickable(onClick = {
                onCodeItemClicked(code.name)
            })
            .padding(4.dp), elevation = 0.dp, backgroundColor = Color.White
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
                color = Color.Black,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = code.name,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    onSearchItemClick: (String) -> Unit,
    onBackPressed: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            onBackPressed(R.id.action_searchFragment_to_homeFragment)
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
        }

        var text by remember { mutableStateOf("") }

        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            textStyle = TextStyle(fontSize = 16.sp),
            label = null,
            placeholder = { Text(text = "Search", fontSize = 16.sp) },
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
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            maxLines = 1,
            modifier = Modifier.weight(1F)
        )

        IconButton(onClick = { onSearchItemClick(text) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon"
            )
        }
    }
}


