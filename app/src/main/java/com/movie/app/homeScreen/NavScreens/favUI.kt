package com.movie.app.homeScreen

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.rememberAsyncImagePainter
import com.movie.app.R
import com.movie.app.room.RoomViewModel
import com.movie.app.room.RoomViewModelFactory

@Composable
fun FavUI() {

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

        Column(
            modifier = Modifier
                .padding(top = 28.dp, bottom = 57.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                allMovies.forEach {
                    if (it.isFav) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.DarkGray)
                            ) {

                                Image(
                                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500" + it.imageUrl),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(180.dp, 150.dp)
                                        .clickable { }
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = it.movieName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(
                                            horizontal = 5.dp,
                                            vertical = 6.dp
                                        )
                                    )

                                    Text(
                                        text = "Rating :- " + it.userRating,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Light,
                                        color = Color.White,
                                        modifier = Modifier.padding(
                                            horizontal = 5.dp,
                                            vertical = 5.dp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row {
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}