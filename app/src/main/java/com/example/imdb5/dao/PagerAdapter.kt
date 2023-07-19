package com.example.imdb5.dao

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.imdb5.ui.ProfileFragment
import com.example.imdb5.ui.HomeFragment
import com.example.imdb5.ui.SearchFragment

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> { HomeFragment() }
            1 -> { SearchFragment() }
            else -> { ProfileFragment() }
            //else -> {throw Resources.NotFoundException("PositionNotfound")}
        }
    }
}