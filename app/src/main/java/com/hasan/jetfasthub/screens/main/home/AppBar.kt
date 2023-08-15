package com.hasan.jetfasthub.screens.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.RippleCustomTheme


@Composable
fun AppBar(
    onNavigationClick: () -> Unit,
    onToolbarItemCLick: (Int, String?, String?) -> Unit
) {
    CompositionLocalProvider(LocalRippleTheme provides RippleCustomTheme) {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                IconButton(
                    onClick = {
                        onNavigationClick()
                    }
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_menu_24),
                        contentDescription = "Localized description",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 10.dp, end = 10.dp),
                    text = "JetHub",
                    style = MaterialTheme.typography.titleLarge,
                )

                IconButton(onClick = {
                    onToolbarItemCLick(
                        R.id.action_homeFragment_to_notificationsFragment,
                        null,
                        null
                    )
                }) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notification",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    onToolbarItemCLick(R.id.action_homeFragment_to_searchFragment, null, null)
                }) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }
    }

}