package com.hasan.jetfasthub.screens.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetFastHubTheme {
                    MainContent(
                        onNavigate = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun MainContent(onNavigate: () -> Unit) {
    val state = rememberScaffoldState()
    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBarContent(onNavigate)
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(contentPadding),
            shadowElevation = 9.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "About",
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clickable {

                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Profile icon",
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                    )
                    androidx.compose.material.Text(
                        text = "Hasan Anorov",
                        modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clickable {

                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "Profile icon",
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                    )
                    Text(
                        text = "HasanAnorov",
                        modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clickable {

                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Profile icon",
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                    )
                    androidx.compose.material.Text(
                        text = "anoorvhasan.edu@gmail.com",
                        modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .clickable {

                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_contact_phone_24),
                        contentDescription = "Profile icon",
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                    )
                    androidx.compose.material.Text(
                        text = "+ 998 93 337 36 46",
                        modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp),
                        fontSize = 16.sp,
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
