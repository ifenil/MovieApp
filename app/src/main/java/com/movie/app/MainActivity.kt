package com.movie.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.movie.app.LoadingState
import com.movie.app.R
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

    val state by loginViewModel.loadingStates.collectAsState()
    val statusText = remember { mutableStateOf("") }

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

    if (state == LOADING) {
            CircularProgressIndicator()
        } else {
            MainUI(
                onSignInClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                statusText = statusText
            )
        }

    when (state.status) {
                LoadingState.Status.SUCCESS -> {
                    statusText.value = "Success"
                }
                LoadingState.Status.FAILED -> {
                    statusText.value = "Error" + state.msg
                }
                LoadingState.Status.LOGGED_IN -> {
                    statusText.value = "Already Logged In"
                }
                else -> {
                }
            }
}

@Composable
fun MainUI(
    onSignInClick:() -> Unit = {},
    statusText: MutableState<String>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign In",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.DarkGray)
                .clickable { onSignInClick() }
                .padding(vertical = 10.dp, horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = statusText.value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}