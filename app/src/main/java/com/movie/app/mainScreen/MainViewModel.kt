package com.movie.app.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.movie.app.mainScreen.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val loadingStates = MutableStateFlow(LoadingState.IDLE)

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingStates.emit(LoadingState.LOADING)
            Firebase.auth.signInWithCredential(credential)
            loadingStates.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingStates.emit(LoadingState.error(e.localizedMessage))
        }
    }
}