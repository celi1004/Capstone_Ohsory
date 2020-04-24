package com.example.ohsoryapp.db

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceController {
    private val USER_NAME: String? = null
    private val myAuth = "myAuth"
    private val USER_IDX : String = "user_idx"
    private val USER_ID: String = "user_id"
    private val USER_PW: String = "user_pw"
    private val USER_TOKEN : String = "my_token"




    private var pref: SharedPreferences? = null




    fun setAuthorization(context: Context, authorization: String) {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(myAuth, authorization)
        editor.commit()

    }

    fun getAuthorization(context: Context): String {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        return pref.getString(myAuth, "")!!
    }

    /*fun getPref(context: Context) {
        if (pref == null) {
            pref = context.getSharedPreferences(SHARED_PREFS_CONFIGURATION, Context.MODE_PRIVATE)
        }
    }
    */

    /*fun load(context: Context) {
        getPref(context)
    }
    */

    fun setPrefData(key: String, value: Float) {
        val editor = pref!!.edit()

        editor.putFloat(key, value)
        editor.commit()
    }

    fun setPrefData(key: String, value: String) {
        val editor = pref!!.edit()

        editor.putString(key, value)
        editor.commit()
    }

    fun setPrefData(key: String, value: Int) {
        val editor = pref!!.edit()

        editor.putInt(key, value)
        editor.commit()
    }

    fun setPrefData(key: String, value: Long) {
        val editor = pref!!.edit()

        editor.putLong(key, value)
        editor.commit()
    }



    //여기서부터 아이디패스워드
    fun setUserID(ctx: Context, input_id: String) {                        //아이디 설정
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(USER_ID, input_id)
        editor.commit()
    }

    fun setUserPW(ctx: Context, input_pw: String) {                            //비밀번호 설정
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(USER_PW, input_pw)
        editor.commit()
    }

    fun getUserID(ctx: Context): String {
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        return preferences.getString(USER_ID, "")!!
    }

    fun getUserPW(ctx: Context): String {
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        return preferences.getString(USER_PW, "")!!
    }

    fun getUserIDX(ctx: Context) : Int {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_IDX, Context.MODE_PRIVATE)
        return preferences.getInt(USER_IDX, -1)
    }
    fun setUserIDX(ctx: Context, input_idx : Int)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_IDX, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(USER_IDX, input_idx)
        editor.commit()
    }
    fun clearUserSharedPreferences(ctx: Context) {
        val preference: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context. MODE_PRIVATE )
        val editor: SharedPreferences.Editor = preference.edit()
        editor.clear()
        editor.commit()
    }
}