package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.Friend;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.Logger;

import org.apache.http.HttpResponse;
import org.json.JSONException;

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
			
			/*Intent notifyInvitationSentintent = new Intent();
			
			notifyInvitationSentintent.setAction(BroadCastConstants.FRIEND_INVITATION_SENT);
			//we will get fbidback in success
			String fbid_of_invitedfriend = "";
			fbid_of_invitedfriend = body.getString("Success");
			Friend invited_friend = FriendsToInvite.getInstance().getFriendWithFBID(fbid_of_invitedfriend);
			if(invited_friend!=null)
			{
				Logger.i(TAG,"friend invited successfully:"+fbid_of_invitedfriend);
				invited_friend.invitationSentSuccessfully();
			}
			//this broadcast is for my active req page to update itself to no active req
			//Platform.getInstance().getContext().sendBroadcast(notifyInvitationSentintent);*/
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			//ToastTracker.showToast("Some error occured in invite friend request");
			e.printStackTrace();
		}
	}
}
