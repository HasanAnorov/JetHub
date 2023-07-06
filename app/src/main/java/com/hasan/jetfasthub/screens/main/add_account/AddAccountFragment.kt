package com.hasan.jetfasthub.screens.main.add_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class AddAccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetFastHubTheme {
                    MainContent(
                        onBackPressed = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }

}

@Composable
private fun MainContent(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = { TopAppBarContent(onBackPressed)}
    ) { paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {

            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group),
                        contentDescription = "group icon"
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                        Text(
                            text = "Enterprise",
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Login to unlimited number of Enterprise & GitHub accounts\n• Access GitHub contents along side your Enterprise Account",
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_book_24),
                        contentDescription = "group icon"
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                        Text(
                            text = "PRO features",
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "A GitHub Enterprise appliance is entirely separate from GitHub.com, and will not have your OAuth application configured.\n \n" +
                                    "You will need to setup your OAuth application again for FastHub on the GitHub Enterprise appliance, which will provide you\n" +
                                    "with new client_id and client_secret credentials. This will have to be completed for each new GitHub Enterprise appliance.\n \n" +
                                    "Your application communicates with. GitHub Enterprise comes preconfigured with the GitHub Desktop OAuth application set up,\n" +
                                    "this is how GitHub Desktop is able to complete the authentication successfully for GitHub Enterprise appliances.",
                            modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }

                Spacer(modifier = Modifier.weight(1F))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Purchase JetHub Premium",
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBarContent(
    onBackPressed: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        shadowElevation = 9.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
            }

            Text(
                color = Color.Black,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 10.dp, end = 10.dp),
                text = "About",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}