package com.hasan.jetfasthub.screens.main.pinned

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class PinnedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setContent {
                JetFastHubTheme {
                    MainContent(
                        onNavigate = { dest, data ->
                            when (dest) {
                                -1 -> {
                                    findNavController().popBackStack()
                                }

                                else -> {
                                    if (data != null) {
                                        val bundle = Bundle()
                                        bundle.putString("pinned_data", data)
                                        findNavController().navigate(dest, bundle)
                                    } else {
                                        findNavController().navigate(dest)
                                    }
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
private fun MainContent(onNavigate: (Int, String?) -> Unit) {
    Scaffold(
        topBar = { TopAppBarContent(onNavigate) }
    ) { paddingValues ->
        TabScreen(contentPaddingValues = paddingValues)
    }

}

@Composable
private fun TabScreen(
    contentPaddingValues: PaddingValues,
) {

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs =
        listOf("REPOSITORIES", "ISSUES", "PULL REQUESTS", "GISTS")

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            edgePadding = 0.dp,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        if (tabIndex == index) {
                            Text(
                                title,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(
                                title,
                                color = MaterialTheme.colorScheme.outline
                            )
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
            0 -> Repositories()
            1 -> Issues()
            2 -> PullRequests()
            3 -> Gists()
        }
    }
}

@Composable
fun Repositories() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material.Text(
            text = "No gists!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Issues() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material.Text(
            text = "No gists!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PullRequests() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material.Text(
            text = "No gists!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Gists() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material.Text(
            text = "No gists!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun TopAppBarContent(
    onBackPressed: (Int, String?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .fillMaxWidth()

        ) {
            IconButton(onClick = {
                onBackPressed(-1, null)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back button"
                )
            }

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 10.dp, end = 10.dp),
                text = "Pinned",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
