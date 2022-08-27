package com.movie.app.homeScreen

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.movie.app.R
import com.movie.app.logout
import com.movie.app.room.MovieRoom
import com.movie.app.room.RoomViewModel
import com.movie.app.room.RoomViewModelFactory

@Composable
fun NavController.MovieUI() {
    val owner = LocalViewModelStoreOwner.current
    owner?.let {
        val viewModel: RoomViewModel = viewModel(
            it,
            "RoomViewModel",
            RoomViewModelFactory(
                LocalContext.current.applicationContext
                        as Application
            )
        )

        val allMovies by viewModel.allProducts.observeAsState(listOf())
        val searchResults by viewModel.searchResults.observeAsState(listOf())

        Column(
            modifier = Modifier
                .padding(top = 28.dp, bottom = 57.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            var movieName by remember { mutableStateOf("") }
            var searching by remember { mutableStateOf(false) }

            val onMovieTextChange = { text : String ->
                movieName = text
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = movieName,
                    placeholder = { Text(text = "Enter Movie Name") },
                    onValueChange = onMovieTextChange,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White, disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent),
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                )
                Text(
                    text = "Search",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.DarkGray)
                        .clickable {
                            searching = true
                            if (movieName.isEmpty()) searching = false
                            viewModel.findMovie(movieName)
                        }
                        .padding(horizontal = 6.dp, vertical = 9.dp)
                )
            }

            if (searching) {
                MovieList(allMovies = searchResults, viewModel = viewModel)
            } else MovieList(allMovies = allMovies, viewModel = viewModel)
        }
    }
}

@Composable
fun NavController.MovieList(allMovies: List<MovieRoom>, viewModel: RoomViewModel) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        allMovies.forEach {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .height(150.dp)
                        .background(Color.DarkGray)
                        .clickable {
                            navigate(
                                "watchFullMovie/${it.movieName}/${it.movieBanner.removeRange(0,1)}/${it.userRating}/${it.description}/${it.releaseDate}"
                            )
                        }
                ) {

                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500" + it.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(180.dp)
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = it.movieName,
                            fontSize = 17.sp,
                            maxLines = 2,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                                .weight(1f)
                        )

                        Row(modifier = Modifier.weight(1f)) {
                            IconButton(onClick = {
                                viewModel.updateIsFav(!it.isFav, it.imageUrl)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_fav),
                                    contentDescription = "Add to Favorite",
                                    tint = if (it.isFav) Color.Yellow else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }

                            IconButton(onClick = {
                                viewModel.updateIsLater(!it.isLater, it.imageUrl)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_later),
                                    contentDescription = "Add to Watchlist",
                                    tint = if (it.isLater) Color.Yellow else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
                                CircularProgressIndicator(
                                    progress = 1f,
                                    color = Color.LightGray,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(30.dp)
                                )
                                Text(
                                    text = it.userRating,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                CircularProgressIndicator(
                                    progress = it.userRating.toFloat()/10,
                                    color = if((it.userRating.toFloat()/10)<0.7f) Color.Red else Color.Green,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "I'm leaving \uD83D\uDC4B",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.DarkGray)
                    .clickable {
                        logout(context, viewModel)
                    }
                    .padding(horizontal = 6.dp, vertical = 9.dp)
            )
        }
    }
}