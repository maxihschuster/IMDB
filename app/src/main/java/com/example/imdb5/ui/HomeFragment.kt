package com.example.imdb5.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb5.R
import com.example.imdb5.dao.MovieAdapter
import com.example.imdb5.databinding.FragmentHomeBinding
import com.example.imdb5.models.Movie
import com.example.imdb5.models.MovieResponse
import com.example.imdb5.services.MovieApiInterface
import com.example.imdb5.services.MovieApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMovieListH.layoutManager = linearLayoutManager
        setVisivilitySearching()
        getPopularMoviesData { movies: List<Movie> ->
            binding.rvMovieListH.adapter = MovieAdapter(movies,MovieAdapter.OnClickListener{ movie-> showMovieDetails(movie.id!!) })
        }
        setMenu(requireActivity())
    }

    fun showMovieDetails(id : Int){
        val intent1 = Intent(activity, MovieSelectedDetails::class.java)
        intent1.putExtra("id",id)
        startActivity(intent1)
    }

    private fun setVisivilitySearching(){
        binding.progressBarHome.visibility = View.VISIBLE
        binding.rvMovieListH.visibility = View.GONE
    }
    private fun setVisibilityLoaded(){
        binding.progressBarHome.visibility = View.GONE
        binding.rvMovieListH.visibility = View.VISIBLE
    }

    private fun setMenu(menuHost:MenuHost){
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_action_popular_movies -> {
                        binding.tvTitleList.text = "Peliculas populares"
                        setVisivilitySearching()
                        getPopularMoviesData { movies: List<Movie> ->
                            binding.rvMovieListH.adapter = MovieAdapter(movies, MovieAdapter.OnClickListener { movie -> showMovieDetails(movie.id!!) })
                        }
                        true
                    }
                    R.id.menu_action_top_rated -> {
                        binding.tvTitleList.text = "Peliculas mas valoradas"
                        setVisivilitySearching()
                        getRatedMoviesData { movies: List<Movie> ->
                            binding.rvMovieListH.adapter = MovieAdapter(movies, MovieAdapter.OnClickListener { movie -> showMovieDetails(movie.id!!) })
                        }
                        true
                    }
                    R.id.menu_action_now_playing -> {
                        binding.tvTitleList.text = "Peliculas mas recientes"
                        setVisivilitySearching()
                        getNowMoviesData { movies: List<Movie> ->
                            binding.rvMovieListH.adapter = MovieAdapter(movies, MovieAdapter.OnClickListener { movie -> showMovieDetails(movie.id!!) })
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getPopularMoviesData(callback: (List<Movie>) -> Unit) {
        MovieApiService.getInstance().create(MovieApiInterface::class.java)
            .getPopularMoviesList().enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    setVisibilityLoaded()
                    return callback(response.body()!!.movies!!)
                }
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Ha ocurrido un error: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getRatedMoviesData(callback: (List<Movie>) -> Unit) {
        MovieApiService.getInstance().create(MovieApiInterface::class.java)
            .getTopRatedMoviesList().enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    setVisibilityLoaded()
                    return callback(response.body()!!.movies!!)
                }
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Ha ocurrido un error: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getNowMoviesData(callback: (List<Movie>) -> Unit) {
        MovieApiService.getInstance().create(MovieApiInterface::class.java)
            .getNowMoviesList().enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    setVisibilityLoaded()
                    return callback(response.body()!!.movies!!)
                }
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Ha ocurrido un error: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}





