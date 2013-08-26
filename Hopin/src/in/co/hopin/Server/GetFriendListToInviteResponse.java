package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.Logger;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.UiSettings;

import android.content.Intent;

public class GetFriendListToInviteResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.GetFriendListToInviteResponse";
	

	public GetFriendListToInviteResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		Logger.i(TAG,"processing GetFriendListToInviteResponse");
		Logger.i(TAG,"server response:"+jobj.toString());
		try {
			body = jobj.getJSONObject("body");			
			FriendsToInvite.getInstance().updateFriendsToInviteFromJSON(body);
			getResponseListener().onComplete("");			
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ToastTracker.showToast("Some error occured in getting friends to invite request");
			e.printStackTrace();
		}
	}
}
