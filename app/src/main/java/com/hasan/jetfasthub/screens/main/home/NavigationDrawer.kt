package com.hasan.jetfasthub.screens.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.screens.main.home.data.remote.user_model.GitHubUser
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Constants
import com.hasan.jetfasthub.utility.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun DrawerHeader(user: Resource<GitHubUser>) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {

        GlideImage(
            failure = { painterResource(id = R.drawable.baseline_account_circle_24) },
            imageModel = { user.data?.avatar_url },
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            imageOptions = ImageOptions(
                contentDescription = "avatar picture",
                contentScale = ContentScale.Crop,
            )
        )

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(start = 24.dp)
        ) {
            Text(user.data?.name.toString(), color = MaterialTheme.colorScheme.onSurface)
            Text(user.data?.login.toString(), color = MaterialTheme.colorScheme.onSurface)
        }
    }

}

@Composable
fun DrawerBody(
    closeDrawer: () -> Unit,
    username: String,
    onLogout: () -> Unit,
    onNavigate: (Int, String?, String?) -> Unit
) {

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("MENU", "PROFILE")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        if (tabIndex == index) {
                            Text(title, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        } else {
                            Text(title, color = MaterialTheme.colorScheme.outline)
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
            0 -> DrawerMenuScreen(closeDrawer, username, onNavigate)
            1 -> DrawerProfileScreen(username, onNavigate, onLogout)
        }
    }

}


@Composable
fun DrawerMenuScreen(
    closeDrawer: () -> Unit, username: String, onNavigate: (Int, String?, String?) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    closeDrawer()
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_home_24),
                contentDescription = "home icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Home",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface)
                ) {
                    closeDrawer()
                    onNavigate(R.id.action_homeFragment_to_profileFragment, username, null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = "Profile icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Profile",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_people_alt_24),
                contentDescription = "Organizations icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Organizations",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_notificationsFragment, null, null)
                }) {
            Image(
                painter = painterResource(id = R.drawable.baseline_notifications_24),
                contentDescription = "Notifications icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Notifications",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_pinnedFragment, null, null)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_bookmark_24),
                contentDescription = "Pinned icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Pinned",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_trending_up_24),
                contentDescription = "Trending icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Trending",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_gistsFragment, username, null)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_code_24),
                contentDescription = "Gists icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Gists",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(
                        R.id.action_homeFragment_to_repositoryFragment,
                        Constants.JetHubOwner,
                        Constants.JetHubRepoName
                    )
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fasthub_mascot),
                contentDescription = "JetHub icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "JetHub",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { onNavigate(R.id.action_homeFragment_to_faqFragment, null, null) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "FAQ icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "FAQ",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_settingsFragment, null, null)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "Setting icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Setting",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_money),
                contentDescription = "Restore Purchases icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Restore Purchases",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_aboutFragment, null, null)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "About icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "About",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DrawerProfileScreen(
    username: String, onNavigate: (Int, String?, String?) -> Unit, onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { onLogout() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Logout",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    //implement action
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Account icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Add Account",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_profileFragment, username, "2")
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_book_24),
                contentDescription = "Repositories icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Repositories",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) {
                    onNavigate(R.id.action_homeFragment_to_profileFragment, username, "3")
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_star_24),
                contentDescription = "Starred icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Starred",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(top = 4.dp, bottom = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
                ) { onNavigate(R.id.action_homeFragment_to_pinnedFragment, null, null) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
                contentDescription = "Pinned icon",
                modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
            Text(
                text = "Pinned",
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun Tests() {
    JetFastHubTheme(isDarkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = Color.Red),
                    ) {
                        //closeDrawer()
                    }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_home_24),
                    contentDescription = "home icon",
                    modifier = Modifier.padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = "Home",
                    modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

