package com.hasan.jetfasthub.screens.main.repository

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.data.download.AndroidDownloader
import com.hasan.jetfasthub.screens.main.repository.models.commits_model.CommitsModel
import com.hasan.jetfasthub.screens.main.repository.models.commits_model.CommitsModelItem
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FileModel
import com.hasan.jetfasthub.screens.main.repository.models.file_models.FilesModel
import com.hasan.jetfasthub.screens.main.repository.models.release_download_model.ReleaseDownloadModel
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModel
import com.hasan.jetfasthub.screens.main.repository.models.releases_model.ReleasesModelItem
import com.hasan.jetfasthub.screens.main.repository.models.repo_contributor_model.Contributors
import com.hasan.jetfasthub.screens.main.repository.models.repo_contributor_model.ContributorsItem
import com.hasan.jetfasthub.screens.main.repository.models.repo_model.RepoModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoryFragment : Fragment() {

    private val repositoryViewModel: RepositoryViewModel by viewModel()
    private lateinit var initialBranch: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val token = PreferenceHelper.getToken(requireContext())
        val owner = arguments?.getString("home_data") ?: ""
        val repo = arguments?.getString("home_extra") ?: ""

        repositoryViewModel.getRepo(
            token = token, owner = owner, repo = repo
        )
        repositoryViewModel.getBranches(
            token = token, owner = owner, repo = repo
        )
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { branches ->
                val branchesList = arrayListOf<String>()
                branches.forEach {
                    branchesList.add(it.name)
                    Log.d("ahi3646", "onCreateView branches: ${it.name}")
                }

                initialBranch = if (branchesList.isNotEmpty()) {
                    if (branchesList.contains("main")) {
                        "main"
                    } else if (branchesList.contains("master")) {
                        "master"
                    } else {
                        branchesList[0]
                    }
                } else {
                    "main"
                }

                repositoryViewModel.getContentFiles(
                    token = token, owner = owner, repo = repo, path = "", ref = initialBranch
                )

                repositoryViewModel.getCommits(
                    token = token, owner = owner, repo = repo, branch = initialBranch, page = 1, path = ""
                )

            }.launchIn(lifecycleScope)


        repositoryViewModel.getContributors(
            token = token, owner = owner, repo = repo, page = 1
        )
        repositoryViewModel.getReleases(
            token = token, owner = owner, repo = repo, page = 1
        )

        repositoryViewModel.getContentFiles(
            token = token, owner = owner, repo = repo, path = "", ref = "main"
        )

        return ComposeView(requireContext()).apply {
            setContent {
                val state by repositoryViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onBottomBarClicked = { repositoryScreen ->
                            repositoryViewModel.onBottomBarItemClicked(repositoryScreen)
                        },
                        onCurrentSheetChanged = { currentSheet ->
                            repositoryViewModel.onBottomSheetChanged(currentSheet)
                        },
                        onItemClicked = { dest, data, extra ->
                            when (dest) {
                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                else -> {
                                    val bundle = Bundle()
                                    if (data != null) {
                                        bundle.putString("home_data", data)
                                    }
                                    if (extra != null) {
                                        bundle.putString("home_extra", extra)
                                    }
                                    findNavController().navigate(dest, bundle)
                                }
                            }
                        },
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
                                    intent.putExtra(Intent.EXTRA_TEXT, data)

                                    ContextCompat.startActivity(
                                        context, Intent.createChooser(intent, shareWith), null
                                    )
                                }

                                "browser" -> {
                                    var webpage = Uri.parse(data)

                                    if (!data!!.startsWith("http://") && !data.startsWith("https://")) {
                                        webpage = Uri.parse("http://$data")
                                    }
                                    val urlIntent = Intent(
                                        Intent.ACTION_VIEW, webpage
                                    )
                                    requireContext().startActivity(urlIntent)
                                }

                                "copy" -> {
                                    val clipboardManager =
                                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clipData = ClipData.newPlainText("text", data)
                                    clipboardManager.setPrimaryClip(clipData)
                                    Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT)
                                        .show()
                                }

                                "on_path_change" -> {
                                    repositoryViewModel.getContentFiles(
                                        token = token,
                                        owner = owner,
                                        repo = repo,
                                        path = data ?: "",
                                        ref = initialBranch
                                    )

                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainContent(
    state: RepositoryScreenState,
    onBottomBarClicked: (RepositoryScreens) -> Unit,
    onItemClicked: (Int, String?, String?) -> Unit,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: (BottomSheetScreens) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed,
        //animationSpec = spring(dampingRatio = Spring.DefaultDisplacementThreshold)
    )
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val closeSheet: () -> Unit = {
        scope.launch {
            sheetState.collapse()
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            when (state.currentSheet) {
                is BottomSheetScreens.ReleaseItemSheet -> {
                    ReleaseInfoSheet(
                        releaseItem = state.currentSheet.releaseItem,
                        closeSheet = closeSheet
                    )
                }

                BottomSheetScreens.RepositoryInfoSheet -> {
                    RepositoryInfoSheet(
                        closeSheet = closeSheet,
                        state = state,
                    )
                }
            }
        },
        scaffoldState = sheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) { sheetPadding ->
        Scaffold(
            modifier = Modifier.padding(sheetPadding),
            topBar = {
                Column(Modifier.fillMaxWidth()) {
                    TitleHeader(
                        state = state.repo,
                        onItemClicked = onItemClicked,
                        onCurrentSheetChanged = {
                            onCurrentSheetChanged(BottomSheetScreens.RepositoryInfoSheet)
                            scope.launch {
                                if (sheetState.isCollapsed) {
                                    sheetState.expand()
                                } else {
                                    sheetState.collapse()
                                }
                            }
                        }
                    )
                    Toolbar(
                        state = state.repo, onItemClicked = onItemClicked, onAction = onAction
                    )
                }
            },
            bottomBar = {
                BottomNav(onBottomBarClicked, state.repo)
            }
        ) { paddingValues ->

            when (state.selectedBottomBarItem) {
                RepositoryScreens.Code -> CodeScreen(
                    paddingValues = paddingValues,
                    state = state,
                    onItemClicked = onItemClicked,
                    onCurrentSheetChanged = { release ->
                        onCurrentSheetChanged(BottomSheetScreens.ReleaseItemSheet(release))
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            } else {
                                sheetState.collapse()
                            }
                        }
                    },
                    onAction = onAction
                )

                RepositoryScreens.Issues -> IssuesScreen(paddingValues = paddingValues)
                RepositoryScreens.PullRequest -> PullRequestsScreen(paddingValues = paddingValues)
                RepositoryScreens.Projects -> {
                    if ((state.repo.data != null) && state.repo.data.has_projects) {
                        ProjectsScreen(paddingValues = paddingValues)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseInfoSheet(releaseItem: ReleasesModelItem, closeSheet: () -> Unit) {

    Column(
        Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = releaseItem.name,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (releaseItem.body != null) {
            Text(
                text = releaseItem.body.toString()
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    closeSheet()
                }
            ) {
                Text(text = "Cancel", color = Color.Red)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    closeSheet()
                }
            ) {
                Text(text = "OK", color = Color.Blue)
            }
        }
    }
}

@Composable
private fun RepositoryInfoSheet(
    state: RepositoryScreenState,
    closeSheet: () -> Unit
) {
    val repository = state.repo
    if (repository.data != null) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = repository.data.full_name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (repository.data.description != null) {
                Text(
                    text = repository.data.description
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        closeSheet()
                    }
                ) {
                    Text(text = "OK")
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
private fun CodeScreen(
    paddingValues: PaddingValues,
    state: RepositoryScreenState,
    onItemClicked: (Int, String?, String?) -> Unit,
    onCurrentSheetChanged: (releaseItem: ReleasesModelItem) -> Unit,
    onAction: (String, String?) -> Unit
) {
    val tabs = listOf("README", "FILES", "COMMITS", "RELEASE", "CONTRIBUTORS")
    var tabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        if (tabIndex == index) {
                            androidx.compose.material3.Text(title, color = Color.Blue)
                        } else {
                            androidx.compose.material3.Text(title, color = Color.Black)
                        }
                    },
                )
            }
        }
        when (tabIndex) {
            0 -> ReadMe()
            1 -> FilesScreen(state.RepositoryFiles, onAction)
            2 -> CommitsScreen(state.Commits)
            3 -> ReleasesScreen(state.Releases, onCurrentSheetChanged)
            4 -> ContributorsScreen(state.Contributors, onItemClicked)
        }
    }
}

