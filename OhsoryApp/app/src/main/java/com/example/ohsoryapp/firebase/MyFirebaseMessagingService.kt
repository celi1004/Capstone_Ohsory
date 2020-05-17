package com.example.ohsoryapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log

import androidx.core.app.NotificationCompat

import com.example.ohsoryapp.R
import com.example.ohsoryapp.activity.MainActivity
import com.example.ohsoryapp.data.FCMData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.put.PutFCMKeyUpdateResponse
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var fcmData : FCMData

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data == null)
            return
        sendNotification(remoteMessage.data["title"], remoteMessage.data["content"])
    }

    private fun sendNotification(title: String?, content: String?) {
        var title = title
        if (title == null)
            title = "기본 제목"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 오레오(8.0) 이상일 경우 채널을 반드시 생성해야 한다.
        val CHANNEL_ID = "채널ID"
        val mManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_NAME = "채널 이름"
            val CHANNEL_DESCRIPTION = "채널 Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // add in API level 26
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)
            mChannel.setSound(defaultSoundUri, null)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setAutoCancel(true)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentText(content)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 아래 설정은 오레오부터 deprecated 되면서 NotificationChannel에서 동일 기능을 하는 메소드를 사용.
            builder.setContentTitle(title)
            builder.setSound(defaultSoundUri)
            builder.setVibrate(longArrayOf(500, 500))
        }

        mManager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("새토큰", token)
        sendRegistrationToServer(token)
        /*
     * 기존의 FirebaseInstanceIdService에서 수행하던 토큰 생성, 갱신 등의 역할은 이제부터
     * FirebaseMessaging에 새롭게 추가된 위 메소드를 사용하면 된다.
     */
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.

        SharedPreferenceController.setUserFCMKey(applicationContext, token!!)

        val muser_pk = SharedPreferenceController.getUserID(applicationContext)

        if(muser_pk == -1){
            //아직 유저정보 기억못해 아무것도 하지말고 나중에 로그인할때 보내주자
        }else{
            //아 로그인은 되어있는데 업데이트 된거구나
            fcmData = FCMData(token)
            val putFCMKeyUpdateResponse = networkService.putFCMKeyUpdateResponse(muser_pk, fcmData)

            putFCMKeyUpdateResponse!!.enqueue(object : Callback<PutFCMKeyUpdateResponse> {
                //통신을 못 했을 때

                override fun onFailure(call: Call<PutFCMKeyUpdateResponse>, t: Throwable) {
                    Log.i("서버에러", "실패")
                }

                override fun onResponse(call: Call<PutFCMKeyUpdateResponse>, response: Response<PutFCMKeyUpdateResponse>) {
                    //통신을 성공적으로 했을 때
                    response.code().toString()
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
}
