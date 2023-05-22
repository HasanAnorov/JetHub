package com.hasan.jetfasthub.screens.login.basic_auth

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.ui.theme.JetFastHubTheme
import com.hasan.jetfasthub.utility.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class BasicAuthFragment : Fragment() {

    private val viewModel: BasicAuthViewModel by viewModel()

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
                val basicUiState by viewModel.uiState.collectAsState()
                MainContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.white))
                        .padding(24.dp),
                    basicUiState = basicUiState,
                    onUsernameChanged = viewModel::onUsernameChange,
                    onPasswordChanged = viewModel::onPasswordChange,
                    onPasswordVisibilityChanged = viewModel::onPasswordVisibilityChange,
                    requireContext()
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    basicUiState: BasicAuthUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityChanged: (visibility: Boolean) -> Unit,
    context: Context
) {
    JetFastHubTheme {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = basicUiState.userName,
                onValueChange = onUsernameChanged,
                label = { Text(text = "Username", color = Color.Blue) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Blue,
                ),
                placeholder = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = basicUiState.password,
                onValueChange = onPasswordChanged,
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChanged(!basicUiState.passwordVisibility) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_remove_red_eye_24),
                            contentDescription = "See password",
                            tint = if (basicUiState.passwordVisibility) MaterialTheme.colorScheme.primary
                            else Color.Blue.copy(.6f)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Password",
                        color = Color.Blue
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Blue,
                ),
                visualTransformation = if (basicUiState.passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = { Text(text = "Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(.08f))
            ) {
                Text(
                    text = "Login", modifier = Modifier
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

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, getAuthorizationUrl())
                        context.startActivity(intent)
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(.08f))
            ) {
                Text(
                    text = "Sign in with your default browser", modifier = Modifier
                        .padding(18.dp, 12.dp, 18.dp, 12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
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
        .appendQueryParameter("client_id", Constants.CLIENT_ID)
        .appendQueryParameter("redirect_uri", Constants.REDIRECT_URL)
        .appendQueryParameter("scope", Constants.SCOPE)
        .appendQueryParameter("state", Constants.STATE)
        .build()
}
