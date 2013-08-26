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

public class InviteFriendResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.InviteFriendResponse";
	

	public InviteFriendResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		Logger.i(TAG,"processing InviteFriendResponse");
		Logger.i(TAG,"server response:"+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			
			Intent notifyInvitationSentintent = new Intent();
			
			notifyInvitationSentintent.setAction(BroadCastConstants.FRIEND_INVITATION_SENT);
			//we will get fbidback in success
			notifyInvitationSentintent.putExtra(UserAttributes.FRIENDFBID, body.getString("Success"));
			//this broadcast is for my active req page to update itself to no active req
			Platform.getInstance().getContext().sendBroadcast(notifyInvitationSentintent);
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ToastTracker.showToast("Some error occured in invite friend request");
			e.printStackTrace();
		}
	}
}
