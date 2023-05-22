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

    fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.TOKEN_KEY, "")!!
    }

}