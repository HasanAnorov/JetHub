package com.hasan.jetfasthub.screens.main.search_topics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.screens.main.search.ResourceWithInitial
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
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

class SearchTopicsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetFastHubTheme {
                    MainContent()
                }
            }
        }
    }

}

@Preview
@Composable
private fun TestMainContent(){
    MainContent()
}


@Composable
private fun MainContent() {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                content = {
                    TopAppBarContent(
                        //onBackPressed = onBackPressed, onSearchItemClick = onSearchClick
                    )
                },
            )
        },
    ) { contentPadding ->
        TabScreen(
            contentPaddingValues = contentPadding,
//            onListItemClick = onListItemClick,
//            state = state
        )
    }
}

@Composable
private fun TopAppBarContent(
    //onSearchItemClick: (String) -> Unit,
    //onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            //onBackPressed()
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

        IconButton(
            onClick = {
                //onSearchItemClick(text)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon"
            )
        }
    }
}


@Composable
private fun TabScreen(
    contentPaddingValues: PaddingValues,
//    onListItemClick: (String) -> Unit,
//    state: SearchScreenState
) {
    val tabs = listOf("REPOSITORIES", "USERS", "ISSUES", "CODE")
    var tabIndex by remember { mutableIntStateOf(0) }
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
//                                val count = state.Repositories.data?.total_count
//                                Log.d("ahi3646gg", "TabScreen: $count")
//                                if (count != null)
//                                    Text("$title ($count)")
//                                else Text(title)
                                Text(title)
                            }

                            1 -> {
//                                val count = state.Users.data?.total_count
//                                Log.d("ahi3646gg", "TabScreen: $count")
//                                if (count != null)
//                                    Text("$title ($count)")
//                                else Text(title)
                                Text(title)
                            }

                            2 -> {
//                                val count = state.Issues.data?.total_count
//                                Log.d("ahi3646gg", "TabScreen: $count")
//                                if (count != null)
//                                    Text("$title ($count)")
//                                else Text(title)
                                Text(title)
                            }

                            3 -> {
//                                val count = state.Codes.data?.total_count
//                                Log.d("ahi3646gg", "TabScreen: $count")
//                                if (count != null)
//                                    Text("$title ($count)")
//                                else Text(title)
                                Text(title)
                            }
                        }

                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            0 -> RepositoriesContent(
                contentPaddingValues = contentPaddingValues,
//                repositories = state.Repositories,
//                onNavigate = onListItemClick
            )

            1 -> {
//                UsersContent(
//                    contentPaddingValues = contentPaddingValues,
//                    onUsersItemClicked = onListItemClick,
//                    users = state.Users
//                )
            }

            2 -> {
//                IssuesContent(
//                    contentPaddingValues = contentPaddingValues,
//                    onIssueItemClicked = onListItemClick,
//                    issues = state.Issues
//                )
            }

            3 -> {
//                CodeContent(
//                    contentPaddingValues = contentPaddingValues,
//                    onCodeItemClicked = onListItemClick,
//                    codes = state.Codes
//                )
            }
        }
    }
}

@Composable
private fun RepositoriesContent(
    contentPaddingValues: PaddingValues,
//    repositories: ResourceWithInitial<RepositoryModel>,
//    onNavigate: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
//        items(repositories.data!!.items) { repository ->
//            RepositoryItem(repository, onNavigate)
//        }
    }

//    when (repositories) {
//        is ResourceWithInitial.Initial -> {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(text = "No search results")
//            }
//        }
//        is ResourceWithInitial.Loading -> {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(text = "Loading ...")
//            }
//        }
//
//        is ResourceWithInitial.Success -> {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(contentPaddingValues)
//                    .fillMaxSize()
//                    .background(Color.White),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Top
//            ) {
//                items(repositories.data!!.items) { repository ->
//                    RepositoryItem(repository, onNavigate)
//                }
//            }
//        }
//
//        is ResourceWithInitial.Failure -> {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(text = "Something went wrong !")
//                Log.d("ahi3646", "Unread: ${repositories.errorMessage}")
//            }
//        }
//    }
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
private fun UsersContent(
    users: ResourceWithInitial<UserModel>,
    contentPaddingValues: PaddingValues,
    onUsersItemClicked: (String) -> Unit
) {
    when (users) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results")
            }
        }
        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
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

        is ResourceWithInitial.Failure -> {
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
private fun UsersItem(
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
private fun IssuesContent(
    issues: ResourceWithInitial<IssuesModel>,
    contentPaddingValues: PaddingValues,
    onIssueItemClicked: (String) -> Unit
) {
    when (issues) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results")
            }
        }
        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
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

        is ResourceWithInitial.Failure -> {
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
private fun IssuesItem(
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
private fun CodeContent(
    codes: ResourceWithInitial<CodeModel>,
    contentPaddingValues: PaddingValues,
    onCodeItemClicked: (String) -> Unit
) {
    when (codes) {
        is ResourceWithInitial.Initial -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No search results")
            }
        }

        is ResourceWithInitial.Loading -> {
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

        is ResourceWithInitial.Success -> {
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

        is ResourceWithInitial.Failure -> {
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
private fun CodesItem(
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
