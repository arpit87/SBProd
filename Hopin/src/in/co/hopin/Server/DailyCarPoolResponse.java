package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;

public class DailyCarPoolResponse extends ServerResponseBase{


	private static final String TAG = "in.co.hopin.Server.GetCarPoolUsersResponse";
	
	
	public DailyCarPoolResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
				
	}
	
	@Override
	public void process() {
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing GetMatchingCarPoolUsersResponse response..geting json");
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			
		} catch (JSONException e) {
			logServererror();
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Network error,try again");
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server in fetching nearby carpool user.JSON:"+jobj.toString());
			e.printStackTrace();
			return;
		}		
		
		CurrentNearbyUsers.getInstance().updateNearbyUsersFromJSON(body);	
		FriendsToInvite.getInstance().updateFriendsToInviteFromJSON(body);
		//MapListActivityHandler.getInstance().updateNearbyUsers();	
		if(CurrentNearbyUsers.getInstance().usersHaveChanged())
		{
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating changed nearby carpool users");
			
			if(mListener!=null)
				mListener.onComplete(BroadCastConstants.NEARBY_USER_UPDATED);		
						
		}
		logSuccessWithArg(HopinTracker.NUMMATCHES, Integer.toString(CurrentNearbyUsers.getInstance().getAllNearbyUsers().size()));
		ProgressHandler.dismissDialoge();
		
	}
	
	

}
