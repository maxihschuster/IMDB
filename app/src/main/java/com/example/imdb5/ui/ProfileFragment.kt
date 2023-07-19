package com.example.imdb5.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imdb5.R
import com.example.imdb5.dao.MovieAdapter
import com.example.imdb5.databinding.FragmentProfileBinding
import com.example.imdb5.models.GlobalUser
import com.example.imdb5.models.Movie
import com.example.imdb5.models.ProviderType
import com.example.imdb5.services.MovieApiInterface
import com.example.imdb5.services.MovieApiService
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var listOfMovies: MutableList<Movie>
    private val dbRef = FirebaseFirestore.getInstance().collection("favs").document(FirebaseAuth.getInstance().currentUser!!.uid)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        listOfMovies = mutableListOf()
        binding.rvMovieListP.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMovieListP.adapter = MovieAdapter(listOfMovies,
            MovieAdapter.OnClickListener { movie -> showMovieDetails(movie.id!!) })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu(requireActivity())
        super.onViewCreated(view, savedInstanceState)
        binding.email.text = Firebase.auth.currentUser?.email

        // mostrar nombre
        when (GlobalUser.getUser(context)?.provider) {
            "GOOGLE" -> {
                binding.userName.text = Firebase.auth.currentUser?.displayName
            }
            "BASIC" -> {
                binding.userName.text = Firebase.auth.currentUser?.email?.split("@")?.get(0)
            }
            "FACEBOOK" -> {
                // TODO ver como traer nombre de FB
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onEmptyFavList()
    }

    private fun onEmptyFavList() {
        dbRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                if ((document.data?.get("movies") as ArrayList<*>).size <= 1) {
                    binding.rvMovieListP.visibility = View.GONE
                    binding.favEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvMovieListP.visibility = View.VISIBLE
                    binding.favEmpty.visibility = View.GONE
                    getFavorited()
                }
            }
        }
    }

    fun getFavorited() {
        listOfMovies.clear()
        binding.rvMovieListP.adapter?.notifyDataSetChanged()
        dbRef.get().addOnSuccessListener{ document ->
            val mList = (document.data?.get("movies") as ArrayList<Int>)
            for (i in 1 until mList.size) {
                getMovieData(mList[i])
            }
        }
    }

    private fun getMovieData(id: Int) {
        val apiService = MovieApiService.getInstance().create(MovieApiInterface::class.java)
        apiService.getMovieDetails(id).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                listOfMovies.add(response.body()!!)
                binding.rvMovieListP.adapter?.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<Movie>, t: Throwable) {
            }
        })
    }

    private fun setMenu(menuHost: MenuHost) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.exitIcon -> {
                        logOutAlert()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showMovieDetails(id: Int) {
        val intent1 = Intent(activity, MovieSelectedDetails::class.java)
        intent1.putExtra("id", id)
        startActivity(intent1)
    }

    private fun logOutAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Log Out")
        builder.setMessage("Estas seguro de que deseas salir?")
        builder.setIcon(R.drawable.film_rolls_pana)
        builder.setPositiveButton("Salir") { dialogInterface, which ->
            if (GlobalUser.getUser(context)?.provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }
            GlobalUser.unlogUser(context)                   //deslogear de app
            FirebaseAuth.getInstance().signOut()            //deslogear de firebase
            showLogin()
        }

        builder.setNeutralButton("Cancel") { dialogInface, which -> }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    private fun showLogin() {
        val authIntent = Intent(context, LoginActivity::class.java)
        startActivity(authIntent)
        requireActivity().finish()
    }

}


