package in.co.hopin.Users;

import in.co.hopin.HelperClasses.JSONHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import org.json.JSONObject;

import android.util.Log;

import java.util.ArrayList;
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
		if(mInviteFriendList.size()>0)
		{
			for(Friend f : mInviteFriendList)
			{
				FBID_FriendToInviteMap.put(f.getFb_id(), f);
			}
		}	
		
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
	
	public List<String> getAllSelectedFriendEmails()
	{
		List<String> emaillist = new ArrayList<String>();
		if(mInviteFriendList.size()>0)
		{
			for(Friend f : mInviteFriendList)
			{
				if(f.isSelected())
					emaillist.add(f.getFBUserName() + "@facebook.com");
			}
		}	
		return emaillist;		
	}
	
	public String getAllSelectedFriendCommaSeparatedIDs()
	{
		String ids ="";
		if(mInviteFriendList.size()>0)
		{
			for(Friend f : mInviteFriendList)
			{
				if(f.isSelected())
				{
					if(ids.equals(""))
						ids = f.getFb_id();
					else
						ids= ids+","+f.getFb_id();
				}
			}
		}	
		return ids;		
	}
	
	public void clearAllData()
	{
		if(!mInviteFriendList.isEmpty())
			mInviteFriendList.clear();
		
		if(FBID_FriendToInviteMap!=null)
			FBID_FriendToInviteMap.clear();
	}
	
}
