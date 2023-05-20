package com.hasan.jetfasthub.screens.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.networking.GitHubHelper
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme

class LoginChooserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                //no state in this screen
                MainContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.white))
                        .padding(start = 30.dp, end = 30.dp),
                    navController = findNavController(),
                    requireContext()
                )
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    context: Context
) {

    JetFastHubTheme {
        Column(
            modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sign in using your GitHub account to use JetFastHub",
                fontWeight = FontWeight.W400,
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Choose your type",
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(R.id.action_loginChooserFragment_to_basicAuthFragment)
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(.08f))
            ) {
                Text(
                    text = "Basic Authentication", modifier = Modifier
                        .padding(18.dp, 12.dp, 18.dp, 12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // to do
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(.08f))
            ) {
                Text(
                    text = "Access Token", modifier = Modifier
                        .padding(18.dp, 12.dp, 18.dp, 12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //to do
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(.08f))
            ) {
                Text(
                    text = "Enterprise", modifier = Modifier
                        .padding(18.dp, 12.dp, 18.dp, 12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //OR
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Red.copy(.08f))
            ) {
                Text(
                    text = "OR", modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, getAuthorizationUrl())
                context.startActivity(intent)
            }, modifier = Modifier.padding(12.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Authenticate with google",
                    modifier = Modifier
                        .size(72.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

private fun getAuthorizationUrl(): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority("github.com")
        .appendPath("login")
        .appendPath("oauth")
        .appendPath("authorize")
        .appendQueryParameter("client_id", GitHubHelper.CLIENT_ID)
        .appendQueryParameter("redirect_uri", GitHubHelper.REDIRECT_URL)
        .appendQueryParameter("scope", GitHubHelper.SCOPE)
        .appendQueryParameter("state", GitHubHelper.STATE)
        .build()
}
