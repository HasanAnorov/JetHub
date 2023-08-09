package com.hasan.jetfasthub.screens.main.gists

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
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
import com.hasan.jetfasthub.screens.main.gists.gist_comments_model.GistCommentsModel
import com.hasan.jetfasthub.screens.main.gists.gist_comments_model.GistCommentsModelItem
import com.hasan.jetfasthub.screens.main.gists.gist_model.GistFileModel
import com.hasan.jetfasthub.screens.main.gists.gist_model.GistModel
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.FileSizeCalculator
import com.hasan.jetfasthub.utility.ParseDateFormat
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GistFragment : Fragment() {

    private val gistViewModel: GistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val token = PreferenceHelper.getToken(requireContext())
        val gistId = arguments?.getString("gist_id")
        val gistOwner = arguments?.getString("gist_owner")

        if (gistId != null) {
            gistViewModel.getGist(token, gistId)
            gistViewModel.getGistComments(token, gistId)
            gistViewModel.hasGistStarred(token, gistId)
        } else {
            Toast.makeText(requireContext(), "Can't find gist !", Toast.LENGTH_SHORT).show()
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val state by gistViewModel.state.collectAsState()
                JetFastHubTheme {
                    MainContent(
                        state = state,
                        onNavigate = { dest, data, id ->
                            when (dest) {
                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                R.id.action_gistFragment_to_editCommentFragment -> {
                                    val bundle = Bundle()
                                    bundle.putString("destination", "GistFragment")
                                    bundle.putString("gist_id", gistId!!)
                                    bundle.putString("edit_comment", data!!)
                                    bundle.putInt("comment_id", id!!)
                                    findNavController().navigate(dest, bundle)
                                }

                                R.id.action_gistFragment_to_profileFragment -> {
                                    val bundle = bundleOf("username" to data)
                                    findNavController().navigate(
                                        dest, bundle
                                    )
                                }
                            }
                        },
                        onAction = { action, data ->
                            when (action) {

                                "delete_gist_comment" -> {
                                    gistViewModel.deleteGistComment(token, gistId!!, data!!.toInt())
                                        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                        .onEach {
                                            if (it) {
                                                gistViewModel.getGistComments(token, gistId)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Can't delete a gist comment !",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .launchIn(lifecycleScope)
                                }

                                "create_gist_comment" -> {
                                    gistViewModel.posGistComment(token, gistId!!, data!!)
                                        .flowWithLifecycle(
                                            lifecycle, Lifecycle.State.STARTED
                                        )
                                        .onEach { response ->
                                            if (response) {
                                                gistViewModel.getGistComments(token, gistId)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Can't post a gist comment !",
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

                                "delete_gist" -> {
                                    gistViewModel.deleteGist(token, gistId!!).flowWithLifecycle(
                                        lifecycle, Lifecycle.State.STARTED
                                    ).onEach { response ->
                                        if (response) {
                                            //delay for future animation :)
                                            delay(300)
                                            findNavController().popBackStack()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't delete a gist !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                                }

                                "fork_gist" -> {
                                    gistViewModel.forkGist(token, gistId!!).flowWithLifecycle(
                                        lifecycle, Lifecycle.State.STARTED
                                    ).onEach { response ->
                                        if (!response) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't fork a gist !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                                }

                                "change_fork_status" -> {
                                    gistViewModel.changeForkStatus()
                                }

                                "star_gist" -> {
                                    gistViewModel.starGist(token, gistId!!).flowWithLifecycle(
                                        lifecycle, Lifecycle.State.STARTED
                                    ).onEach { response ->
                                        if (!response) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't star a gist !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                                }

                                "un_star_gist" -> {
                                    gistViewModel.unstarGist(token, gistId!!).flowWithLifecycle(
                                        lifecycle, Lifecycle.State.STARTED
                                    ).onEach { response ->
                                        if (!response) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Can't unstar a gist !",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }.launchIn(lifecycleScope)
                                }

                            }
                        },
                        gistOwner = gistOwner!!,
                        onCurrentSheetChanged = {
                            gistViewModel.changeCurrentSheet(it)
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
    state: GistScreenState,
    onNavigate: (Int, String?, Int?) -> Unit,
    onAction: (String, String?) -> Unit,
    gistOwner: String,
    onCurrentSheetChanged: (GistScreenSheets) -> Unit
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
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
        sheetContent = {
            when (state.CurrentSheet) {
                GistScreenSheets.DeleteGistSheet -> {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(text = "Delete", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Are you sure ?")

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
                                onAction("delete_gist", null)
                                closeSheet()
                            }) {
                                Text(text = "Yes", color = Color.White)
                            }
                        }
                    }
                }

                is GistScreenSheets.DeleteGistCommentSheet -> {
                    val commentId = state.CurrentSheet.commentId
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(text = "Delete", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Are you sure ?")

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
                                onAction("delete_gist_comment", commentId.toString())
                                closeSheet()
                            }) {
                                Text(text = "Yes", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    ) { sheetPadding ->
        Scaffold(
            modifier = Modifier.padding(sheetPadding),
            topBar = {
                Column(Modifier.fillMaxWidth()) {
                    TitleHeader(
                        state = state.Gist,
                        onNavigate = onNavigate,
                    )
                    Toolbar(gistOwner = gistOwner,
                        state = state,
                        onNavigate = onNavigate,
                        onAction = onAction,
                        onDelete = {
                            onCurrentSheetChanged(GistScreenSheets.DeleteGistSheet)
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
                    modifier = Modifier.fillMaxWidth(),
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
                            state = state.Gist,
                            onAction = onAction
                        )
                    }

                    1 -> CommentsScreen(
                        state = state.GistComments,
                        onAction = onAction,
                        onCurrentSheetChanged = { sheet ->
                            onCurrentSheetChanged(sheet)
                            scope.launch {
                                if (sheetScaffoldState.bottomSheetState.isCollapsed) {
                                    sheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    sheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        },
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }
}

@Composable
private fun FilesScreen(
    state: Resource<GistModel>, onAction: (String, String?) -> Unit
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
            val files = state.data!!.files
            val listFiles = arrayListOf<GistFileModel>()
            files.forEach { (key, value) ->
                listFiles.add(GistFileModel(key, value.type, value.size, value.raw_url))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                items(listFiles) { file ->
                    GistFileItem(file, onAction)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CommentsScreen(
    state: Resource<GistCommentsModel>,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: (GistScreenSheets) -> Unit,
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
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .weight(1F)
                    ) {
                        itemsIndexed(comments) { index, comment ->
                            GistCommentItem(
                                comment = comment,
                                onCurrentSheetChanged = onCurrentSheetChanged,
                                onReply = { userLogin ->
                                    val newValue = textFieldValueState.text.plus("@$userLogin")
                                    textFieldValueState = TextFieldValue(
                                        text = newValue,
                                        selection = TextRange(newValue.length)
                                    )
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                },
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
                                    "create_gist_comment",
                                    textFieldValueState.text
                                )
                                textFieldValueState = TextFieldValue("")
                                keyboardController?.hide()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Min length for comment is 2 !",
                                    Toast.LENGTH_SHORT
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
                            .weight(1F),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No comments")
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

                        IconButton(
                            onClick = {
                                if (textFieldValueState.text.length >= 2) {
                                    onAction(
                                        "create_gist_comment",
                                        textFieldValueState.text
                                    )
                                    textFieldValueState = TextFieldValue("")
                                    keyboardController?.hide()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Min length for comment is 2 !",
                                        Toast.LENGTH_SHORT
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
private fun TitleHeader(
    state: Resource<GistModel>,
    onNavigate: (Int, String?, Int?) -> Unit,
) {
    when (state) {

        is Resource.Loading -> {
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1F)
                    ) {

                    }
                }
            }
        }

        is Resource.Success -> {
            val gist = state.data!!

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
                            gist.owner.avatar_url
                        }, // loading a network image using an URL.
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .size(48.dp, 48.dp)
                            .clip(CircleShape)
                            .clickable {
                                onNavigate(
                                    R.id.action_commitFragment_to_profileFragment,
                                    gist.owner.login,
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

                    val fileValues = gist.files.values

                    val fileName = if (gist.description == "" || gist.description == null) {
                        fileValues.elementAt(0).filename
                    } else {
                        gist.description
                    }

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1F)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(gist.owner.login)
                                }
                                append(" / ")
                                append(fileName)

                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(
                                text = ParseDateFormat.getTimeAgo(gist.created_at).toString(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (gist.history.size != 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "edited",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            val file = gist.files.values
                            val fileSize = file.elementAt(0).size
                            Log.d("ahi3646", "TitleHeader: $fileSize ")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = FileSizeCalculator.humanReadableByteCountBin(fileSize.toLong()),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        is Resource.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = 12.dp, top = 16.dp)
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
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1F)
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    gistOwner: String,
    state: GistScreenState,
    onNavigate: (Int, String?, Int?) -> Unit,
    onAction: (String, String?) -> Unit,
    onDelete: () -> Unit
) {
    when (state.Gist) {
        is Resource.Loading -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = {
                    onNavigate(-1, null, null)
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Gist",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    Modifier.weight(1F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {

                    IconButton(onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_browser),
                            contentDescription = "browser icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        //implement action
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "pin icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }


                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "more option",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }

        is Resource.Success -> {
            var showMenu by remember { mutableStateOf(false) }
            val gist = state.Gist.data!!

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = {
                    onNavigate(-1, null, null)
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Gist",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    Modifier.weight(1F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {

                    if (gist.owner.login == gistOwner) {
                        IconButton(onClick = {
                            //implement action
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (gist.owner.login != gistOwner) {
                        IconButton(onClick = {
                            if (state.HasForked) {
                                onAction("change_fork_status", null)
                            } else {
                                onAction("fork_gist", gist.id)
                            }
                        }) {
                            if (state.HasForked) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fork),
                                    contentDescription = null,
                                    tint = Color.Blue
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fork),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    IconButton(onClick = {
                        if (state.HasGistStarred) {
                            onAction("un_star_gist", null)
                        } else {
                            onAction("star_gist", null)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star_filled),
                            contentDescription = null,
                            tint = if (state.HasGistStarred) Color.Blue else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        onAction("browser", gist.html_url)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_browser),
                            contentDescription = "browser icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        //implement action
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "pin icon",
                            tint = MaterialTheme.colorScheme.onSurface
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
                            Icons.Filled.MoreVert,
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }, onClick = {
                            onAction("share", gist.html_url)
                            showMenu = false
                        })

                        if (gist.owner.login == gistOwner) {
                            DropdownMenuItem(text = {
                                Text(
                                    text = "Delete",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }, onClick = {
                                onDelete()
                                showMenu = false
                            })
                        }

                    }
                }

            }
        }

        is Resource.Failure -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 4.dp)
            ) {

                IconButton(onClick = {
                    onNavigate(-1, null, null)
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Gist",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    Modifier.weight(1F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {

                    IconButton(onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_browser),
                            contentDescription = "browser icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        //implement action
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "pin icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }


                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "more option",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun GistFileItem(fileModel: GistFileModel, onAction: (String, String?) -> Unit) {
    Surface(
        shadowElevation = 10.dp,
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                onAction("browser", fileModel.raw_url)
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(12.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = fileModel.fileName, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                color = Color.Gray
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(IntrinsicSize.Min),
            ) {
                Text(text = fileModel.textType, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.weight(1F))
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                )
                Text(
                    text = FileSizeCalculator.humanReadableByteCountBin(fileModel.fileSize.toLong()),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                )
                //didn't not get why fast hub not show this on even private gist ???
//                IconButton(
//                    onClick = { /*TODO*/ },
//                    modifier = Modifier
//                        .height(24.dp)
//                        .width(24.dp)
//                        .padding(4.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_edit),
//                        contentDescription = null
//                    )
//                }
//                IconButton(
//                    onClick = { /*TODO*/ },
//                    modifier = Modifier
//                        .height(24.dp)
//                        .width(24.dp)
//                        .padding(4.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_trash),
//                        contentDescription = null
//                    )
//                }
            }
        }
    }
}


@Composable
private fun GistCommentItem(
    onReply: (String) -> Unit,
    comment: GistCommentsModelItem,
    onCurrentSheetChanged: (GistScreenSheets) -> Unit,
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
                    comment.user.avatar_url
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

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = comment.user.login, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.weight(1F))
                Text(
                    text = comment.created_at, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface
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
                    DropdownMenuItem(text = { Text(text = "Edit", color = MaterialTheme.colorScheme.onPrimaryContainer) }, onClick = {
                        onNavigate(
                            R.id.action_gistFragment_to_editCommentFragment,
                            comment.body,
                            comment.id
                        )
                        showMenu = false
                    })
                    DropdownMenuItem(text = { Text(text = "Delete", color = MaterialTheme.colorScheme.onPrimaryContainer) }, onClick = {
                        onCurrentSheetChanged(
                            GistScreenSheets.DeleteGistCommentSheet(
                                comment.id
                            )
                        )
                        showMenu = false
                    })
                    DropdownMenuItem(text = { Text(text = "Reply", color = MaterialTheme.colorScheme.onPrimaryContainer) }, onClick = {
                        onReply(comment.user.login)
                        showMenu = false
                    })
                    DropdownMenuItem(text = { Text(text = "Share", color = MaterialTheme.colorScheme.onPrimaryContainer) }, onClick = {
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