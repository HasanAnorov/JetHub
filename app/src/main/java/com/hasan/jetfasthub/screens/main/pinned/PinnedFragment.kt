package com.hasan.jetfasthub.screens.main.pinned

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class PinnedFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return ComposeView(requireContext()).apply {

            setContent {
                JetFastHubTheme() {
                    MainContent()
                }
            }
        }
    }

}

@Composable
private fun MainContent(){

}