package com.movie.app

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


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