package com.hasan.jetfasthub.screens.login

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.main.AppActivity
import com.hasan.jetfasthub.utility.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                onHandleAuthIntent(intent = intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                onHandleAuthIntent(intent = intent)
            }
        }
    }

    private fun onHandleAuthIntent(intent: Intent?) {
        if (intent != null && intent.data != null) {
            val uri = intent.data
            if (uri.toString().startsWith(Constants.REDIRECT_URL)) {
                val tokenCode = uri!!.getQueryParameter("code")
                if (tokenCode!!.isNotBlank()) {
                    viewModel.changeStatus(UserLoadCase.Fetching)
                    viewModel.getAccessToken(tokenCode)
                        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                        .onEach {
                            if (it != "null") {
                                PreferenceHelper.saveToken(this, it)
                                viewModel.getAuthenticatedUser(it)
                                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                    .onEach { login ->
                                        if (login != "") {
                                            viewModel.changeStatus(UserLoadCase.Success)
                                            PreferenceHelper.saveAuthenticatedUser(this, login)
                                            navigateToMainScreen()
                                        } else {
                                            viewModel.changeStatus(UserLoadCase.Error)
                                        }
                                    }.launchIn(lifecycleScope)
                            } else {
                                viewModel.changeStatus(UserLoadCase.Error)
                            }
                        }
                        .launchIn(lifecycleScope)
                } else {
                    Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMainScreen() {
        Intent(this, AppActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        finish()
    }

}