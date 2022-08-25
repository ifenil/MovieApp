package com.movie.app.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class LoadingStates constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingStates(Status.SUCCESS)
        val IDLE = LoadingStates(Status.IDLE)
        val LOADING = LoadingStates(Status.RUNNING)
        val LOGGED_IN = LoadingStates(Status.LOGGED_IN)
        fun error(msg: String?) = LoadingStates(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
        LOGGED_IN
    }
}

class MainViewModel : ViewModel() {
    val loadingStates = MutableStateFlow(LoadingStates.IDLE)

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingStates.emit(LoadingStates.LOADING)
            Firebase.auth.signInWithCredential(credential)
            loadingStates.emit(LoadingStates.LOADED)
        } catch (e: Exception) {
            loadingStates.emit(LoadingStates.error(e.localizedMessage))
        }
    }
}