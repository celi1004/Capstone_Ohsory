package com.example.ohsoryapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohsoryapp.R;
import com.example.ohsoryapp.myclass.WavRecorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordingService extends Service {
    private static final String LOG_TAG = "RecordingService";

    WavRecorder wavRecorder = new WavRecorder("/sdcard/AudioRecorder/", this);

    private String mFileName = null;

    private long now = 0;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String strId = getString(R.string.noti_channel_id);
            final String strTitle = getString(R.string.app_name);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = notificationManager.getNotificationChannel(strId);
            if (channel == null) {
                channel = new NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, strId).build();
            startForeground(1, notification);
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupRecorder(intent);

        now = System.currentTimeMillis();
        wavRecorder.startRecording();

        return START_STICKY;
    }
    // 서비스가 호출될 때 마다 실행

    @Override
    public void onDestroy() {
        if (wavRecorder != null) {
            wavRecorder.stopRecording();
        }

        new Handler().postDelayed(new Runnable()
        {
            //위에 레코딩 쓰레드들이 끝나도록 기다려주자
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(true);
                }
            }
        }, 10000);

        super.onDestroy();
    }
    // 서비스가 종료될 때 실행

    public void setupRecorder(Intent intent) {
        mFileName = intent.getStringExtra("name");
        wavRecorder.setFilename(mFileName);
    }
}
