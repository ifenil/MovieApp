package com.movie.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
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


fun createNotification(context: Context) {

    createNotificationChannel(context)
    val channelId = "all_notifications"
    val intent = Intent(context, ContactsContract.Profile::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    val builder =
        NotificationCompat.Builder(context, channelId) // Create notification with channel Id
            .setSmallIcon(R.drawable.ic_movie)
            .setContentTitle("MovieeApp")
            .setContentText("Movie is Playing")
            .setPriority(NotificationCompat.PRIORITY_MAX)
    builder.setContentIntent(pendingIntent).setAutoCancel(true)
    val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    with(mNotificationManager) {
        notify(123, builder.build())

    }
}

fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "all_notifications"
        val mChannel = NotificationChannel(
            channelId,
            "General Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mChannel.description = "This is default channel used for all other notifications"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}
