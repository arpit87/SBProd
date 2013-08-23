package in.co.hopin.Server;

import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.util.Log;

public class FeedbackResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.FeedbackResponse";
	
	public FeedbackResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing FeedbackResponse");
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"server response:"+jobj.toString());
		try {			
			String body = jobj.getString("body");
			ToastTracker.showToast("Feedback saved successfully");		
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Some error occured in saving feedback");
			e.printStackTrace();
		}
	}
}
