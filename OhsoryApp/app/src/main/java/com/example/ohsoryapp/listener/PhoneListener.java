package com.example.ohsoryapp.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.ohsoryapp.myclass.AppPreferences;
import com.example.ohsoryapp.myclass.DBHelper;
import com.example.ohsoryapp.service.RecordingService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

//브로드캐스트로 상태왔을 때 정화히 어떤 전화상태인지 알기위한 클래스 우리가 만들었음
public class PhoneListener extends PhoneStateListener {
    private static PhoneListener instance = null;

    static private Intent mIntent = null;
    static private DBHelper mDatabase;

    private String mFileName = null;
    private long mElapsedMillis = 0;

    /**
     * Must be called once on app startup
     *
     * @param context - application context
     * @return
     */
    //없으면 만들고 있으면 있는거 줘 싱글톤
    public static PhoneListener getInstance(Context context) {
        if (instance == null) {
            instance = new PhoneListener(context);
        }
        mDatabase = new DBHelper(context);

        return instance;
    }

    public static boolean hasInstance() {
        return null != instance;
    }

    private final Context context;

    private PhoneListener(Context context) {
        this.context = context;
    }

    AtomicBoolean isRecording = new AtomicBoolean();
    AtomicBoolean isWhitelisted = new AtomicBoolean();


    /**
     * Set the outgoing phone number
     * <p/>
     * Called by {@link //MyCallReceiver}  since that is where the phone number is available in a outgoing call
     *
     * @param
     */

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        super.onCallStateChanged(state, incomingNumber);
        mIntent = new Intent(context, RecordingService.class);

        if (AppPreferences.getInstance(context).isRecordingEnabled()) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // Idle... no call
                    if (isRecording.get()) {

                        context.stopService(mIntent);
                        isRecording.set(false);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // Call answered
                    if (isWhitelisted.get()) {
                        isWhitelisted.set(false);
                        return;
                    }
                    if (!isRecording.get()) {
                        isRecording.set(true);
                        // start: Probably not ever usefull
                        // end: Probably not ever usefull끝

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMdd_hhmmss");
                        String getTime = sdf.format(date);

                        mFileName = getTime + ".wav";

                        mIntent.putExtra("name", mFileName);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(mIntent);
                        } else {
                            context.startService(mIntent);
                        }
                    }
                    break;
            }
        }else{

        }
    }
}
