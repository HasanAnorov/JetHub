package com.hasan.jetfasthub.screens.main.organisations

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModel
import com.hasan.jetfasthub.screens.main.organisations.model.OrganisationMemberModelItem
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModel
import com.hasan.jetfasthub.screens.main.organisations.org_repo_model.OrganisationsRepositoryModelItem
import com.hasan.jetfasthub.screens.main.organisations.organisation_model.OrganisationModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrganisationsFragment : Fragment() {

    private val organisationsViewModel: OrganisationsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val organisation = arguments?.getString("profile_data") ?: ""
        val token = PreferenceHelper.getToken(requireContext())

        organisationsViewModel.getOrganisationMembers(token = token, organisation, 1)
        organisationsViewModel.getOrganisationRepositories(
            token = token,
            organisation = organisation,
            type = "all",
            page = 1
        )
        organisationsViewModel.getOrg(token, organisation)

        return ComposeView(requireContext()).apply {
            setContent {
                val state by organisationsViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(state = state,
                        organisation = organisation,
                        onRecyclerItemClick = { dest, data ->
                            if (data != null) {
                                val bundle = Bundle()
                                bundle.putString("home_data", data)
                                findNavController().navigate(dest, bundle)
                            } else if (dest == -1) {
                                findNavController().popBackStack()
                            } else {
                                findNavController().navigate(dest)
                            }
                        },
                        onAction = {action, data ->
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
                                        context,
                                        Intent.createChooser(intent, shareWith),
                                        null
                                    )
                                }

                                "browser" -> {
                                    var webpage = Uri.parse(data)

                                    if (!data.startsWith("http://") && !data.startsWith("https://")) {
                                        webpage = Uri.parse("http://$data")
                                    }
                                    val urlIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        webpage
                                    )
                                    requireContext().startActivity(urlIntent)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun MainContent(
    state: OrganisationScreenState,
    organisation: String,
    onRecyclerItemClick: (Int, String?) -> Unit,
    onAction: (String, String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopAppBar(
            backgroundColor = Color.White,
            elevation = 0.dp,
            content = {
                TopAppBarContent(onRecyclerItemClick, organisation, onAction)
            },
        )
    }) { paddingValues ->
        TabScreen(paddingValues, state, onRecyclerItemClick, onAction )
    }
}

@Composable
private fun TabScreen(
    paddingValues: PaddingValues,
    state: OrganisationScreenState,
    onRecyclerItemClick: (Int, String?) -> Unit,
    onAction: (String, String) -> Unit
) {

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("OVERVIEW", "REPOSITORIES", "PEOPLE")

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {

        ScrollableTabRow(selectedTabIndex = tabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        if (tabIndex == index) {
                            androidx.compose.material3.Text(title, color = Color.Blue)
                        } else {
                            androidx.compose.material3.Text(title, color = Color.Black)
                        }
                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }

        when (tabIndex) {
            0 -> {
                Overview(state.Organisation, onAction, onRecyclerItemClick)
            }

            1 -> {
                Repositories(
                    orgRepos = state.OrganisationRepos, onRecyclerItemClick = onRecyclerItemClick
                )
            }

            2 -> {
                People(state = state.OrganisationMembers, onRecyclerItemClick = onRecyclerItemClick)
            }
        }
    }
}

@Composable
private fun Overview(
    org: Resource<OrganisationModel>,
    onAction: (String, String) -> Unit,
    onRecyclerItemClick: (Int, String?) -> Unit
) {

    when (org) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 9.dp
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                    ) {
                        GlideImage(
                            failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                            imageModel = {
                                org.data!!.avatar_url
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

                        Column(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = org.data!!.login,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                color = Color.Black,
                                style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                        }
                    }

                    if (org.data!!.description != null) {
                        Text(
                            text = org.data.description!!,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 2
                        )
                    }

                    if (org.data.location != null) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = "Location"
                            )
                            Text(
                                text = org.data.location,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        Divider(
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }

                    if (org.data.email != null) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_email),
                                contentDescription = "Email"
                            )
                            Text(
                                text = org.data.email,
                                modifier = Modifier.padding(start = 16.dp).clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onAction("share", org.data.email )
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

                    if (org.data.blog != null) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_insert_link),
                                contentDescription = "Corporation"
                            )
                            Text(
                                text = org.data.blog,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                      onAction("browser", org.data.blog )
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

                    if (org.data.created_at != null) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_time),
                                contentDescription = "Corporation"
                            )
                            Text(
                                text = ParseDateFormat.getTimeAgo(org.data.created_at).toString(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        Divider(
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }

                    if (org.data.has_organization_projects) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_project),
                                tint = Color.Blue,
                                contentDescription = "Corporation"
                            )
                            Text(
                                text = "Projects",
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
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
                androidx.compose.material3.Text(text = "Something went wrong ...")
            }
        }
    }

}


@Composable
private fun Repositories(
    orgRepos: Resource<OrganisationsRepositoryModel>,
    onRecyclerItemClick: (Int, String?) -> Unit
) {
    when (orgRepos) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(orgRepos.data!!) { index, memberModel ->
                    OrganisationRepoItem(
                        memberModel, onRepositoryItemClicked = onRecyclerItemClick
                    )
                    if (index < orgRepos.data.lastIndex) {
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
                androidx.compose.material3.Text(text = "Something went wrong ...")
            }
        }
    }
}

@Composable
fun OrganisationRepoItem(
    repository: OrganisationsRepositoryModelItem, onRepositoryItemClicked: (Int, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onRepositoryItemClicked(0, repository.full_name)
            })
            .padding(4.dp), elevation = 0.dp, backgroundColor = Color.White
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
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Forked / ")
                            }
                        }
                        append(repository.name)
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
private fun People(
    state: Resource<OrganisationMemberModel>, onRecyclerItemClick: (Int, String?) -> Unit
) {
    when (state) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(state.data!!) { index, memberModel ->
                    OrganisationMemberItemCard(
                        memberModel, onItemClicked = onRecyclerItemClick
                    )
                    if (index < state.data.lastIndex) {
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
                androidx.compose.material3.Text(text = "Something went wrong ...")
            }
        }
    }
}

@Composable
fun OrganisationMemberItemCard(
    memberModel: OrganisationMemberModelItem, onItemClicked: (Int, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(
                    R.id.action_organisationsFragment_to_profileFragment,
                    memberModel.login
                )
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
                    memberModel.avatar_url
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
                    text = memberModel.login,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
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
private fun TopAppBarContent(
    onBackPressed: (Int, String?) -> Unit,
    organisation: String,
    onAction: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            onBackPressed(-1, null)
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
        }

        Text(
            color = Color.Black,
            modifier = Modifier
                .weight(1F)
                .padding(start = 10.dp, end = 10.dp),
            text = organisation,
            style = MaterialTheme.typography.titleMedium,
        )

        IconButton(onClick = { onAction("share", organisation) }) {
            Icon(Icons.Filled.Share, contentDescription = "Share")
        }
    }
}