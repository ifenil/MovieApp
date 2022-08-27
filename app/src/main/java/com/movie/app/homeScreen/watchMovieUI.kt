package com.movie.app.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import com.movie.app.createNotification

@Composable
fun WatchMovieUI(
    movieName: String,
    movieBanner: String,
    movieRating: String,
    movieDes: String,
    movieReleaseDate: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 38.dp, bottom = 57.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = movieName,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color.LightGray)
        ){
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500/$movieBanner"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(onClick = { createNotification(context) }) {
                Icon(
                    painter = painterResource(id = com.movie.app.R.drawable.ic_play),
                    contentDescription = "Play Now",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(80.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                CircularProgressIndicator(
                    progress = 1f,
                    color = Color.Gray,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(35.dp)
                )
                Text(
                    text = movieRating,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircularProgressIndicator(
                    progress = movieRating.toFloat() / 10,
                    color = if((movieRating.toFloat()/10)<0.7f) Color.Red else Color.Green,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Text(
            text = movieDes,
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 5.dp)
        )

        Text(
            text = "Release Date: $movieReleaseDate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}