@Composable
private fun FilesScreen(
    state: Resource<FilesModel>,
    onAction: (String, String?) -> Unit,
) {

    when (state) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {

            val paths = remember {
                mutableListOf<FileModel>()
            }

            Column(
                Modifier
                    .background(Color.White)
                    .fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_branch),
                        contentDescription = "branch icon"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                            .clickable {

                            }
                    ) {
                        Text(
                            text = "Here should be branch of repositories",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dropdown_icon),
                            contentDescription = "dropdown"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "direction"
                        )
                    }
                    LazyRow(Modifier.weight(1F)) {
                        items(paths) { file ->
                            FilePathRowItemCard(file, onAction)
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = "download"
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "add icon"
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "direction"
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(state.data!!) { index, file ->
                        when (file.type) {
                            "dir" -> {
                                FileFolderItemCard(
                                    file = file,
                                    onAction = onAction,
                                    onPathChanged = { changedFile ->
                                        if (paths.contains(changedFile)) {
                                            Log.d("ahi3646", "FilesScreen: contains ")
                                            paths.subList(0, paths.indexOf(changedFile)).clear()
                                        } else {
                                            paths.add(changedFile)
                                        }
                                    }
                                )
                            }

                            "file" -> {
                                FileDocumentItemCard(
                                    file = file,
                                    onAction = onAction,
                                    onPathChanged = { changedFile ->
                                        paths.add(changedFile)
                                    }
                                )
                            }

                            else -> {}
                        }
                        if (index < state.data.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                            )
                        }
                    }
                }

            }

        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
            }
        }
    }

}


