package com.pclink.attendance.system.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StillServiceReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        JobSchHelper.scheduleJob(context);

    }

    
}
