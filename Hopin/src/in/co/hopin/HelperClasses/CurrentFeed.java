package in.co.hopin.HelperClasses;

import in.co.hopin.Users.UserAttributes;

import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentFeed {
	
	private static CurrentFeed instance = null;
	private long cuttofftime = 0;
	
	List<LiveFeed> mFeedList = Collections.emptyList();
	
	public static CurrentFeed getInstance()
	{
		if(instance == null)
			instance = new CurrentFeed();
		return instance;
		
	}
	
	public void updateLiveFeedFromJSON(JSONObject body)
	{		
		mFeedList = JSONHandler.GetLiveFeedFromJSONObject(body);
		try {
			cuttofftime = body.getLong(UserAttributes.CUTTOFFTIME);
		} catch (JSONException e) {			
		}
	}
	
	public List<LiveFeed> getLiveFeedList()
	{
		return mFeedList;
	}
	
	public long getCuttoffTime()
	{
		return cuttofftime;
	}
		

}