@Composable
private fun FilePathRowItemCard(file: FileModel, onAction: (String, String?) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(4.dp)
            .clickable {
                onAction("on_path_change", file.path)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = file.name)
        Icon(painter = painterResource(id = R.drawable.ic_right_arrow), contentDescription = "path")
    }
}


@Composable
private fun FileFolderItemCard(
    file: FileModel,
    onAction: (String, String?) -> Unit,
    onPathChanged: (file: FileModel) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onPathChanged(file)
                onAction("on_path_change", file.path)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_folder),
            contentDescription = "folder icon"
        )

        Text(
            text = file.name,
            modifier = Modifier
                .padding(8.dp)
                .weight(1F),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_overflow),
            contentDescription = "option menu"
        )
    }
}

@Composable
private fun FileDocumentItemCard(
    file: FileModel,
    onAction: (String, String?) -> Unit,
    onPathChanged: (file: FileModel) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onPathChanged(file)
                onAction("on_path_change", file.path)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_document),
            contentDescription = "Document icon"
        )

        Text(
            text = file.name,
            modifier = Modifier
                .padding(8.dp)
                .weight(1F),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_overflow),
            contentDescription = "option menu"
        )
    }
}

@Composable
private fun CommitsScreen(commits: Resource<CommitsModel>) {
    when (commits) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
            }
        }

        is Resource.Success -> {
            Column(
                Modifier
                    .background(Color.White)
                    .fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_branch),
                        contentDescription = "branch icon"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                            }
                    ) {
                        Text(
                            text = "Here should be branch of repositories",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dropdown_icon),
                            contentDescription = "dropdown"
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(commits.data!!) { index, commit ->
                        CommitsItem(commit)
                        if (index < commits.data.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(start = 6.dp, end = 6.dp)
                                    .fillMaxWidth(0.7F)
                                    .align(Alignment.End)
                            )
                        }
                    }
                }

            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
            }
        }
    }
}

