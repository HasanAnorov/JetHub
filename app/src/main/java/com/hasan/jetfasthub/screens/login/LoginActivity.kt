package com.hasan.jetfasthub.screens.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hasan.jetfasthub.R
import com.hasan.jetfasthub.data.PreferenceHelper
import com.hasan.jetfasthub.screens.login.model.AccessTokenModel
import com.hasan.jetfasthub.screens.main.AppActivity
import com.hasan.jetfasthub.utility.Constants
import com.hasan.jetfasthub.utility.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

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
        Log.d("ahi3646", "onNewIntent: ")
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
                Log.d("ahi3646", "onHandleAuthIntent1 : tokenCode -  $tokenCode")
                if (tokenCode!!.isNotBlank()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.getAccessToken(code = tokenCode)
                        viewModel.accessTokenModel.observe(this@LoginActivity) {
                            handleLoginResult(it)
                        }
                    }
                } else {
                    Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLoginResult(status: Resource<Response<AccessTokenModel>>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                status.data.let { it ->
                    val token = it?.body()?.access_token.toString()
                    PreferenceHelper.saveToken(this, token)
                    navigateToMainScreen()
                }
            }

            is Resource.DataError -> {
                //binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    applicationContext,
                    "Credentials don't match records - $status",
                    Toast.LENGTH_SHORT
                ).show()
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