package in.co.hopin.Users;

import in.co.hopin.HelperClasses.JSONHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

/****
 * 
 * @author arpit87
 * this maintains a list of all nearby users from most recent getMatchingUsers request
 *
 */
public class CurrentNearbyUsers {
	
	private static String TAG = "in.co.hopin.Users.CurrentNearbyUsers" ;
	private HashMap<String, NearbyUser> FBID_NearbyUserMap = new HashMap<String, NearbyUser>(); //store fbid<-> nearbyuser obj map
	private List<NearbyUser> mCurrentNearbyUserList = Collections.emptyList();
	private List<NearbyUser> mNewNearbyUserList = Collections.emptyList();
	private static CurrentNearbyUsers instance=new CurrentNearbyUsers();
	private boolean updatedToCurrent = false;	
	public static CurrentNearbyUsers getInstance() {
		 return instance;
	}
	
	public void updateNearbyUsersFromJSON(JSONObject body)
	{		
		//we temporarily put new users in new list and MapHandler has to check if changed and callupdate then we change current to new
		
		Logger.i(TAG,"updating nearby users");
		updatedToCurrent = false;
		mNewNearbyUserList = JSONHandler.GetNearbyUsersInfoFromJSONObject(body);		
		//else
		//	ToastTracker.showToast("sorry no match found");
		
	}

	public List<NearbyUser> getAllNearbyUsers()
	{
		return mCurrentNearbyUserList;
	}
	
	private void updateCurrentToNew()
	{
		mCurrentNearbyUserList = mNewNearbyUserList ;
		FBID_NearbyUserMap.clear();
		if(mCurrentNearbyUserList.size()>0)
		{
			for(NearbyUser n : mCurrentNearbyUserList)
			{
				FBID_NearbyUserMap.put(n.getUserFBInfo().getFbid(), n);
			}
		}		
		updatedToCurrent = true;
	}
	
	public NearbyUser getNearbyUserWithFBID(String FBid)
	{
		NearbyUser n;
		n = FBID_NearbyUserMap.get(FBid);
		return n;
	}
	
	public NearbyUser getNearbyUserAtPosition(int id)
	{
		NearbyUser n;
		n = mCurrentNearbyUserList.get(id);
		return n;
	}
	
	public boolean usersHaveChanged()
	{
        boolean haveUsersChanged = false;
		Logger.i(TAG,"chking if usr changed ");
		if(!updatedToCurrent) {
			if ((mCurrentNearbyUserList.isEmpty() && mNewNearbyUserList.isEmpty()))
					ToastTracker.showToast("Sorry no match found");
			else if ((mCurrentNearbyUserList.isEmpty() && !mNewNearbyUserList.isEmpty()) ||
                    (!mCurrentNearbyUserList.isEmpty() && mNewNearbyUserList.isEmpty())){
                if (mNewNearbyUserList.isEmpty()){
                    ToastTracker.showToast("Sorry no match found");
                }
                haveUsersChanged = true;
            } else {
                if (mCurrentNearbyUserList.size() != mNewNearbyUserList.size()){
                    haveUsersChanged = true;
                } else {
                    for (NearbyUser n : mNewNearbyUserList) {
                        if (!mCurrentNearbyUserList.contains(n)){
                            haveUsersChanged = true;
                            break;
                        }
                    }
                }
            }
        }

        if (haveUsersChanged){
            updateCurrentToNew();
        }

        return haveUsersChanged;
	}
	
	public void clearAllData()
	{
		if(!mCurrentNearbyUserList.isEmpty())
			mCurrentNearbyUserList.clear();
		if(!mNewNearbyUserList.isEmpty())
			mNewNearbyUserList.clear();
		if(FBID_NearbyUserMap!=null)
			FBID_NearbyUserMap.clear();
	}
	
}
