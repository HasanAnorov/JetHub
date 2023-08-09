package com.hasan.jetfasthub.screens.main.commits

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
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
import com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model.CommitCommentsModel
import com.hasan.jetfasthub.screens.main.commits.models.commit_comments_model.CommitCommentsModelItem
import com.hasan.jetfasthub.screens.main.commits.models.commit_model.CommitModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class CommitFragment : Fragment() {

    private val commitViewModel: CommitViewModel by viewModel()
    private lateinit var token: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        token = PreferenceHelper.getToken(requireContext())
        val owner = arguments?.getString("owner")
        val repo = arguments?.getString("repo")
        val sha = arguments?.getString("sha")

        if (!owner.isNullOrEmpty() && !repo.isNullOrEmpty() && !sha.isNullOrEmpty()) {

            commitViewModel.init(owner, repo, sha)

            commitViewModel.getCommit(
                token = token, owner = owner, repo = repo, branch = sha
            )

            commitViewModel.getCommitComments(
                token = token, owner = owner, repo = repo, branch = sha
            )

        } else {
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val state by commitViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(state = state, onNavigate = { dest, data, id ->
                        when (dest) {
                            -1 -> {
                                findNavController().popBackStack()
                            }

                            R.id.action_commitFragment_to_editCommentFragment -> {
                                val bundle = Bundle()
                                bundle.putString("owner", state.CommitOwner)
                                bundle.putString("repo", state.CommitRepo)
                                bundle.putString("edit_comment", data)
                                bundle.putInt("comment_id", id!!)
                                findNavController().navigate(dest, bundle)
                            }

                            R.id.action_commitFragment_to_profileFragment -> {
                                val bundle = bundleOf("username" to data)
                                findNavController().navigate(dest, bundle)
                            }
                        }
                    }, onAction = { action, data ->
                        when (action) {

                            "post_comment" -> {
                                commitViewModel.postCommitComment(
                                    token = token,
                                    owner = state.CommitOwner,
                                    repo = state.CommitRepo,
                                    branch = state.CommitSha,
                                    body = data!!
                                ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                    .onEach { response ->
                                        if (response) {
                                            commitViewModel.getCommitComments(
                                                token = token,
                                                owner = state.CommitOwner,
                                                repo = state.CommitRepo,
                                                branch = state.CommitSha
                                            )
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't post comment",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                            }

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

                            "download" -> {
                                val message = Uri.parse(
                                    data!!
                                ).lastPathSegment
                                commitViewModel.downloadCommit(
                                    data, message ?: "jethub_download"
                                )
                            }

                            "delete_comment" -> {
                                commitViewModel.deleteComment(
                                    token = token,
                                    owner = state.CommitOwner,
                                    repo = state.CommitRepo,
                                    commentId = data!!.toInt()
                                ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                    .onEach { response ->
                                        if (response) {
                                            commitViewModel.getCommitComments(
                                                token = token,
                                                owner = state.CommitOwner,
                                                repo = state.CommitRepo,
                                                branch = state.CommitSha
                                            )
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't delete comment now !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                            }

                        }
                    }, onCurrentSheetChanged = { currentSheet ->
                        commitViewModel.onBottomSheetChanged(currentSheet)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainContent(
    state: CommitScreenState,
    onNavigate: (Int, String?, Int?) -> Unit,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: (currentSheet: CommitScreenSheets) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed,
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
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                scope.launch {
                    if (sheetScaffoldState.bottomSheetState.isExpanded) {
                        sheetScaffoldState.bottomSheetState.collapse()
                    }
                }
            })
        },
        scaffoldState = sheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            when (state.CurrentSheet) {
                is CommitScreenSheets.CommitInfoSheet -> {
                    when (state.Commit) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val commit = state.Commit.data!!
                            Column(
                                Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = commit.commit.message,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "${state.CommitOwner} / ${state.CommitRepo}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(onClick = {
                                        closeSheet()
                                    }) {
                                        Text(text = "Cancel", color = Color.Red)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(onClick = {
                                        closeSheet()
                                    }) {
                                        Text(text = "OK", color = Color.Blue)
                                    }
                                }
                            }
                        }
                        is Resource.Failure -> {}
                    }
                }
                is CommitScreenSheets.CommitDeleteRequestSheet -> {
                    val commentId = state.CurrentSheet.commentId

                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Delete", style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Are you sure ?"
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Button(onClick = { closeSheet() }) {
                                Text(text = "No", color = Color.Red)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Button(onClick = {
                                onAction("delete_comment", commentId.toString())
                                closeSheet()
                            }) {
                                Text(text = "Yes", color = Color.White)
                            }
                        }
                    }
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { sheetPadding ->
        Scaffold(
            modifier = Modifier.padding(sheetPadding),
            topBar = {
                Column(Modifier.fillMaxWidth()) {
                    TitleHeader(
                        repo = state.CommitRepo,
                        state = state.Commit,
                        onNavigate = onNavigate,
                        onCurrentSheetChanged = {
                            onCurrentSheetChanged(it)
                            scope.launch {
                                if (sheetScaffoldState.bottomSheetState.isCollapsed) {
                                    sheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    sheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        })
                    Toolbar(
                        state = state.Commit,
                        onNavigate = onNavigate,
                        onAction = onAction,
                    )
                }
            },
        ) { paddingValues ->

            var tabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("FILES", "COMMENTS")

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            text = {
                                if (tabIndex == index) {
                                    Text(
                                        title, color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    Text(title, color = MaterialTheme.colorScheme.outline)
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }

                when (tabIndex) {
                    0 -> {
                        FilesScreen(
                            state = state.Commit,
                            onAction = onAction
                        )
                    }

                    1 -> CommentsScreen(
                        state = state.CommitComments,
                        onNavigate = onNavigate,
                        onAction = onAction,
                        onCurrentSheetChanged = {
                            onCurrentSheetChanged(it)
                            scope.launch {
                                if (sheetScaffoldState.bottomSheetState.isCollapsed) {
                                    sheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    sheetScaffoldState.bottomSheetState.collapse()
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
private fun FilesScreen(
    state: Resource<CommitModel>, onAction: (String, String?) -> Unit
) {
    when (state) {
        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Loading ...", color = (MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }

        is Resource.Success -> {
            val files = state.data!!.files

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(files) { file ->
                    ExpandableCard(file = file, onAction = onAction)
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
                Text(
                    text = "Something went wrong !",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CommentsScreen(
    state: Resource<CommitCommentsModel>,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: (CommitScreenSheets) -> Unit,
    onNavigate: (Int, String?, Int?) -> Unit
) {
    when (state) {

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
            val comments = state.data!!

            var textFieldValueState by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = ""
                    )
                )
            }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember { FocusRequester() }
            val context = LocalContext.current

            if (!comments.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        itemsIndexed(comments) { index, comment ->
                            CommentItem(
                                comment = comment,
                                onAction = onAction,
                                onReply = { userLogin ->
                                    if (userLogin != "N/A") {
                                        val newValue = textFieldValueState.text.plus("@$userLogin")
                                        textFieldValueState = TextFieldValue(
                                            text = newValue, selection = TextRange(newValue.length)
                                        )
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    } else {
                                        Toast.makeText(
                                            context, "Can't identify user !", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onCurrentSheetChanged = onCurrentSheetChanged,
                                onNavigate = onNavigate
                            )
                            if (index < comments.lastIndex) {
                                Divider(
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .height(0.5.dp)
                                        .padding(start = 6.dp, end = 6.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.End)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = textFieldValueState,
                            onValueChange = {
                                textFieldValueState = it
                            },
                            textStyle = TextStyle(fontSize = 16.sp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1F)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                        )

                        IconButton(onClick = {
                            if (textFieldValueState.text.length >= 2) {
                                onAction(
                                    "post_comment", textFieldValueState.text
                                )
                                textFieldValueState = TextFieldValue("")
                                keyboardController?.hide()
                            } else {
                                Toast.makeText(
                                    context, "Min length for comment is 2 !", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                tint = Color.Blue,
                                contentDescription = "search icon"
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No comments",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = textFieldValueState,
                            onValueChange = {
                                textFieldValueState = it
                            },
                            textStyle = TextStyle(fontSize = 16.sp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1F)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                        )

                        IconButton(onClick = {
                            if (textFieldValueState.text.length >= 2) {
                                onAction(
                                    "post_comment", textFieldValueState.text
                                )
                                textFieldValueState = TextFieldValue("")
                                keyboardController?.hide()
                            } else {
                                Toast.makeText(
                                    context, "Min length for comment is 2 !", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                tint = Color.Blue,
                                contentDescription = "search icon"
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
private fun CommentItem(
    comment: CommitCommentsModelItem,
    onAction: (String, String?) -> Unit,
    onReply: (String) -> Unit,
    onCurrentSheetChanged: (CommitScreenSheets) -> Unit,
    onNavigate: (Int, String?, Int?) -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
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
                    if (comment.user != null) {
                        comment.user.avatar_url
                    } else {
                        R.drawable.baseline_account_circle_24
                    }
                    //repository.owner.avatar_url
                }, // loading a network image using an URL.
                modifier = Modifier
                    .size(32.dp, 32.dp)
                    .clip(CircleShape)
                    .clickable { },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterStart,
                    contentDescription = "Actor Avatar"
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1F)
            ) {
                Row {
                    Text(
                        text = comment.user?.login ?: "N/A",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = ParseDateFormat.getTimeAgo(comment.created_at).toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (comment.author_association.lowercase(Locale.ROOT) != "none") {
                    Text(
                        text = comment.author_association.lowercase(Locale.ROOT),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_emoji),
                    contentDescription = "emoji",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Box {
                IconButton(
                    onClick = {
                        showMenu = !showMenu
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow),
                        contentDescription = "more option",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                        onClick = {
                            onNavigate(
                                R.id.action_commitFragment_to_editCommentFragment,
                                comment.body,
                                comment.id
                            )
                            showMenu = false
                        })
                    DropdownMenuItem(text = {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, onClick = {
                        onCurrentSheetChanged(
                            CommitScreenSheets.CommitDeleteRequestSheet(
                                comment.id
                            )
                        )
                        onAction("delete", "")
                        showMenu = false
                    })
                    DropdownMenuItem(text = {
                        Text(
                            text = "Reply",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, onClick = {
                        onReply(comment.user?.login ?: "N/A")
                        showMenu = false
                    })
                    DropdownMenuItem(text = {
                        Text(
                            text = "Share",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, onClick = {
                        onAction("share", comment.html_url)
                        showMenu = false
                    })
                }
            }
        }

        Text(
            text = comment.body,
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp, end = 8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun TitleHeader(
    repo: String,
    state: Resource<CommitModel>,
    onNavigate: (Int, String?, Int?) -> Unit,
    onCurrentSheetChanged: (CommitScreenSheets) -> Unit
) {
    when (state) {

        is Resource.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.baseline_account_circle_24),
                        contentDescription = "avatar icon",
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .size(48.dp, 48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )

                    Spacer(
                        modifier = Modifier
                            .width(24.dp)
                            .width(6.dp)
                    )
                }
            }
        }

        is Resource.Success -> {
            val commit = state.data!!

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    GlideImage(
                        failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
                        imageModel = {
                            if (commit.author != null) {
                                commit.author.avatar_url
                            } else {
                                R.drawable.baseline_account_circle_24
                            }
                        }, // loading a network image using an URL.
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .size(48.dp, 48.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (commit.author != null) {
                                    onNavigate(
                                        R.id.action_commitFragment_to_profileFragment,
                                        commit.author.login,
                                        null
                                    )
                                }
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
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1F)
                    ) {
                        Text(
                            text = commit.commit.message,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(
                                text = repo,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ParseDateFormat.getTimeAgo(commit.commit.committer.date)
                                    .toString(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onCurrentSheetChanged(CommitScreenSheets.CommitInfoSheet)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info_outline),
                            contentDescription = "info",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = 12.dp, top = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.baseline_account_circle_24),
                        contentDescription = "avatar icon",
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .size(48.dp, 48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Spacer(modifier = Modifier.height(4.dp))

                        Row {

                            Spacer(modifier = Modifier.width(6.dp))

                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

    }
}

@Composable
private fun Toolbar(
    state: Resource<CommitModel>,
    onNavigate: (Int, String?, Int?) -> Unit,
    onAction: (String, String?) -> Unit,
) {
    when (state) {
        is Resource.Loading -> {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = { }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_document),
                            contentDescription = "changes",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "add",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }


                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "clear",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }

                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "more option",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        is Resource.Success -> {
            var showMenu by remember { mutableStateOf(false) }

            val commit = state.data!!

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = { onNavigate(-1, null, null) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_document),
                            contentDescription = "changes",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = commit.files.size.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "add",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = commit.stats.additions.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "clear",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = commit.stats.deletions.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }

                Box {
                    IconButton(
                        onClick = {
                            showMenu = !showMenu
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "more option",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(text = {
                            Text(
                                text = "Share",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("share", commit.html_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "Open in browser",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("browser", commit.html_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "Copy URL",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("copy", commit.html_url)
                            showMenu = false
                        })

                        DropdownMenuItem(text = {
                            Text(
                                text = "Copy SHA-1",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("copy", commit.sha)
                            showMenu = false
                        })

                    }
                }

            }
        }

        is Resource.Failure -> {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_document),
                            contentDescription = "changes",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "add",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = "clear",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }

                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "more option",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
