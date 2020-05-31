package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.FCMData
import com.example.ohsoryapp.data.LoginData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.post.PostSignUpResponse
import com.example.ohsoryapp.put.PutFCMKeyUpdateResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var tok :String
    lateinit var loginData: LoginData
    lateinit var fcmData : FCMData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setOnBtnClickListener()

        tok = SharedPreferenceController.getAuthorization(this)

        if (tok.isNotEmpty()) {
            //저장된 아이디가 있으면 자동로그인 메인으로 바로 이동
            startActivity<MainActivity>("token" to "$tok")
            finish()
        }
    }

    private fun setOnBtnClickListener() {
        bt_log_in_confirm.setOnClickListener {
            getLoginResponse()
        }

        bt_log_in_join.setOnClickListener {
            startActivity<SignUpActivity>()
        }

        bt_log_in_find_pw.setOnClickListener{

        }
    }

    private fun getLoginResponse() {
        if (et_log_in_email.text.toString().isNotEmpty() && et_log_in_pw.text.toString().isNotEmpty()) {
            val input_id = et_log_in_email.text.toString()
            val input_pw = et_log_in_pw.text.toString()

            loginData = LoginData(input_id,input_pw)

            val postLogInResponse = networkService.postLoginResponse(loginData)

            postLogInResponse!!.enqueue(object : Callback<PostSignUpResponse> {
                //통신을 못 했을 때
                override fun onFailure(call: Call<PostSignUpResponse>, t: Throwable) {
                    Log.e("Login fail", t.toString())
                }

                override fun onResponse(call: Call<PostSignUpResponse>, response: Response<PostSignUpResponse>) {
                    //통신을 성공적으로 했을 때
                    if (response.isSuccessful) {
                        val id = response.body()!!.user.id
                        val name = response.body()!!.user.username
                        val token = response.body()!!.token
                        val fcm = SharedPreferenceController.getUserFCMKey(this@LoginActivity)

                        //저번 시간에 배웠던 SharedPreference에 토큰을 저장!
                        SharedPreferenceController.setAuthorization(this@LoginActivity, token)
                        SharedPreferenceController.setUserID(this@LoginActivity, id)
                        SharedPreferenceController.setUserName(this@LoginActivity, name)
                        // toast(SharedPreferenceController.getAuthorization(this@LogInActivity))

                        setFCMKey(id, fcm)

                        startActivity<MainActivity>()
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity,
                                "아이디 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            })

        }else{
            Toast.makeText(this,
                    "빈칸이 있습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setFCMKey(id : Int, fcm : String){
        fcmData = FCMData(fcm)
        val putFCMKeyUpdateResponse = networkService.putFCMKeyUpdateResponse(id, fcmData)

        putFCMKeyUpdateResponse!!.enqueue(object : Callback<PutFCMKeyUpdateResponse> {
            //통신을 못 했을 때

            override fun onFailure(call: Call<PutFCMKeyUpdateResponse>, t: Throwable) {
                Log.i("서버에러", "실패")
            }

            override fun onResponse(call: Call<PutFCMKeyUpdateResponse>, response: Response<PutFCMKeyUpdateResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    Log.i("네트워크", "업데이트성공")
                }
                else{
                    Log.i("서버에러", "서버에러")
                }
            }
        })
    }
}
