package in.co.hopin.Platform;

import in.co.hopin.Activities.StartStrangerBuddyActivity;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.service.OnAlarmReceiver;
import in.co.hopin.service.UploadEventService;
import in.co.hopin.service.WakefulIntentService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SBGlobalBroadcastReceiver extends BroadcastReceiver {
	
	private static final String TAG = "in.co.hopin.Platform.SBGlobalBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d(TAG, "Got intent");
		String intentAction = intent.getAction();
		if(intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			Logger.d(TAG, "Got boot intent");
			Platform.getInstance().startChatService();
            if (!StringUtils.isEmpty(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID))) {
                Platform.getInstance().startGCMService();
            }
            //else userid has not been set yet, service will be started after add user response is received.
            
            ///uploader alarm service
            Intent newIntent =  new Intent(context, OnAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC, 0, Platform.UPLOAD_FREQUENCY, pendingIntent);

            WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
            context.startService(new Intent(context, UploadEventService.class)); //start UploadEventsService
		}
		
	}

}
