package com.hasan.jetfasthub.screens.main.file_view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

class FileViewFragment : Fragment() {

    private lateinit var fileName: String
    private val fileViewVM: FileViewVM by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val token = PreferenceHelper.getToken(context)

        val repoOwner = arguments?.getString("repo_owner")
        val repoName = arguments?.getString("repo_name")
        val filePath = arguments?.getString("file_path")
        val fileRef = arguments?.getString("file_ref")
        Log.d("ahi3646", "onAttach: $repoName $repoOwner $filePath $fileRef ")

        if (!repoOwner.isNullOrEmpty() && !repoName.isNullOrEmpty() && !filePath.isNullOrEmpty() && !fileRef.isNullOrEmpty()) {
            fileName = Uri.parse(filePath).lastPathSegment.toString()
            fileViewVM.getContentFile(
                token = token,
                owner = repoOwner,
                repo = repoName,
                path = filePath,
                ref = fileRef
            )
        } else {
            Toast.makeText(context, "Can't load data!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return ComposeView(requireContext()).apply {
            setContent {
                val state by fileViewVM.state.collectAsState()

                JetFastHubTheme {
                    MainContent(
                        state = state,
                        fileName = fileName,
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

                                    if (!data.startsWith("http://") && !data.startsWith("https://")) {
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

                                "download_file" -> {
                                    val message = Uri.parse(
                                        data
                                    ).lastPathSegment
                                    fileViewVM.downloadFile(
                                        data,
                                        message ?: "jethub_download"
                                    )
                                }

                            }
                        },
                        onBackPressed = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: FileViewScreenState,
    fileName: String,
    onAction: (String, String) -> Unit,
    onBackPressed: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    var showMenu by remember { mutableStateOf(false) }
    val checkBoxState = remember {
        mutableStateOf(false)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Surface(elevation = 9.dp) {
                TopAppBar(
                    title = {
                        Text(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            text = fileName,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onBackPressed()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
                        }
                    },
                    actions = {

                        IconButton(onClick = {
                            showMenu = !showMenu
                        }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "more option")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    showMenu = false
                                }
                            ) {
                                Text(
                                    text = "View as code",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Checkbox(
                                    checked = checkBoxState.value,
                                    onCheckedChange = { checkBoxState.value = it }
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    if(state.files.data!=null){
                                        onAction("download_file", state.files.data.downloadUrl)
                                    }
                                    showMenu = false
                                }
                            ) {
                                Text(
                                    text = "Download",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    if(state.files.data!=null){
                                        onAction("browser", state.files.data.htmlUrl)
                                    }
                                    showMenu = false
                                }
                            ) {
                                Text(
                                    text = "Open in browser",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    if(state.files.data!=null){
                                        onAction("copy", state.files.data.htmlUrl)
                                    }
                                    showMenu = false
                                }
                            ) {
                                Text(
                                    text = "Copy URL",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    if(state.files.data!=null){
                                        onAction("share", state.files.data.htmlUrl)
                                    }
                                    showMenu = false
                                }
                            ) {
                                Text(
                                    text = "Share",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                )
            }
        },
    ) { contentPadding ->
        when (state.files) {
            is Resource.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Loading ...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            is Resource.Success -> {
                val file = state.files.data!!
                val url by remember {
                    mutableStateOf(file.downloadUrl)
                }
                val webViewState = rememberWebViewState(url = url)
                val navigator = rememberWebViewNavigator()

                // A custom WebViewClient and WebChromeClient can be provided via subclassing
                val webClient = remember {
                    object : AccompanistWebViewClient() {
                        override fun onPageStarted(
                            view: WebView,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            Log.d("Accompanist WebView", "Page started loading for $url")
                        }
                    }
                }
                WebView(
                    state = webViewState,
                    navigator = navigator,
                    captureBackPresses = true,
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    client = webClient
                )
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
        Box(modifier = Modifier.padding(contentPadding)) {

        }
    }
}