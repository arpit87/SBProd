package in.co.hopin.Users;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {
	
	private String fb_id = "";
	private String name = "";
	JSONObject allInfo = null;
	private String installed_hopin = "0";
	private String invited = "0";
	private boolean invitationJustSent = false;
	
	
	
	public Friend(JSONObject jsonObject) {
        allInfo = jsonObject;
        try {
        	fb_id = allInfo.getString(UserAttributes.FRIENDFBID);
        } catch (JSONException e) {
        	
        }
        
        try {
        	name = allInfo.getString(UserAttributes.FRIENDNAME);
        } catch (JSONException e) {
        	
        }
        
        try {
        	installed_hopin = allInfo.getString(UserAttributes.INSTALLEDHOPIN);
        } catch (JSONException e) {
        	
        }
        
        try {
        	invited = allInfo.getString(UserAttributes.INVITED);
        } catch (JSONException e) {
        }
	}
	
	public String getFb_id() {
		return fb_id;
	}
	public String getName() {
		return name;
	}
	
	public String getImageURL() {
		String picurl = "http://graph.facebook.com/" + fb_id + "/picture?type=small";
		return picurl;
	}
	
	public boolean hasInstalledHopin()
	{
		if(installed_hopin.equals("1"))
			return true;
		else 
			return false;		
	}
	
	public boolean hasBeenInvited()
	{
		if(invited.equals("1"))
			return true;
		else 
			return false;		
	}
	
	public boolean isInvitationJustSent() {
		return invitationJustSent;
	}

	public void setInvitationJustSent(boolean invitationJustSent) {
		this.invitationJustSent = invitationJustSent;
	}
	
	public void invitationSentSuccessfully()
	{
		invited = "1";
		invitationJustSent = false;
	}

}
