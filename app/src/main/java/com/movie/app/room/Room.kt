package com.movie.app.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
class MovieRoom {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "movieId")
    var id: Int = 0

    @ColumnInfo(name = "movieData")
    var movieName: String = ""
    var imageUrl: String = ""
    var category: String = ""
    var movieBanner: String = ""
    var description: String = ""
    var releaseDate: String = ""
    var userRating: String = ""
    var isFav: Boolean = false
    var isLater: Boolean = false

    constructor() {}

    constructor(
        name: String,
        imageURL: String,
        category: String,
        movieBanner: String,
        description: String,
        releaseDate: String,
        userRating: String,
        isFav: Boolean,
        isLater: Boolean
    ) {
        this.id = id
        this.movieName = name
        this.imageUrl = imageURL
        this.category = category
        this.movieBanner = movieBanner
        this.description = description
        this.releaseDate = releaseDate
        this.userRating = userRating
        this.isFav = isFav
        this.isLater = isLater
    }
}