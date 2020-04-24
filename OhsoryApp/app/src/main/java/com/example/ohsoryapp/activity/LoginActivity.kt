package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.LoginData
import com.example.ohsoryapp.db.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var tok :String
    lateinit var loginData: LoginData

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

    private fun setOnBtnClickListener() {                          //로그인 버튼
        bt_log_in_confirm.setOnClickListener {
            getLoginResponse()

            //여기 밑은 임시 로그인
            val token = "임시완"
//저번 시간에 배웠던 SharedPreference에 토큰을 저장!

            SharedPreferenceController.setAuthorization(this@LoginActivity, token)
            // toast(SharedPreferenceController.getAuthorization(this@LogInActivity))
            startActivity<MainActivity>()
            finish()
        }

        bt_log_in_join.setOnClickListener() {
            startActivity<JoinActivity>()
        }

        bt_log_in_find_pw.setOnClickListener{

        }
    }

    private fun getLoginResponse() {
        if (et_log_in_email.text.toString().isNotEmpty() && et_log_in_pw.text.toString().isNotEmpty()) {
            val input_id = et_log_in_email.text.toString()
            val input_pw = et_log_in_pw.text.toString()
            val jsonObject: JSONObject = JSONObject()
            jsonObject.put("email", input_id)
            jsonObject.put("password", input_pw)

            loginData = LoginData(input_id,input_pw)

            /* 로그인 통신 부분 django면 retropit 써되는가? 이렇게 하는거 맞아?
            val postLogInResponse = networkService.postLoginResponse(loginData)

            postLogInResponse.enqueue(object : Callback<PostLoginResponse> {
                override fun onFailure(call: Call<PostLoginResponse>, t: Throwable) {
                    Log.e("Login fail", t.toString())
                }

                override fun onResponse(call: Call<PostLoginResponse>, response: Response<PostLoginResponse>) {
                    if (response.isSuccessful) {

                        Log.v("success communicate", response.body().toString())
                        when (response.body()!!.status) {
                            200 -> {

                                Log.v("success", response.message().toString())
                                val token = response.body()!!.data.token
//저번 시간에 배웠던 SharedPreference에 토큰을 저장!

                                SharedPreferenceController.setAuthorization(this@LogInActivity, token)
                                // toast(SharedPreferenceController.getAuthorization(this@LogInActivity))
                                startActivity<AAAAMainActivity>()
                                finish()


                            }



                            401 -> {
                                Log.v("401 fail", response.message())
                                Log.v("fail", response.errorBody().toString())
                                toast("인증 실패")
                            }

                            500 -> {

                                Log.v("409 error", response.message())
                                Log.v("server error", response.errorBody().toString())
                                toast("서버 내부 에러")
                            }
                            600 -> {
                                Log.v("600 error", response.message())
                                Log.v("database error", response.errorBody().toString())
                                toast("데이터베이스 에러")
                            }
                            else -> {
                                toast("Error")
                            }
                        }
                    }
                    else{
                        toast("로그인 실패")
                    }
                }
            })

            */

        }
    }
}
