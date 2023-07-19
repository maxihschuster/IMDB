package com.example.imdb5.services

import com.example.imdb5.models.Movie
import com.example.imdb5.models.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieApiInterface {

    // MAS PUNTUADAS
    @GET("/3/movie/top_rated?api_key=841379f04363ea6c7c8d72c2a3316c81&language=es")
     fun getTopRatedMoviesList(): Call<MovieResponse>

    // POPULARES
    @GET("/3/movie/popular?api_key=841379f04363ea6c7c8d72c2a3316c81&language=es")
    fun getPopularMoviesList(): Call<MovieResponse>

    // Recientes
    @GET("/3/movie/now_playing?api_key=841379f04363ea6c7c8d72c2a3316c81&language=es")
     fun getNowMoviesList(): Call<MovieResponse>

    //  PELICULA SELECCIONADA
    @GET("/3/movie/{id}?api_key=841379f04363ea6c7c8d72c2a3316c81&language=es")
     fun getMovieDetails(@Path("id") id : Int): Call<Movie>

    // BUSCADOR
    @GET("/3/search/movie?api_key=841379f04363ea6c7c8d72c2a3316c81&language=es&page=1")
     fun getMovieList(@Query("query") query: String,@Query("page") pageIndex: Int): Call<MovieResponse>

}