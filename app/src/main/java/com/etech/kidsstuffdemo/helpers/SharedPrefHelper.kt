package com.etech.kidsstuffdemo.helpers

import android.content.Context

class SharedPrefHelper {

    companion object {

        const val LOGIN_PREF_KEY="isLogedIn"
        const val PREF_NAME = "loginpref"
        const val USER_ID_KEY="userId"
        const val AUTH_TOKEN_KEY = "authToken"
        const val USER_NAME_KEY = "userName"
        const val EMAIL_KEY = "email"

        fun add(context:Context, key:String ,value:Int){
            var pref = context.getSharedPreferences(PREF_NAME,0)
            var editor = pref.edit()
            editor.putInt(key,value)
            editor.apply()
        }

        fun getInt(context: Context,key:String,default:Int):Int{
            var pref = context.getSharedPreferences(PREF_NAME,0)
            return pref.getInt(key,default)
        }

        fun add(context:Context, key:String ,value:String){
            var pref = context.getSharedPreferences(PREF_NAME,0)
            var editor = pref.edit()
            editor.putString(key,value)
            editor.apply()
        }
        fun getString(context: Context,key:String,default:String):String{
            var pref = context.getSharedPreferences(PREF_NAME,0)
            return pref.getString(key,default)
        }

    }
}