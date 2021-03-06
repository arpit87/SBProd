package in.co.hopin.Activities;

import android.app.Application;
import android.content.Context;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import static org.acra.ReportField.*;

@ReportsCrashes(formKey = "dDZQYXlhUldnM192YWhpdUhmTm1MLUE6MQ" ,
customReportContent = {APP_VERSION_NAME,
		APP_VERSION_CODE,  PACKAGE_NAME,PHONE_MODEL,BRAND, ANDROID_VERSION,
		TOTAL_MEM_SIZE, AVAILABLE_MEM_SIZE ,CUSTOM_DATA, STACK_TRACE,
		 DISPLAY,USER_APP_START_DATE , USER_CRASH_DATE,LOGCAT },
logcatArguments = { "-t", "100", "-v", "long", "StrangerBuddy:I", "*:D", "*:S" },
mode = ReportingInteractionMode.TOAST,
forceCloseDialogAfterToast = false, // optional, default false
resToastText = in.co.hopin.R.string.crash_toast_text
) 
public class StrangerBuddy extends Application{
	
	private Context context;
	private Platform platform;	
	private static final String TAG = "in.co.hopin.Activities.StrangerBuddy";
		
	@Override
	public void onCreate()
	{		
		super.onCreate();
		ACRA.init(this);
		Logger.i(TAG,"App start");
		context = getApplicationContext();
		platform=Platform.getInstance();
		platform.initialize(this);
		platform.getInstance().startChatService();
        if (!StringUtils.isEmpty(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID))) {
            platform.getInstance().startGCMService();
        }
        //else userid has not been set yet, service will be started after add user response is received.

		//we check on userid which we wipe out on fb logout. User may login as another user
		//for which we will provide different userid		
		Logger.i(TAG,"Platform initialized");
		
	}

}
