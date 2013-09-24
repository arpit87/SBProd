package in.co.hopin.HelperClasses;

import in.co.hopin.Users.UserAttributes;

import org.json.JSONException;
import org.json.JSONObject;

public class LiveFeed {
	String namelocation = "";
	String source = "";
	String destination = "";
	String fbid = "";
	String time = "";
	JSONObject allInfo = null;
	
	
	public LiveFeed(JSONObject jsonObject) {
        allInfo = jsonObject;
        try {
        	fbid = allInfo.getString(UserAttributes.LIVEFEEDFBID);
        } catch (JSONException e) {
        	
        }
        
        try {
        	namelocation = allInfo.getString(UserAttributes.LIVEFEEDLOCATION);
        } catch (JSONException e) {
        	
        }
        
        try {
        	destination = allInfo.getString(UserAttributes.LIVEFEEDDESTINATION);
        } catch (JSONException e) {
        	
        }  
        
        try {
        	source = allInfo.getString(UserAttributes.LIVEFEEDSOURCE);
        } catch (JSONException e) {
        	
        }
        try {
        	time = allInfo.getString(UserAttributes.LIVEFEEDTIME);
        } catch (JSONException e) {
        	
        }
       
	}
	
	public String getNameLocation() {
		return namelocation;
	}


	public String getSource() {
		return source;
	}
	
	public String getTime() {
		return time;
	}


	public String getDestination() {
		return destination;
	}


	public String getFbid() {
		return fbid;
	}

	

}
