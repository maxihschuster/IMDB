package com.example.imdb5.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.imdb5.R
import com.example.imdb5.dao.IMAGE_BASE
import com.example.imdb5.databinding.ActivityMovieSelectedDetailsBinding
import com.example.imdb5.models.Movie
import com.example.imdb5.services.MovieApiInterface
import com.example.imdb5.services.MovieApiService
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.properties.Delegates

class MovieSelectedDetails : AppCompatActivity() {
    lateinit var binding : ActivityMovieSelectedDetailsBinding
    lateinit var itemAux : MenuItem
    lateinit var list : MutableList<Movie>
    var flagFavorite :Boolean = false
    var movieID by Delegates.notNull<Int>()
    val userID = FirebaseAuth.getInstance().currentUser!!.uid
    val dbRef = FirebaseFirestore.getInstance().collection("favs").document(userID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSelectedDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title=""

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //adds a back button

        list = mutableListOf()
        binding.progressBarSelectedMovie.visibility = ProgressBar.VISIBLE
        binding.movieTotal.visibility = View.GONE
        movieID = intent.getIntExtra("id",0)

        getFavorited()
        setupData()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_details_menu, menu)
        itemAux = menu.findItem(R.id.favoriteIcon)
        val favoriteItem = menu.findItem(R.id.favoriteIcon)
        if (flagFavorite) {
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_24)
        } else {
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_border_24)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favoriteIcon -> {
                onFavoriteClicked()
                invalidateOptionsMenu()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }


    private fun onFavoriteClicked() {
        if (flagFavorite){
            dbRef.update("movies", FieldValue.arrayRemove(movieID)).addOnSuccessListener(){
                flagFavorite = false
                itemAux.setIcon(R.drawable.ic_baseline_favorite_border_24)
                Toast.makeText(this,"Se quitó de favoritos",Toast.LENGTH_SHORT).show()
            }
        }else{
            dbRef.set(hashMapOf("movies" to FieldValue.arrayUnion(movieID)), SetOptions.merge()).addOnSuccessListener(){
                flagFavorite = true
                itemAux.setIcon(R.drawable.ic_baseline_favorite_24)
                Toast.makeText(this,"Se añadió a favoritos",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupData(){
        getMovieDataDetails(movieID) {
            binding.movieDetailsTitle.text = it.title
            supportActionBar?.title = it.title
            binding.movieDetailsRate.text = it.voteAverage
            binding.movieDetailsOverview.text = it.overview
            binding.movieDetailsRelease.text = it.release
            binding.movieDetailsLang.text = it.originalLanguage
            binding.movieDetailsVotes.text = it.voteCount
            Glide.with(this).load(IMAGE_BASE + it.poster).error(R.drawable.img_not_found).into(binding.movieDetailsPoster)
            Glide.with(this).load(IMAGE_BASE + it.backdrop).error(R.drawable.img_not_found).into(binding.movieDetailsBackdrop)
        }
    }

    private fun getFavorited() {
        dbRef.addSnapshotListener { snapshot, e ->
            if (e != null) { return@addSnapshotListener }
            if (snapshot != null && snapshot.exists()) {
                for (i in 0 until  (snapshot.data?.get("movies") as ArrayList<*>).size){
                    if( (snapshot.data?.get("movies") as ArrayList<*>)[i] == movieID.toLong() ){
                        flagFavorite =true
                    }
                }
            }
        }
    }

    private fun getMovieDataDetails(id:Int, callback: (Movie) -> Unit) {

        val apiService = MovieApiService.getInstance().create(MovieApiInterface::class.java)
        apiService.getMovieDetails(id).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                binding.movieTotal.visibility=View.VISIBLE
                list.add(response.body()!!)
                return callback(response.body()!!)
            }
            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Toast.makeText(applicationContext,"Ha ocurrido un error: ${t.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }


}

