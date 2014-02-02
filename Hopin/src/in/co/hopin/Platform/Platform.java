package in.co.hopin.Platform;

import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.Logger;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

public class Platform {
	
	private final static String TAG = "in.co.hopin.Platform.Platform";
	private static Platform instance = null;
	private Context context;	
	private Handler handler;
	private boolean ENABLE_LOGGING = false;
	public boolean SUPPORTS_NEWAPI = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
		
	private Platform() {
	}
	
	public static Platform getInstance()
	{
		if(instance == null)
			instance = new Platform();
		return instance;
	}
	
	public boolean isLoggingEnabled() {
		return ENABLE_LOGGING;
	}	

	public Context getContext(){
		return context;
	}	
	
	public Handler getHandler(){
		return handler;
	}
	
	public void initialize(Context context) {
		this.context= context;			
		SBHttpClient.getInstance();
		handler = new Handler();		
		CurrentNearbyUsers.getInstance().clearAllData();
		ThisUserNew.getInstance();
		EasyTracker tracker = EasyTracker.getInstance(context);
		GoogleAnalytics.getInstance(context).setDefaultTracker(tracker);
		ENABLE_LOGGING = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
	}
	
	public int getThisAppVersion()
	{
	 try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
	}	
	
	public String getThisAppVersionName()
	{
	 try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
	}	
	
	 public void startChatService(){
	     
         Intent i = new Intent("in.co.hopin.ChatService.SBChatService");                  
         if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "Service starting" );
         context.startService(i);
        
        }
             
	
	 public void stopChatService() {		
	          Intent i = new Intent("in.co.hopin.ChatService.SBChatService");
	          context.stopService(i);       
	          
	          if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "Service stopped" );	         
	             
     }
    
    public void startGCMService() {
        Intent intent = new Intent("in.co.hopin.gcm.GCMService");
        Logger.d(TAG, "Starting GCM service");
        context.startService(intent);
     }
    
   /* public void startUploadEventService() {
        Intent i = new Intent("in.co.hopin.service.UploadEventService");
        Logger.d(TAG, "Starting UploadEvent service");
        context.startService(i);
    }
   
      
    public void stopUploadEventService() {
        Intent i = new Intent("in.co.hopin.service.UploadEventService");
        context.stopService(i);
        Logger.d(TAG, "UploadEvent service stopped");
    }*/
}
