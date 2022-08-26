package com.movie.app

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.movie.app.room.MovieRoom

@Dao
interface MovieDao {

    @Insert
    fun insertMovie(movie: MovieRoom)

    @Query("SELECT * FROM movies WHERE movieData = :name")
    fun findMovie(name: String): List<MovieRoom>

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<MovieRoom>>

    @Query("UPDATE movies SET isFav = :newVal WHERE imageUrl =:imageURL")
    fun updateFav(newVal: Boolean, imageURL: String)

    @Query("UPDATE movies SET isLater = :newVal WHERE imageUrl =:imageURL")
    fun updateLater(newVal: Boolean, imageURL: String)
}

@Database(entities = [(MovieRoom::class)], version = 1)
abstract class MovieRoomDatabase: RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {

        private var INSTANCE: MovieRoomDatabase? = null

        fun getInstance(context: Context): MovieRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MovieRoomDatabase::class.java,
                        "movie_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}