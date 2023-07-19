package com.example.imdb5

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.imdb5.dao.PagerAdapter
import com.example.imdb5.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        supportActionBar?.setCustomView(R.layout.abs_layout)

        binding.viewPager.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,index ->
            tab.text = when(index){
                0 -> {""}
                1 -> {""}
                2 -> {""}
                else -> {throw Resources.NotFoundException("Position Not Found")}
            }
        }.attach()

        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_home_24)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_search_24)
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_baseline_perm_identity_24)

        FirebaseFirestore.getInstance().collection("favs").document(FirebaseAuth.getInstance().currentUser!!.uid).addSnapshotListener { snapshot, e ->
                if (snapshot == null || !snapshot.exists()) {
                    FirebaseFirestore.getInstance().collection("favs").document(FirebaseAuth.getInstance().currentUser!!.uid).set(hashMapOf("movies" to FieldValue.arrayUnion(0)), SetOptions.merge())
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}