@Composable
private fun CommitsItem(commit: CommitsModelItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {

            })
            .padding(4.dp), elevation = 0.dp, backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            GlideImage(
                failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                imageModel = {
                    commit.author.avatar_url
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

//            Image(
//                painter = painterResource(id = R.drawable.baseline_person_24),
//                contentDescription = "avatar icon",
//                modifier = Modifier
//                    .size(48.dp, 48.dp)
//                    .size(48.dp, 48.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop,
//            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = commit.commit.message,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = buildAnnotatedString {
                    append(commit.author.login)
                    append(" ")
                    append(ParseDateFormat.getDateFromString(commit.commit.author.date).toString())
                })

            }
        }
    }
}

@Composable
private fun ReleasesScreen(
    releases: Resource<ReleasesModel>,
    onCurrentSheetChanged: (releaseItem: ReleasesModelItem) -> Unit
) {
    when (releases) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
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
                itemsIndexed(releases.data!!) { index, release ->
                    ReleaseItemCard(releasesModelItem = release, onCurrentSheetChanged)
                    if (index < releases.data.lastIndex) {
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Can't load data !")
            }
        }
    }
}

@Composable
private fun ReleaseItemCard(
    releasesModelItem: ReleasesModelItem,
    onCurrentSheetChanged: (release: ReleasesModelItem) -> Unit,
) {

    var isDialogShown by remember {
        mutableStateOf(false)
    }
    val releases = arrayListOf<ReleaseDownloadModel>()

    //delegate this feature to viewModel
    val downloader = AndroidDownloader(LocalContext.current)

    if (releasesModelItem.zipball_url.isNotEmpty()) {
        releases.add(
            ReleaseDownloadModel(
                title = "Source code (zip)",
                url = releasesModelItem.tarball_url,
                extension = "application/zip",
                downloadCount = 0,
                notificationTitle = releasesModelItem.tag_name
            )
        )
    }
    if (releasesModelItem.tarball_url.isNotEmpty()) {
        releases.add(
            ReleaseDownloadModel(
                title = "Source code (tar.gz)",
                url = releasesModelItem.tarball_url,
                extension = "application/x-gzip",
                downloadCount = 0,
                notificationTitle = releasesModelItem.tag_name
            )
        )
    }
    if (releasesModelItem.assets.isNotEmpty()) {
        releasesModelItem.assets.forEach { asset ->
            releases.add(
                ReleaseDownloadModel(
                    title = asset.name,
                    url = asset.browser_download_url,
                    extension = asset.content_type,
                    downloadCount = asset.download_count,
                    notificationTitle = releasesModelItem.tag_name
                )
            )
        }
    }

    if (isDialogShown) {
        Dialog(
            onDismissRequest = { isDialogShown = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                elevation = 5.dp,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .fillMaxHeight(0.33f)
                    .border(1.dp, color = Color.Black, shape = RoundedCornerShape(15.dp))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Releases",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyColumn {
                        itemsIndexed(releases) { index, release ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        downloader.download(release)
                                    },
                                elevation = 0.dp
                            ) {
                                val title =
                                    if (release.downloadCount != 0) {
                                        "${release.title} (${release.downloadCount})"
                                    } else release.title
                                Text(
                                    text = title,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 12.dp,
                                        bottom = 12.dp
                                    )
                                )
                            }
                            if (index < releases.lastIndex) {
                                Divider(
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onCurrentSheetChanged(releasesModelItem)
            })
            .padding(4.dp),
        elevation = 0.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = releasesModelItem.name,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        append(releasesModelItem.author.login)
                        append(" ")
                        if (releasesModelItem.draft) {
                            append("Drafted")
                        } else {
                            append("Released")
                        }
                        append(" ")
                        append(
                            ParseDateFormat.getDateFromString(releasesModelItem.created_at)
                                .toString()
                        )
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    isDialogShown = true
                },
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .size(48.dp, 48.dp)
                    .clip(CircleShape),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = "release download"
                )
            }
        }
    }
}

