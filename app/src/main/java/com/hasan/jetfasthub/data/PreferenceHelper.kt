package com.hasan.jetfasthub.data

import android.content.Context
import com.hasan.jetfasthub.utility.Constants

object PreferenceHelper {

    fun saveToken(context: Context, token:String){
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(Constants.TOKEN_KEY, token)
            apply()
        }
    }

    fun saveAuthenticatedUser(context: Context, username: String){
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(Constants.USERNAME_KEY, username)
            apply()
        }
    }

    fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.TOKEN_KEY, "")!!
    }

    fun getAuthenticatedUsername(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.USERNAME_KEY, "")!!
    }
}