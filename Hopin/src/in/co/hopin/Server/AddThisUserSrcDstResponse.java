package in.co.hopin.Server;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.InstaRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class AddThisUserSrcDstResponse extends ServerResponseBase{

    private static final String TAG = "in.co.hopin.Server.AddUserResponse";
    private static long TIME_INTERVAL = 1 * 60 * 1000; //1 minute

	String user_id;
		
	public AddThisUserSrcDstResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);

				
	}
	
	@Override
	public void process() {
		//this process is not called if u make syncd consecutive requests,that time only last process called
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			//ToastTracker.showToast("added this user src,dst");
            body.put(UserAttributes.DAILYINSTATYPE, 1);
            ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_INSTA, body.toString());

			MapListActivityHandler.getInstance().setSourceAndDestination(body);
            //Context context = Platform.getInstance().getContext();
            if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Fetching nearby users..");
            SBHttpRequest getNearbyUsersRequest = new InstaRequest();
            SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);          
            logSuccess();
		} catch (JSONException e) {
			logServererror();
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server on user add scr dst");
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Network error,try again");
			e.printStackTrace();
		}
		
		
	}
}
