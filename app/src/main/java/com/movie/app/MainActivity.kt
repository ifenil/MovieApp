package com.movie.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.movie.app.LoadingState.Companion.LOADING
import com.movie.app.ui.theme.MovieAppTheme

class MainActivity : ComponentActivity() {
    private val loginViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    GoogleSignInComponent(loginViewModel = loginViewModel)
                }
            }
        }
    }
}

@Composable
fun GoogleSignInComponent(loginViewModel: MainViewModel) {

    val state by loginViewModel.loadingState.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                loginViewModel.signWithCredential(credential)
            } catch (e: ApiException) {
                Log.e("TAG", "Google sign in failed", e)
            }
        }

    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            OutlinedButton(border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (state == LOADING) Arrangement.Center else Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            if (state == LOADING) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                    text = "Sign in with Google"
                                )
                            }
                        }
                    )
                })

            when (state.status) {
                LoadingState.Status.SUCCESS -> {

                    Text(text = "Success")
                }
                LoadingState.Status.FAILED -> {
                    Text(text = state.msg ?: "Error")
                }
                LoadingState.Status.LOGGED_IN -> {
                    Text(text = "Already Logged In")
                }
                else -> {
                }
            }
        }
    )
}

