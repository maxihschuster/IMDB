package com.example.imdb5.dao


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imdb5.R
import com.example.imdb5.databinding.MovieItemBinding
import com.example.imdb5.models.Movie

const val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"

class MovieAdapter ( private val movies : List<Movie>, private val onClickListener: OnClickListener) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    class MovieViewHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindMovie(movie : Movie) = binding.apply{
            movieTitle.text = movie.title
            movieReleaseDate.text = movie.release
            movieRate.text = movie.voteAverage
            Glide.with(binding.root)
                .load(IMAGE_BASE + movie.poster)
                .error(R.drawable.img_not_found)
                .into(moviePoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.itemView.setOnClickListener { onClickListener.onClick(movies[position]) }
        holder.bindMovie(movies[position])
    }

    class OnClickListener(val clickListener: (movie: Movie) -> Unit) {
        fun onClick(movie: Movie) = clickListener(movie)
    }

    override fun getItemCount(): Int = movies.size

}




