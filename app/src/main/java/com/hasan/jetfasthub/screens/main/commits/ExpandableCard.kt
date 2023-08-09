package com.hasan.jetfasthub.screens.main.commits

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.screens.main.commits.models.commit_model.File

@Composable
fun ExpandableCard(file: File, onAction: (String, String?) -> Unit) {
    var expandableState by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(targetValue = if (expandableState) 180f else 0f)
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .clickable { expandableState = !expandableState },
        shadowElevation = 10.dp,
    ) {
        Column {

            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    text = file.filename,
                    modifier = Modifier.weight(1F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .rotate(rotationState),
                    onClick = { expandableState = !expandableState }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown_icon),
                        contentDescription = "dropdown icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(text = {
                            Text(
                                text = "Open",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }, onClick = {
                            onAction("browser", file.blob_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "Download",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }, onClick = {
                            onAction("download", file.blob_url)
                            showMenu = false
                        })
                        DropdownMenuItem(text = {
                            Text(
                                text = "Share",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }, onClick = {
                            onAction("share", file.blob_url)
                            showMenu = false
                        })

                        DropdownMenuItem(text = {
                            Text(
                                text = "Copy URL",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                            onClick = {
                                onAction("copy", file.blob_url)
                                showMenu = false
                            })

                    }
                }

            }

            if (expandableState) {
                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                ) {

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
                    Text(text = "Changes", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = file.changes.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Addition", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = file.additions.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Deletion", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = file.deletions.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Status", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = file.status,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

        }
    }

}
