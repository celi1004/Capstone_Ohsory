package com.example.ohsoryapp.db

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceController {
    private val USER_NAME: String? = null
    private val myAuth = "myAuth"
    private val USER_ID: String = "user_id"
    private val USER_PW: String = "user_pw"
    private val USER_MODEL_EXIST: String = "user_model_exist"
    private val USER_PROGRESS: String = "user_progress"
    private val USER_AUDIO_TIME: String = "user_audio_time"
    private val USER_FCM_KEY : String = "user_fcm_key"
    private val ACTIVATE_CALL_RECORDING : String = "activate_call_recording"
    private val ACTIVATE_ALARM : String = "activate_alarm"

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
    fun setUserName(ctx: Context, input_name: String) {                            //비밀번호 설정
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(USER_NAME, input_name)
        editor.commit()
    }

    fun getUserName(ctx: Context): String {
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE)
        return preferences.getString(USER_NAME, "")!!
    }

    fun setUserFCMKey(ctx: Context, input_k: String) {                            //비밀번호 설정
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_FCM_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(USER_FCM_KEY, input_k)
        editor.commit()
    }

    fun getUserFCMKey(ctx: Context): String {
        val preferences: SharedPreferences = ctx.getSharedPreferences(USER_FCM_KEY, Context.MODE_PRIVATE)
        return preferences.getString(USER_FCM_KEY, "")!!
    }

    fun getUserID(ctx: Context) : Int {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_ID, Context.MODE_PRIVATE)
        return preferences.getInt(USER_ID, -1)
    }
    fun setUserID(ctx: Context, input_idx : Int)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_ID, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(USER_ID, input_idx)
        editor.commit()
    }

    fun getUserME(ctx: Context) : Int {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_MODEL_EXIST, Context.MODE_PRIVATE)
        return preferences.getInt(USER_MODEL_EXIST, 0)
    }
    fun setUserME(ctx: Context, input_idx : Int)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_MODEL_EXIST, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(USER_MODEL_EXIST, input_idx)
        editor.commit()
    }

    fun getUserPG(ctx: Context) : Int {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_PROGRESS, Context.MODE_PRIVATE)
        return preferences.getInt(USER_PROGRESS, 0)
    }
    fun setUserPG(ctx: Context, input_idx : Int)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_PROGRESS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(USER_PROGRESS, input_idx)
        editor.commit()
    }

    fun getUserAT(ctx: Context) : Float {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_AUDIO_TIME, Context.MODE_PRIVATE)
        return preferences.getFloat(USER_AUDIO_TIME, 0.0F)
    }
    fun setUserAT(ctx: Context, input_idx : Float)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(USER_AUDIO_TIME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putFloat(USER_AUDIO_TIME, input_idx)
        editor.commit()
    }


    fun clearUserSharedPreferences(ctx: Context) {
        val preference: SharedPreferences = ctx.getSharedPreferences(USER_NAME, Context. MODE_PRIVATE )
        val editor: SharedPreferences.Editor = preference.edit()
        editor.clear()
        editor.commit()
    }

    fun getActivateCallRecording(ctx: Context) : Boolean {
        val preferences : SharedPreferences = ctx.getSharedPreferences(ACTIVATE_CALL_RECORDING, Context.MODE_PRIVATE)
        return preferences.getBoolean(ACTIVATE_CALL_RECORDING, true)
    }
    fun setActivateCallRecording(ctx: Context, isflag : Boolean)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(ACTIVATE_CALL_RECORDING, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(ACTIVATE_CALL_RECORDING, isflag)
        editor.commit()
    }

    fun getActivateAlarm(ctx: Context) : Boolean {
        val preferences : SharedPreferences = ctx.getSharedPreferences(ACTIVATE_ALARM, Context.MODE_PRIVATE)
        return preferences.getBoolean(ACTIVATE_ALARM, true)
    }
    fun setActivateAlarm(ctx: Context, isflag : Boolean)  {
        val preferences : SharedPreferences = ctx.getSharedPreferences(ACTIVATE_ALARM, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(ACTIVATE_ALARM, isflag)
        editor.commit()
    }
}