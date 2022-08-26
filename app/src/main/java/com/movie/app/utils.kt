package com.movie.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.movie.app.room.RoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class LoginManager(private val context: Context) {

    suspend fun setLoginData(value: Boolean){
        context.loginDataStore.edit { preferences ->
            preferences[IS_LOGGED] = value
        }
    }

    val counter : Flow<Boolean>
        get() = context.loginDataStore.data.map { preferences ->
            preferences[IS_LOGGED] ?: false
        }

    companion object {
        private const val DATASTORE_NAME = "MovieApp"

        private val IS_LOGGED = booleanPreferencesKey("isLogged");

        private val Context.loginDataStore by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }
}

data class Movie(val name: String,
                 val imageUrl: String,
                 val category: String,
                 val movieBanner: String,
                 val description: String,
                 val releaseDate: String,
                 val userRating: String,
                 val isFav: MutableState<Boolean> = mutableStateOf(false),
                 val isLater: MutableState<Boolean> = mutableStateOf(false))

fun logout(context: Context, viewModel: RoomViewModel) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val loginManager = LoginManager(context)

    coroutineScope.launch(Dispatchers.IO) {
        viewModel.deleteALL()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
        loginManager.setLoginData(false)

        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }
}