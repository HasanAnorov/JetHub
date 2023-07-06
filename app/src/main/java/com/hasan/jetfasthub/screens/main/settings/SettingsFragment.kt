package com.hasan.jetfasthub.screens.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class SettingsFragment : Fragment() {

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
                            findNavController().navigate(R.id.action_aboutFragment_to_homeFragment)
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun MainContent(onNavigate: (Int) -> Unit) {
    val state = rememberScaffoldState()
    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBarContent(onNavigate)
        }
    ) { contentPadding ->

    }
}

@Composable
private fun TopAppBarContent(
    onBackPressed: (Int) -> Unit
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
            IconButton(onClick = {
                onBackPressed(R.id.action_settingsFragment_to_homeFragment)
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back button")
            }

            Text(
                color = Color.Black,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 10.dp, end = 10.dp),
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
