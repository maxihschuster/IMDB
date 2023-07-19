package com.example.imdb5.models

import android.content.Context
import com.example.imdb5.R

object GlobalUser {

    fun setUser(context: Context, user: User) {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", user.email)
        prefs.putString("provider", user.provider)
        prefs.apply()
    }

    fun getUser(context: Context?): User? {
        val prefs = context?.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs?.getString("email", "")
        val provider = prefs?.getString("provider", "")  // cambiar por null y sacar todos los && != ""
        if (email != null && provider != null && email != "" && provider != "") {
            return User(email, provider)
        } else return null
    }

    fun unlogUser(context: Context?){
        val prefs = context?.getSharedPreferences(context.getString(R.string.prefs_file),Context.MODE_PRIVATE)?.edit()
        prefs?.clear()
        prefs?.apply()
    }
}
