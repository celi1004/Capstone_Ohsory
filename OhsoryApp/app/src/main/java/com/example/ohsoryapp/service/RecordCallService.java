package com.example.ohsoryapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.ohsoryapp.R;
import com.example.ohsoryapp.activity.MainActivity;
import com.example.ohsoryapp.myclass.AppPreferences;
import com.example.ohsoryapp.myclass.LocalBroadcastActions;

import java.io.File;

//전화중일때 서비스로 돌아갈 일 정의
public class RecordCallService extends Service {

    public RecordCallService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startRecording();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    boolean isRecording = false;

    private void stopRecording() {
        Log.i("시발", "그만레코딩");
        if (isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;

                displayNotification();

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LocalBroadcastActions.NEW_RECORDING_BROADCAST));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    MediaRecorder mediaRecorder;

    private void startRecording() {
        Log.i("시발", "시작레코딩");
        if (!isRecording) {
            isRecording = true;
            File file = null;
            try {
                File dir = AppPreferences.getInstance(getApplicationContext()).getFilesDirectory();
                mediaRecorder = new MediaRecorder();
                file = File.createTempFile("record", ".3gp", dir);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncodingBitRate(12200);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
                isRecording = false;
                if (file != null) file.delete();
                isRecording = false;
            }
        }
    }

    public void displayNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_recording_conversation_white_24);
        builder.setContentTitle(getApplicationContext().getString(R.string.notification_title));
        builder.setContentText(getApplicationContext().getString(R.string.notification_text));
        builder.setContentInfo(getApplicationContext().getString(R.string.notification_more_text));
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis())); // fake action to force PendingIntent.FLAG_UPDATE_CURRENT
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        builder.setContentIntent(PendingIntent.getActivity(this, 0xFeed, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(0xfeed, builder.build());
    }


    public static void startRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        context.startService(intent);
    }


    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        context.stopService(intent);
    }
}
