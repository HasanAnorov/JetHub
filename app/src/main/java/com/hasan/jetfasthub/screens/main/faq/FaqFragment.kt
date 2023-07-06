package com.hasan.jetfasthub.screens.main.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class FaqFragment : Fragment() {

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
private fun MainContent() {
    Surface() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "PLEASE READ!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Red
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp), color = Color.Black
            )
            Text(text = buildAnnotatedString {

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("• Why can't I see either my Private or my Public Organizations\n")
                }
                append("\nOpen up https://github.com/settings/applications and look for FastHub, open it up, scroll down to \"Organization access\" and click on the \"Grant\" button. Alternatively you can sign in using Access Token which will ease the process.\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("\nI have tried signing in with an Access Token and a One-Time Password (OTP) without any luck. \nWhat's wrong?\n")
                }
                append("\nYou can't sign in using Access Token and OTP all together. Due to the limited lifetime of such passwords you'll have to repeat a sign in procedure every few seconds.\n")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("\nI have tried signing in with an Access Token and a One-Time Password (OTP) without any luck. \nWhat's wrong?\n")
                }
                append("\nYou can't sign in using Access Token and OTP all together. Due to the limited lifetime of such passwords you'll have to repeat a sign in procedure every few seconds.\n")

            }
            )

            Spacer(Modifier.weight(1F))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(text = "Done", modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
            }
        }
    }
}