@Composable
private fun ReadMe() {
    val webViewState = rememberWebViewState(url = "")
    WebView(
        state = webViewState,
        //this later might be problem for play market upload
        //onCreated = {it.settings.javaScriptEnabled = true},
        captureBackPresses = true
    )
}

@Composable
private fun ContributorsScreen(
    contributors: Resource<Contributors>,
    onItemClicked: (Int, String?, String?) -> Unit,
) {
    when (contributors) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...")
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
                itemsIndexed(contributors.data!!) { index, contributor ->
                    ContributorsItemCard(
                        contributor,
                        onItemClicked
                    )
                    if (index < contributors.data.lastIndex) {
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Something went wrong !")
            }
        }
    }
}

@Composable
private fun ContributorsItemCard(
    contributorsItem: ContributorsItem,
    onItemClicked: (Int, String?, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClicked(
                    R.id.action_repositoryFragment_to_profileFragment,
                    contributorsItem.login,
                    null
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
                    contributorsItem.avatar_url
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
                    text = contributorsItem.login,
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Commits (${contributorsItem.contributions})")

            }
        }
    }
}

@Composable
private fun IssuesScreen(paddingValues: PaddingValues) {
    val tabs = listOf("OPENED", "CLOSED")
    var tabIndex by remember { mutableIntStateOf(0) }

    TabRow(
        selectedTabIndex = tabIndex,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabIndex == index, onClick = { tabIndex = index },
                text = {
                    if (tabIndex == index) {
                        androidx.compose.material3.Text(title, color = Color.Blue)
                    } else {
                        androidx.compose.material3.Text(title, color = Color.Black)
                    }
                },
            )
        }
    }
    when (tabIndex) {
        0 -> {}
        1 -> {}
    }
}

@Composable
private fun PullRequestsScreen(paddingValues: PaddingValues) {
    val tabs = listOf("OPENED", "CLOSED")
    var tabIndex by remember { mutableIntStateOf(0) }

    TabRow(
        selectedTabIndex = tabIndex,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabIndex == index, onClick = { tabIndex = index },
                text = {
                    if (tabIndex == index) {
                        androidx.compose.material3.Text(title, color = Color.Blue)
                    } else {
                        androidx.compose.material3.Text(title, color = Color.Black)
                    }
                },
            )
        }
    }
    when (tabIndex) {
        0 -> {}
        1 -> {}
    }
}

@Composable
private fun ProjectsScreen(paddingValues: PaddingValues) {
    val tabs = listOf("OPENED", "CLOSED")
    var tabIndex by remember { mutableIntStateOf(0) }

    TabRow(
        selectedTabIndex = tabIndex,
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth(1F)
            .padding(paddingValues)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabIndex == index, onClick = { tabIndex = index },
                text = {
                    if (tabIndex == index) {
                        androidx.compose.material3.Text(title, color = Color.Blue)
                    } else {
                        androidx.compose.material3.Text(title, color = Color.Black)
                    }
                },
            )
        }
    }
    when (tabIndex) {
        0 -> {}
        1 -> {}
    }
}

