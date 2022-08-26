package com.movie.app.homeScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.movie.app.Movie

class MovieViewModel: ViewModel() {
    val responseTxt = MutableLiveData<List<Movie>>()

}