package com.example.ohsoryapp.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.SignUpData
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.post.PostSignUpResponse
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    lateinit var networkservice: NetworkService
    lateinit var signUpData: SignUpData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setWatcher()

        btn_sign_up_end.setOnClickListener {
            possible_btn()
        }

        bt_back.setOnClickListener{
            finish()
        }
    }

    fun setWatcher(){
        et_sign_up_password.addTextChangedListener(pwTextWatcher)
        et_sign_up_password_check.addTextChangedListener(repwTextWatcher)
        et_sign_up_email.addTextChangedListener(emailWatcher)
    }

    //비밀번호 형식 체크
    var pwTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(et_sign_up_name.text.isNotEmpty() &&  et_sign_up_email.text.isNotEmpty() && et_sign_up_password.text.isNotEmpty() && et_sign_up_password_check.text.isNotEmpty()){
                btn_sign_up_end.isClickable =  true
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", s)) {
                tv_sign_up_notice.setTextColor(Color.parseColor("#7a36e4"))
                tv_sign_up_notice.text = "특수문자,영문,숫자를 8-20자로 포함해주세요"
            }else{
                tv_sign_up_notice.text = "유효한 비밀번호 입니다."
            }
        }

    }

    //비번 과 재비번 이 같은지 체크
    var repwTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(et_sign_up_name.text.isNotEmpty() &&  et_sign_up_email.text.isNotEmpty() && et_sign_up_password.text.isNotEmpty() && et_sign_up_password_check.text.isNotEmpty()){
                btn_sign_up_end.isClickable =  true
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            tv_sign_up_check.visibility = View.VISIBLE
            if(et_sign_up_password.text.toString().equals(et_sign_up_password_check.text.toString())){
                tv_sign_up_check.text = "비밀번호 확인이 일치합니다."
                if(et_sign_up_name.text.isNotEmpty() &&  et_sign_up_email.text.isNotEmpty() && et_sign_up_password.text.isNotEmpty() && et_sign_up_password_check.text.isNotEmpty()){
                    btn_sign_up_end.isClickable =  true
                }
            }else{
                tv_sign_up_check.text = "비밀번호 확인이 일치하지 않습니다."
            }
        }
    }

    //이메일 형식이 맞는지 체크
    var emailWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(et_sign_up_name.text.isNotEmpty() && et_sign_up_email.text.isNotEmpty() && et_sign_up_password.text.isNotEmpty() && et_sign_up_password_check.text.isNotEmpty()){
                btn_sign_up_end.isClickable =  true
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            tv_activity_sign_up_check_email.visibility = View.VISIBLE
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                tv_activity_sign_up_check_email.text= "유효한 이메일 입니다."
            }else{
                tv_activity_sign_up_check_email.text= "유효하지 않은 이메일 입니다."
            }
        }
    }

    fun possible_btn(){
        var name = et_sign_up_name.text.toString()
        var firstname = et_sign_up_first_name.text.toString()
        var lastname = et_sign_up_last_name.text.toString()
        var password = et_sign_up_password.text.toString()
        var passwordcheck = et_sign_up_password_check.text.toString()
        var email = et_sign_up_email.text.toString()

        if (password.equals(passwordcheck)) {
            if (name.length>0 && firstname.length>0 && lastname.length>0 && email.length > 0 && password.length > 8 && passwordcheck.length > 8) { //서로 같은지
                postUserCreate(name, firstname, lastname, password, email)
            } else {
                toast("정보를 확실히 입력해주세요 ").show()
            }
        }else{
            toast("비밀번호가 일치 하지 않습니다").show()
        }

    }

    fun postUserCreate(username: String, userfirstname: String, userlastname : String, userpw: String, useremail: String) {
        //userData에 값 넣기
        signUpData = SignUpData(username, userpw, userlastname, userfirstname, useremail)
        networkservice = ApplicationController.instance.networkService
        //액티비티에서 NetworkService 초기화 레트로핏 인스턴스를 가져와
        var userCreateResponse = networkservice.postSignUpResponse(signUpData)
        //만들어준 UserData객체를 넘겨주고 통신하고 받은 정보
        userCreateResponse!!.enqueue(object : Callback<PostSignUpResponse> {
            override fun onFailure(call: Call<PostSignUpResponse>, t: Throwable) {
                Log.v("Error SignUpActivity : ", t.message)
            }

            override fun onResponse(call: Call<PostSignUpResponse>, response: Response<PostSignUpResponse>) {

                when (response.code()) {
                    200 -> {
                        Toast.makeText(applicationContext,
                                "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    400 -> {
                        Toast.makeText(applicationContext,
                                "중복된 아이디가 존재합니다. 다른 아이디를 입력해주세요.", Toast.LENGTH_LONG).show()
                    }
                    406 ->{
                        Toast.makeText(applicationContext,
                                "아이디 길이를 6글자 이상으로 해주세요.", Toast.LENGTH_LONG).show()
                        //  toast("중복된 이메일 입니다")
                    }
                    else -> {
                        Log.i("에",response.code().toString())
                        Toast.makeText(applicationContext,
                                "서버 에러.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
