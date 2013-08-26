package in.co.hopin.Users;

import in.co.hopin.HelperClasses.JSONHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;

import org.json.JSONObject;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/****
 * 
 * @author arpit87
 * this maintains a list of all friends to invite getFriendsToInvite request
 *
 */
public class FriendsToInvite {
	
	private static String TAG = "in.co.hopin.Users.FriendsToInvite" ;
	private HashMap<String, Friend> FBID_FriendToInviteMap = new HashMap<String, Friend>(); //store fbid<-> friend obj map
	private List<Friend> mInviteFriendList = Collections.EMPTY_LIST;
	private static FriendsToInvite instance=new FriendsToInvite();	
	public static FriendsToInvite getInstance() {
		 return instance;
	}
	
	public void updateFriendsToInviteFromJSON(JSONObject body)
	{		
		Logger.i(TAG,"updateFriendsToInviteFromJSON");		
		mInviteFriendList = JSONHandler.GetFriendsToInviteFromJSONObject(body);
		
	}

	public List<Friend> getAllFriends()
	{
		return mInviteFriendList;
	}
	
	
	public Friend getFriendWithFBID(String FBid)
	{
		Friend n;
		n = FBID_FriendToInviteMap.get(FBid);
		return n;
	}
	
	public Friend getFriendAtPosition(int id)
	{
		Friend n;
		n = mInviteFriendList.get(id);
		return n;
	}
	
	
	public void clearAllData()
	{
		if(mInviteFriendList!=null)
			mInviteFriendList.clear();
		
		if(FBID_FriendToInviteMap!=null)
			FBID_FriendToInviteMap.clear();
	}
	
}