package in.co.hopin.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UploadEventService.class)); //start UploadEventService
    }
}
