package in.co.hopin.Platform;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SaveReferrerRequest;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.analytics.tracking.android.CampaignTrackingReceiver;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

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
		}
		else if(intentAction.equals("com.android.vending.INSTALL_REFERRER"))
		{
			Bundle b = intent.getExtras();			
			if (b != null) {
				Logger.d(TAG, "Got referral intent uri:"+b.getString("referrer"));
				Logger.d(TAG, "Got referral intent uri:"+b.toString());
				String referrerStr = b.getString("referrer");
				ThisAppConfig.getInstance().putString(ThisAppConfig.REFERRER_STRING,referrerStr);			
	        	MapBuilder.createAppView().setAll(getReferrerMapFromUri(referrerStr));           
	            ToastTracker.showToast("Welcome to Hopin!");	           
	            SaveReferrerRequest saveReferrerRequest = new SaveReferrerRequest(null);
	            SBHttpClient.getInstance().executeRequest(saveReferrerRequest);
	          }
			else
				Logger.d(TAG, "uri null in referal");
			
			
			// When you're done, pass the intent to the Google Analytics receiver.
		    new CampaignTrackingReceiver().onReceive(context, intent);
	
		}
		
	}
	
	Map<String,String> getReferrerMapFromUri(String referrerStr) {

	    MapBuilder paramMap = new MapBuilder();

	    // If no URI, return an empty Map.
	    if (referrerStr == null) { return paramMap.build(); }
	    
	    if (referrerStr.contains("utm_source")) {
	      paramMap.setCampaignParamsFromUrl("https://play.google.com/store/apps/details?id=in.co.hopin&referrer=" + referrerStr);
	     } else {
	       paramMap.set(Fields.CAMPAIGN_SOURCE, "unknown");
	     }

	     return paramMap.build();
	  }
	
}