@Composable
private fun BottomNav(
    onBottomBarClicked: (RepositoryScreens) -> Unit,
    state: Resource<RepoModel>
) {
    val context = LocalContext.current

    when (state) {

        is Resource.Loading -> {
            Surface(elevation = 16.dp) {
                BottomAppBar(containerColor = Color.White) {
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_code_24),
                                contentDescription = "Code Screen"
                            )
                        },
                        label = {
                            Text(
                                "Code",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        selected = false,
                        onClick = {},
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
                        onClick = {},
                    )

                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_pull_requests),
                                contentDescription = "PullRequest Screen"
                            )
                        },
                        label = {
                            Text(
                                "Pull Requests",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                softWrap = false,
                            )
                        },
                        selected = false,
                        onClick = {},
                    )
                }
            }
        }

        is Resource.Success -> {
            val repository = state.data!!

            var isCodeCurrent by remember {
                mutableStateOf(true)
            }
            var isIssuesCurrent by remember {
                mutableStateOf(false)
            }
            var isPRCurrent by remember {
                mutableStateOf(false)
            }
            var isProjectsCurrent by remember {
                mutableStateOf(false)
            }

            Surface(elevation = 16.dp) {
                BottomAppBar(containerColor = Color.White) {
                    BottomNavigationItem(
                        alwaysShowLabel = isCodeCurrent,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_code_24),
                                contentDescription = "Code Screen"
                            )
                        },
                        label = {
                            Text(
                                "Code",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        selected = false,
                        onClick = {
                            isCodeCurrent = true
                            isIssuesCurrent = false
                            isPRCurrent = false
                            isProjectsCurrent = false
                            onBottomBarClicked(RepositoryScreens.Code)
                        },
                    )

                    BottomNavigationItem(
                        alwaysShowLabel = isIssuesCurrent,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_issues),
                                contentDescription = "Issues Screen"
                            )
                        },
                        label = { Text("Issues") },
                        selected = false,
                        onClick = {
                            if (repository.has_issues) {
                                isCodeCurrent = false
                                isIssuesCurrent = true
                                isPRCurrent = false
                                isProjectsCurrent = false

                                onBottomBarClicked(RepositoryScreens.Issues)
                            } else {
                                Toast.makeText(
                                    context, "Issue section has been disabled", Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )

                    BottomNavigationItem(
                        alwaysShowLabel = isPRCurrent,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_pull_requests),
                                contentDescription = "PullRequest Screen"
                            )
                        },
                        label = {
                            Text(
                                "Pull Requests",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                softWrap = false,
                            )
                        },
                        selected = false,
                        onClick = {
                            isCodeCurrent = false
                            isIssuesCurrent = false
                            isPRCurrent = true
                            isProjectsCurrent = false
                            onBottomBarClicked(RepositoryScreens.PullRequest)
                        },
                    )

                    BottomNavigationItem(
                        alwaysShowLabel = isProjectsCurrent,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_project),
                                contentDescription = "PullRequest Screen"
                            )
                        },
                        label = { Text("Projects") },
                        selected = false,
                        onClick = {
                            isCodeCurrent = false
                            isIssuesCurrent = false
                            isPRCurrent = false
                            isProjectsCurrent = true
                            onBottomBarClicked(RepositoryScreens.Projects)
                        },
                    )
                }
            }
        }

        is Resource.Failure -> {
            Toast.makeText(context, "Can't load data !", Toast.LENGTH_SHORT).show()

            Surface(elevation = 16.dp) {
                BottomAppBar(containerColor = Color.White) {
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_code_24),
                                contentDescription = "Code Screen"
                            )
                        },
                        label = {
                            Text(
                                "Code",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        selected = false,
                        onClick = {},
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
                        onClick = {},
                    )

                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_pull_requests),
                                contentDescription = "PullRequest Screen"
                            )
                        },
                        label = {
                            Text(
                                "Pull Requests",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                softWrap = false,
                            )
                        },
                        selected = false,
                        onClick = {},
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleHeader(
    state: Resource<RepoModel>,
    onItemClicked: (Int, String?, String?) -> Unit,
    onCurrentSheetChanged: () -> Unit
) {
    when (state) {
        is Resource.Loading -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "avatar icon",
                    modifier = Modifier
                        .size(48.dp, 48.dp)
                        .size(48.dp, 48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        is Resource.Success -> {
            val repository = state.data!!

            var hasTopics by remember {
                mutableStateOf(false)
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                                    R.id.action_repositoryFragment_to_profileFragment,
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = repository.full_name,
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row {
                            Text(
                                text = ParseDateFormat.getTimeAgo(repository.pushed_at).toString(),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = FileSizeCalculator.humanReadableByteCountBin(repository.size.toLong()),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            if (repository.language != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = repository.language.toString(),
                                    fontSize = 14.sp,
                                    color = Color.Yellow
                                )
                            }
                        }
                    }

                    if (repository.topics.isNotEmpty()) {
                        IconButton(onClick = {
                            hasTopics = !hasTopics
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_label),
                                contentDescription = "label"
                            )
                        }
                    }

                    IconButton(onClick = {
                        onCurrentSheetChanged()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info_outline),
                            contentDescription = "info"
                        )
                    }
                }

                if (hasTopics) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        items(repository.topics) { topic ->
                            Surface(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .clickable { },
                                contentColor = colorResource(id = R.color.milt_black),
                                color = Color.Gray
                            ) {
                                Text(
                                    text = topic,
                                    Modifier.padding(
                                        top = 4.dp,
                                        bottom = 4.dp,
                                        start = 6.dp,
                                        end = 6.dp
                                    ),
                                    color = Color.Blue
                                )
                            }
                        }
                    }
                }

            }

        }

        is Resource.Failure -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "avatar icon",
                    modifier = Modifier
                        .size(48.dp, 48.dp)
                        .size(48.dp, 48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
private fun Toolbar(
    state: Resource<RepoModel>,
    onItemClicked: (Int, String?, String?) -> Unit,
    onAction: (String, String?) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    when (state) {

        is Resource.Loading -> {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { onItemClicked(-1, null, null) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye),
                                contentDescription = "Watch"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = "Star"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fork),
                                contentDescription = "Star"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pin),
                                contentDescription = "Pin"
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = {
                            showMenu = !showMenu
                        },
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "more option")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(text = { Text(text = "Share") }, onClick = {
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Open in browser") }, onClick = {
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Copy URL") }, onClick = {
                            showMenu = false
                        })
                    }
                }
            }
        }

        is Resource.Success -> {
            val repository = state.data!!

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { onItemClicked(-1, null, null) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye),
                                contentDescription = "Watch"
                            )
                        }
                        Text(text = repository.subscribers_count.toString())
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = "Star"
                            )
                        }
                        Text(text = repository.stargazers_count.toString())
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fork),
                                contentDescription = "Star"
                            )
                        }
                        Text(text = repository.forks_count.toString())
                    }

                    if (repository.has_wiki) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_book),
                                    contentDescription = "Star"
                                )
                            }
                            Text(text = "Wiki")
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pin),
                                contentDescription = "Pin"
                            )
                        }
                        Text(text = "Pin")
                    }

                    if (repository.license != null) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_license),
                                    contentDescription = "License"
                                )
                            }
                            Text(text = repository.license.spdx_id, maxLines = 1)
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = {
                            showMenu = !showMenu
                        },
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "more option")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(text = { Text(text = "Share") }, onClick = {
                            onAction("share", repository.html_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Open in browser") }, onClick = {
                            onAction("browser", repository.html_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Copy URL") }, onClick = {
                            onAction("copy", repository.html_url)
                            showMenu = false
                        })
                        if (repository.fork) {
                            DropdownMenuItem(
                                text = { Text(text = repository.parent.full_name) },
                                onClick = {
                                    onItemClicked(
                                        R.id.action_repositoryFragment_self,
                                        repository.parent.owner.login,
                                        repository.parent.name
                                    )
                                    showMenu = false
                                })
                        }
                    }
                }

            }
        }

        is Resource.Failure -> {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { onItemClicked(-1, null, null) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye),
                                contentDescription = "Watch"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = "Star"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fork),
                                contentDescription = "Star"
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pin),
                                contentDescription = "Pin"
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = {
                            showMenu = !showMenu
                        },
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "more option")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(text = { Text(text = "Share") }, onClick = {
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Open in browser") }, onClick = {
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(text = "Copy URL") }, onClick = {
                            showMenu = false
                        })
                    }
                }
            }
        }

    }
}

