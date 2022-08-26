package com.movie.app.room

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movie.app.MovieRepository
import com.movie.app.MovieRoomDatabase

class RoomViewModel(application: Application) : ViewModel() {

    val allProducts: LiveData<List<MovieRoom>>
    private val repository: MovieRepository
    val searchResults: MutableLiveData<List<MovieRoom>>

    init {
        val productDb = MovieRoomDatabase.getInstance(application)
        val productDao = productDb.movieDao()
        repository = MovieRepository(productDao)

        allProducts = repository.allProducts
        searchResults = repository.searchResults
    }

    fun insertMovie(movie: MovieRoom) {
        repository.insertMovie(movie)
    }

    fun findMovie(name: String) {
        repository.findMovie(name)
    }

    fun updateIsFav(isFav: Boolean, imageURL: String) {
        repository.updateIsFav(isFav, imageURL)
    }

    fun updateIsLater(isLater: Boolean, imageURL: String) {
        repository.updateIsLater(isLater, imageURL)
    }
}

class RoomViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomViewModel(application) as T
    }
}