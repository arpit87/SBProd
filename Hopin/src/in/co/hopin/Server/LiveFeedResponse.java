package in.co.hopin.Server;

import in.co.hopin.HelperClasses.CurrentFeed;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Util.Logger;

import org.apache.http.HttpResponse;
import org.json.JSONException;

public class LiveFeedResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.LiveFeedResponse";
	

	public LiveFeedResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
		
	}

	@Override
	public void process() {		
		Logger.i(TAG,"processing LiveFeedResponse");
		Logger.i(TAG,"server response:"+jobj.toString());
		try {
			body = jobj.getJSONObject("body");			
			CurrentFeed.getInstance().updateLiveFeedFromJSON(body);
			SBHttpResponseListener listener = getResponseListener();
			if(listener!=null)
				listener.onComplete("");	
			else
				Logger.i(TAG, "feed listener null");
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ToastTracker.showToast("Some error occured in live feed response");			
		}
	}
}
