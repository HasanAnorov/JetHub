package com.hasan.jetfasthub.screens.main.commits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class CommitFragment : Fragment() {

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
private fun MainContent() {

    Scaffold(
        topBar = {
            Column(Modifier.fillMaxWidth()) {
                TitleHeader(
                    onNavigate = { dest, data, extra -> },
                    onCurrentSheetChanged = { }
                )
                Toolbar(
                    onNavigate = { dest, data, extra -> },
                    onAction = { action, data -> },
                    onCurrentSheetChanged = { },
                    onLicenseClicked = { }
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
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index, onClick = { tabIndex = index },
                        text = {
                            if (tabIndex == index) {
                                Text(title, color = Color.Blue)
                            } else {
                                Text(title, color = Color.Black)
                            }
                        },
                    )
                }
            }

            when (tabIndex) {
                0 -> {
                    FilesScreen()
                }
                1 -> CommentsScreen()
            }
        }
    }
}

@Composable
private fun FilesScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val comments = listOf("", "")
        itemsIndexed(comments) { index, comment ->
            CommentItemCard(comment)
        }
    }
}

@Composable
private fun CommentItemCard(comment: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = { }),
        elevation = 10.dp,
        backgroundColor = Color.White
    ) {
        Column {

            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Text(text = "html/kotlin has been used")

                Spacer(Modifier.weight(1F))

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown_icon),
                        contentDescription = "dropdown icon"
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "options menu")
                }
            }

            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .height(0.5.dp)
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Changes")
                    Text(text = "265")
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Addition")
                    Text(text = "265")
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Deletion")
                    Text(text = "5")
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Status")
                    Text(text = "modified")
                }
            }
            
        }
    }
}

@Composable
private fun CommentsScreen() {

}

@Composable
private fun TitleHeader(
    onNavigate: (Int, String?, String?) -> Unit,
    onCurrentSheetChanged: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

//            GlideImage(
//                failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
//                imageModel = {
//                    //repository.owner.avatar_url
//                }, // loading a network image using an URL.
//                modifier = Modifier
//                    .size(48.dp, 48.dp)
//                    .size(48.dp, 48.dp)
//                    .clip(CircleShape)
//                    .clickable {
////                        onNavigate(
////                            R.id.action_commitFragment_to_profileFragment,
////                            repository.owner.login,
////                            null
////                        )
//                    },
//                imageOptions = ImageOptions(
//                    contentScale = ContentScale.Crop,
//                    alignment = Alignment.CenterStart,
//                    contentDescription = "Actor Avatar"
//                )
//            )

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
                Text(
                    text = "repository.full_name",
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "compose-multiplatform",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Time should be",
                        fontSize = 14.sp,
                        color = Color.Gray
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

    }
}

@Composable
private fun Toolbar(
    onNavigate: (Int, String?, String?) -> Unit,
    onAction: (String, String?) -> Unit,
    onCurrentSheetChanged: () -> Unit,
    onLicenseClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {

        IconButton(onClick = { onNavigate(-1, null, null) }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
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
                    tint = Color.Black
                )
                Text(text = "42")
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "add",
                )
                Text(text = "51")
            }


            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = "clear"
                )
                Text(text = "17")
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
                    onAction("share", "repository.html_url")
                    showMenu = false
                })
                DropdownMenuItem(text = { Text(text = "Open in browser") }, onClick = {
                    onAction("browser", "repository.html_url")
                    showMenu = false
                })
                DropdownMenuItem(text = { Text(text = "Copy URL") }, onClick = {
                    onAction("copy", "repository.html_url")
                    showMenu = false
                })

                DropdownMenuItem(text = { Text(text = "Copy SHA-1") },
                    onClick = {
                        onAction("copy", "copy sha-1 ")
                        showMenu = false
                    })

            }
        }

    }
}