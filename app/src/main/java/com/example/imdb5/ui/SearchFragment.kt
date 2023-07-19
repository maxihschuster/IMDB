package com.example.imdb5.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb5.PaginationScrollListener
import com.example.imdb5.dao.MovieAdapter
import com.example.imdb5.databinding.FragmentSearchBinding
import com.example.imdb5.models.Movie
import com.example.imdb5.models.MovieResponse
import com.example.imdb5.services.MovieApiInterface
import com.example.imdb5.services.MovieApiService
import retrofit2.*

class SearchFragment : Fragment() {
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false
    var isLastPage = false
    var TOTAL_PAGES = 20
    private lateinit var listOfMovies: MutableList<Movie>
    lateinit var stringSearch: String
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var apiInterface: MovieApiInterface
    private lateinit var binding : FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        listOfMovies = mutableListOf()
        binding.rvMovieListS.adapter = MovieAdapter(listOfMovies,
            MovieAdapter.OnClickListener{movie -> showMovieDetails(movie.id!!) })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        apiInterface = MovieApiService.getInstance().create(MovieApiInterface::class.java)
        binding.rvMovieListS.layoutManager =linearLayoutManager
        binding.rvMovieListS.setHasFixedSize(true)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.imgBuscaTuPeli.visibility = View.GONE
                binding.rvMovieListS.visibility = View.GONE
                binding.progressBarSearch.visibility = View.VISIBLE
                currentPage = PAGE_START
                getListOfMovies(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        setScrollListener()
    }

    private fun getListOfMovies(query: String) {
        listOfMovies.clear()
        binding.rvMovieListS.adapter?.notifyDataSetChanged()
        getMovieData(query,PAGE_START)
    }

    fun loadNextPage(query: String) {
        val call = apiInterface.getMovieList(query, currentPage)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    isLoading = false
                    isLastPage = false
                    currentPage++
                    getMovieData(query,currentPage)
                } else Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun setScrollListener() {
        binding.rvMovieListS.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun getTotalPageCount(): Int { return TOTAL_PAGES }
            override fun isLastPage(): Boolean { return isLastPage }
            override fun isLoading(): Boolean { return isLoading }
            override fun loadMoreItems() {
                isLoading = true
                loadNextPage(stringSearch)
            }
        })
    }

    fun getMovieData(query:String,page : Int) {
        stringSearch=query
        MovieApiService.getInstance().create(MovieApiInterface::class.java)
            .getMovieList(query, page).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                binding.rvMovieListS.visibility = View.VISIBLE
                binding.progressBarSearch.visibility = View.GONE
                listOfMovies.addAll(response.body()!!.movies!!)
                binding.rvMovieListS.adapter?.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(context, "Error : $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showMovieDetails(id: Int) {
        val intent1 = Intent(activity, MovieSelectedDetails::class.java)
        intent1.putExtra("id", id)
        startActivity(intent1)
    }

}

