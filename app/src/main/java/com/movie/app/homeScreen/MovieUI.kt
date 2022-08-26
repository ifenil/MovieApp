package com.movie.app.homeScreen

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.movie.app.Movie
import com.movie.app.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Composable
fun MovieUI() {
    val context = LocalContext.current
    val movieViewModel: MovieViewModel = viewModel()

    LaunchedEffect(key1 = Unit, block = {
        getRequest(context, movieViewModel.responseTxt)
    })

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
            movieViewModel.responseTxt.value?.forEach {
                item {
                    val addedFav = remember { mutableStateOf(false) }
                    val addedLater = remember { mutableStateOf(false) }

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
                                text = it.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 6.dp)
                            )

                            Text(
                                text = "Rating :- "+ it.userRating,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row {
                                IconButton(onClick = { addedFav.value = !addedFav.value }) {
                                    Icon(
                                        painter = painterResource(id =  R.drawable.ic_fav),
                                        contentDescription = "Add to Favorite",
                                        tint = if(addedFav.value) Color.Yellow else Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                }

                                IconButton(onClick = { addedLater.value = !addedLater.value }) {
                                    Icon(
                                        painter = painterResource(id =  R.drawable.ic_later),
                                        contentDescription = "Add to Watchlist",
                                        tint = if(addedLater.value) Color.Yellow else Color.White,
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

fun getRequest(context: Context, responseTxt: MutableLiveData<List<Movie>>) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://api.themoviedb.org/3/movie/popular?api_key=4d0b112b33ae21e2647cd2002ebcfacf&language=en-US&page=1"

    val jsonObjectRequest =
        JsonObjectRequest(Request.Method.GET, url, null, {
            try {
                val jsonArray: JSONArray = it.getJSONArray("results")
                val listOfMovie = mutableListOf<Movie>()
                for (i in 0..jsonArray.length()){
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val movieData = Movie(
                        name = jsonObject.getString("original_title"),
                        imageUrl = jsonObject.getString("backdrop_path"),
                        category = jsonObject.getString("genre_ids"),
                        movieBanner = jsonObject.getString("poster_path"),
                        description = jsonObject.getString("overview"),
                        releaseDate = jsonObject.getString("release_date"),
                        userRating = jsonObject.getString("vote_average"))

                    listOfMovie.add(movieData)
                    responseTxt.postValue(listOfMovie)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) {
            Toast.makeText(context, "Fail to get data..", Toast.LENGTH_SHORT).show()
        }
    queue.add(jsonObjectRequest)
}