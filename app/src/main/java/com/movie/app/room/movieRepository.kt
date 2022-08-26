package com.movie.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.movie.app.room.MovieRoom
import kotlinx.coroutines.*

class MovieRepository(private val movieDao: MovieDao) {


    val allProducts: LiveData<List<MovieRoom>> = movieDao.getAllMovies()
    val searchResults = MutableLiveData<List<MovieRoom>>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertMovie(newmovie: MovieRoom) {
        coroutineScope.launch(Dispatchers.IO) {
            movieDao.insertMovie(newmovie)
        }
    }

    fun findMovie(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    fun updateIsFav(isFav: Boolean, imageURL: String) {
        coroutineScope.launch(Dispatchers.IO) {
            movieDao.updateFav(isFav, imageURL)
        }
    }

    fun updateIsLater(isLater: Boolean, imageURL: String) {
        coroutineScope.launch(Dispatchers.IO) {
            movieDao.updateLater(isLater, imageURL)
        }
    }

    fun deleteALL() {
        coroutineScope.launch(Dispatchers.IO) {
            movieDao.deleteAll()
        }
    }

    private fun asyncFind(name: String): Deferred<List<MovieRoom>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async movieDao.findMovie(name)
        }
}