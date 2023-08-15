package com.hasan.jetfasthub.screens.main.issue

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.buildAnnotatedString
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
import com.hasan.jetfasthub.screens.main.issue.comments_model.IssueCommentsModel
import com.hasan.jetfasthub.screens.main.issue.comments_model.IssueCommentsModelItem
import com.hasan.jetfasthub.screens.main.issue.issue_model.IssueModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class IssueFragment : Fragment() {

    private val issueViewModel: IssueViewModel by viewModel()
    private lateinit var token: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        token = PreferenceHelper.getToken(requireContext())

        val owner = arguments?.getString("issue_owner")
        val repo = arguments?.getString("issue_repo")
        val issueNumber = arguments?.getString("issue_number")

        if (issueNumber != null && owner != null && repo != null) {
            issueViewModel.init(owner, repo, issueNumber)
            issueViewModel.getIssue(token, owner, repo, issueNumber)
            issueViewModel.getComments(token, repo, owner)
        } else {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                JetFastHubTheme {
                    val state by issueViewModel.state.collectAsState()
                    MainContent(
                        state = state,
                        onAction = { action, data ->
                            when (action) {

                                "post_comment" -> {
                                    issueViewModel.postComment(
                                        token = token,
                                        owner = state.IssueOwner,
                                        repo = state.IssueRepo,
                                        issueNumber = state.IssueNumber,
                                        body = data!!
                                    ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                        .onEach { response ->
                                            if (response) {
                                                issueViewModel.getComments(
                                                    token = token,
                                                    owner = state.IssueOwner,
                                                    repo = state.IssueRepo,
                                                )
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Can't post a comment",
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

                                "delete_comment" -> {
                                    issueViewModel.deleteComment(
                                        token = token,
                                        owner = state.IssueOwner,
                                        repo = state.IssueRepo,
                                        commentId = data!!.toInt()
                                    ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                        .onEach { response ->
                                            if (response) {
                                                issueViewModel.getComments(
                                                    token = token,
                                                    owner = state.IssueOwner,
                                                    repo = state.IssueRepo,
                                                )
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Can't delete a comment!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }.launchIn(lifecycleScope)
                                }

                            }
                        },
                        onNavigate = { dest, data, _ ->
                            when (dest) {

                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                R.id.action_issueFragment_to_profileFragment -> {
                                    val bundle = bundleOf("username" to data)
                                    findNavController().navigate(dest, bundle)
                                }

                            }
                        },
                        onCurrentSheetChanged = {
                            issueViewModel.changeCurrentSheet(it)
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
    state: IssueScreenState,
    onAction: (String, String?) -> Unit,
    onNavigate: (Int, String?, Int?) -> Unit,
    onCurrentSheetChanged: (IssueScreenSheets) -> Unit
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
                IssueScreenSheets.IssueInfoSheet -> {
                    when (state.Issue) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            val issue = state.Issue.data!!
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.inverseOnSurface),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(state.IssueOwner)
                                        append("/")
                                        append(state.IssueRepo)
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (issue.title != null) {
                                    Text(
                                        text = issue.title,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(onClick = {
                                        closeSheet()
                                    }) {
                                        Text(text = "OK")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            }
                        }

                        is Resource.Failure -> {}
                    }
                }

                is IssueScreenSheets.CommentDeleteSheet -> {
                    val commentId = state.CurrentSheet.commentId

                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Are you sure ?",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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

                is IssueScreenSheets.UnlockIssueSheet -> {

                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { sheetPadding ->
        Scaffold(
            modifier = Modifier.padding(sheetPadding),
            topBar = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    TitleHeader(
                        state = state.Issue,
                        onNavigate = onNavigate,
                        onCurrentSheetChanged = { currentSheet ->
                            onCurrentSheetChanged(currentSheet)
                            scope.launch {
                                if (sheetScaffoldState.bottomSheetState.isCollapsed) {
                                    sheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    sheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        }
                    )
                    Toolbar(
                        state = state,
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
            },
        ) { paddingValues ->
            CommentsScreen(
                paddingValues,
                state = state.Comments,
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

@Composable
private fun TitleHeader(
    state: Resource<IssueModel>,
    onNavigate: (Int, String?, Int?) -> Unit,
    onCurrentSheetChanged: (IssueScreenSheets) -> Unit
) {
    when (state) {

        is Resource.Loading -> {
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
            val issue = state.data!!

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
                            if (issue.user != null) {
                                issue.user.avatar_url
                            } else {
                                R.drawable.baseline_account_circle_24
                            }
                        }, // loading a network image using an URL.
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .size(48.dp, 48.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (issue.user != null) {
                                    onNavigate(
                                        R.id.action_issueFragment_to_profileFragment,
                                        issue.user.login,
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
                            text = issue.title ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            if (issue.state == "closed") {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_issue_closed_small),
                                    contentDescription = null
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Closed")
                                        append(" ")
                                        append("by")
                                        append(" ")
                                        append(issue.closed_by.login)
                                        append(" ")
                                        append(
                                            ParseDateFormat.getTimeAgo(issue.closed_at.toString())
                                                .toString()
                                        )
                                    },
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_issue_opened_small),
                                    contentDescription = null
                                )

                                Text(
                                    text = buildAnnotatedString {
                                        append("Opened")
                                        append(" ")
                                        append("by")
                                        append(" ")
                                        append(issue.user?.login)
                                        append(" ")
                                        append(ParseDateFormat.getTimeAgo(issue.created_at))
                                    },
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            onCurrentSheetChanged(IssueScreenSheets.IssueInfoSheet)
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

                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

    }
}

@Composable
private fun Toolbar(
    state: IssueScreenState,
    onNavigate: (Int, String?, Int?) -> Unit,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: (IssueScreenSheets) -> Unit
) {
    when (state.Issue) {
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
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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
            var showEditMenu by remember { mutableStateOf(false) }
            val issue = state.Issue.data!!

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {

                IconButton(onClick = { onNavigate(-1, null, null) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = "#${issue.number}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.IssueOwner,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(
                        onClick = {
                            showEditMenu = !showEditMenu
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "edit option",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showEditMenu,
                        onDismissRequest = { showEditMenu = false },
                    ) {
                        when (issue.author_association) {
                            "OWNER" -> {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_edit),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Edit",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        //navigate to edit screen
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_lock),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Lock conversation",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onAction("lock_conversation", null)
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_info_outline),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Close",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onCurrentSheetChanged(IssueScreenSheets.UnlockIssueSheet("2"))
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_label),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Labels",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        //navigate to edit screen
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_milestone),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Milestone",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        //your action
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_person_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Assignees",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        //your action
                                        showEditMenu = false
                                    }
                                )
                            }

                            "NONE" -> {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_edit),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Edit",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        //navigate to edit screen
                                        showEditMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_info_outline),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Reopen",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onAction("lock_conversation", null)
                                        showEditMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = "Back button",
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
                            onAction("share", issue.html_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "Open in browser",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("browser", issue.html_url)
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

                Spacer(modifier = Modifier.weight(1F))

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CommentsScreen(
    contentPaddingValues: PaddingValues,
    state: Resource<IssueCommentsModel>,
    onAction: (String, String?) -> Unit,
    onNavigate: (Int, String?, Int?) -> Unit,
    onCurrentSheetChanged: (IssueScreenSheets) -> Unit
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
                        .padding(contentPaddingValues)
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
                                onNavigate = onNavigate,
                                onCurrentSheetChanged = onCurrentSheetChanged
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
                            .background(MaterialTheme.colorScheme.surface.copy(0.8F)),
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
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        )

                        IconButton(
                            onClick = {
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
                            }
                        ) {
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
                        .padding(contentPaddingValues)
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
                            .background(MaterialTheme.colorScheme.surface.copy(0.8F)),
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
    comment: IssueCommentsModelItem,
    onAction: (String, String?) -> Unit,
    onReply: (String) -> Unit,
    onNavigate: (Int, String?, Int?) -> Unit,
    onCurrentSheetChanged: (IssueScreenSheets) -> Unit
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
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Edit",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            //navigate to edit screen with body
                            showMenu = false
                        }
                    )

                    if (comment.author_association == "OWNER") {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Delete",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }, onClick = {
                                onCurrentSheetChanged(
                                    IssueScreenSheets.CommentDeleteSheet(
                                        comment.id
                                    )
                                )
                                showMenu = false
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Reply",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onReply(comment.user?.login ?: "N/A")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Share",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onAction("share", comment.html_url)
                            showMenu = false
                        }
                    )
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

