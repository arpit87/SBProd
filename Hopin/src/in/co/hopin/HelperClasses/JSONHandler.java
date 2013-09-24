package in.co.hopin.HelperClasses;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.Friend;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Util.Logger;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONHandler {
	
		
	public static List<NearbyUser> GetNearbyUsersInfoFromJSONObject(JSONObject jObj)
	{
		
		
		ArrayList<NearbyUser> nearbyUsers = new ArrayList<NearbyUser>();
		try {			
						
			JSONArray users = jObj.getJSONArray("NearbyUsers");
			
			for(int i=0;i<users.length();i++)
			{
				JSONObject thisOtherUser=users.getJSONObject(i);
				if (Platform.getInstance().isLoggingEnabled()) Log.d("json",thisOtherUser.toString());
				NearbyUser u = new NearbyUser(thisOtherUser);
				nearbyUsers.add((u));				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nearbyUsers;
		
	}
	
	public static List<Friend> GetFriendsToInviteFromJSONObject(JSONObject jObj)
	{
		
		
		ArrayList<Friend> friendsToInvite = new ArrayList<Friend>();
		try {			
						
			JSONArray allFriendsToInvite = jObj.getJSONArray("FriendsToInvite");
			
			for(int i=0;i<allFriendsToInvite.length();i++)
			{
				JSONObject thisFriend=allFriendsToInvite.getJSONObject(i);
				Logger.d("json",thisFriend.toString());
				Friend f = new Friend(thisFriend);
				friendsToInvite.add((f));				
			}
			
		} catch (JSONException e) {
			
		}
		return friendsToInvite;
		
	}
	
	public static List<Friend> GetMutualFriendsFromJSONObject(JSONObject jObj)
	{
		
		
		ArrayList<Friend> friends = new ArrayList<Friend>();;
		try {			
						
			JSONArray users = jObj.getJSONArray("mutual_friends");
						
			if(users.length() > 0)
				friends = new ArrayList<Friend>();
			
			for(int i=0;i<users.length();i++)
			{
				JSONObject thisMutualFriend=users.getJSONObject(i);
				Logger.d("json",thisMutualFriend.toString());
				Friend m = new Friend(thisMutualFriend);
				friends.add((m));				
			}
			
		} catch (JSONException e) {
			
		}
		return friends;
		
	}	
	
	public static List<Friend> GetFriendsFromJSONObject(JSONObject jObj)
	{
		
		//for 0 users we are returning null and not zero size list
		ArrayList<Friend> friends = new ArrayList<Friend>();;
		try {			
						
			JSONArray users = jObj.getJSONArray("friends");
						
			if(users.length() > 0)
				friends = new ArrayList<Friend>();
			
			for(int i=0;i<users.length();i++)
			{
				JSONObject thisFriend=users.getJSONObject(i);
				Logger.d("json",thisFriend.toString());
				Friend m = new Friend(thisFriend);
				friends.add((m));				
			}
			
		} catch (JSONException e) {			
		}
		return friends;
		
	}	
	
	public static List<Friend> GetHopinFriendsFromJSONObject(JSONObject jObj)
	{
		
		
		ArrayList<Friend> hopinfriends = new ArrayList<Friend>();;
		try {			
						
			JSONArray users = jObj.getJSONArray("HopinFriends");
						
			if(users.length() > 0)
				hopinfriends = new ArrayList<Friend>();
			
			for(int i=0;i<users.length();i++)
			{
				JSONObject thisHopinFriend=users.getJSONObject(i);
				Logger.d("json",thisHopinFriend.toString());
				Friend m = new Friend(thisHopinFriend);
				hopinfriends.add((m));				
			}
			
		} catch (JSONException e) {			
		}
		return hopinfriends;
		
	}	
	
	public static List<LiveFeed> GetLiveFeedFromJSONObject(JSONObject jObj)
	{
		
		
		ArrayList<LiveFeed> liveFeed = new ArrayList<LiveFeed>();;
		try {			
						
			JSONArray feed = jObj.getJSONArray("LiveFeed");
						
			if(feed.length() > 0)
			{
				Logger.d("LiveFeed length:",Integer.toString(feed.length()));
				liveFeed = new ArrayList<LiveFeed>();
			}
			
			for(int i=0;i<feed.length();i++)
			{
				JSONObject thisliveFeed=feed.getJSONObject(i);
				Logger.d("json",thisliveFeed.toString());
				LiveFeed m = new LiveFeed(thisliveFeed);
				liveFeed.add((m));				
			}
			
		} catch (JSONException e) {	
			e.printStackTrace();
		}
		return liveFeed;
		
	}	

}
