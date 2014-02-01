package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;

public class SaveReferralResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.SaveReferralResponse";


	public SaveReferralResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();		
		try {
			//body = jobj.getJSONObject("body");
			String body = jobj.getString("body");			
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ProgressHandler.dismissDialoge();
			//ToastTracker.showToast("Some error occured in saving referral request");
			e.printStackTrace();
		}
	}
}
