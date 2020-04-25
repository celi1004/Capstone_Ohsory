package com.example.ohsoryapp.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ohsoryapp.listener.PhoneListener;
import com.example.ohsoryapp.myclass.AppPreferences;
import com.example.ohsoryapp.myclass.DBHelper;
import com.example.ohsoryapp.service.RecordCallService;
import com.example.ohsoryapp.service.RecordingService;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyCallReceiver extends BroadcastReceiver {

    public MyCallReceiver() {
    }

    static TelephonyManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("JLCreativeCallRecorder", "MyCallReceiver.onReceive ");

        if (!AppPreferences.getInstance(context).isRecordingEnabled()) {
            removeListener();
            return;
        }

        // Start Listening to the call....
        if (null == manager) {
            manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (null != manager)
            manager.listen(PhoneListener.getInstance(context), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void removeListener() {
        if (null != manager) {
            if (PhoneListener.hasInstance())
                manager.listen(PhoneListener.getInstance(null), PhoneStateListener.LISTEN_NONE);
        }
    }
}
