package com.android.priyanka.securityapp.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.priyanka.securityapp.BroadcastReceiver.CallHelper;

public class CallDetectService extends Service {

    private CallHelper callHelper;

    public CallDetectService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        callHelper = new CallHelper(this);

        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        callHelper.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
