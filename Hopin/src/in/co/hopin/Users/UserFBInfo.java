package in.co.hopin.Users;

import in.co.hopin.HelperClasses.JSONHandler;
import in.co.hopin.Util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class UserFBInfo {

    JSONObject allInfo = null;
    private String fbinfoavailable = "0";
    private String firstName = "";
    private String lastName = "";
    private String gender = "";
    private String imageURL = "";
    private String worksAt = "";
    private String livesIn = "";
    private String studiedAt = "";
    private String hometown = "";
    private String current_city = "";
    private String fbid = "";
    private String fbusername = "";
    private String phone = "";
    private String email = "";
    private String mutual_friend_count="0";  
    private String friend_count="0"; 
    private String email_verified="0"; 
    private List<Friend> mMutualFriendList = new ArrayList<Friend>();
    private List<Friend> mHopinFriendList = new ArrayList<Friend>();
    public UserFBInfo() {
        // TODO Auto-generated constructor stub
    }

    public JSONObject getJsonObj()
    {    	
    	return allInfo;
    }
    
	public UserFBInfo(JSONObject jsonObject) {
        allInfo = jsonObject;
        try {
        	fbinfoavailable = allInfo.getString(UserAttributes.FBINFOAVAILABLE);
        } catch (JSONException e) {
        	return;
        }
        
        if(FBInfoAvailable())
        {
	        try {
	            firstName = allInfo.getString(UserAttributes.FB_FIRSTNAME);
	        } catch (JSONException e) {
	        }
	
	        try {
	            lastName = allInfo.getString(UserAttributes.FB_LASTNAME);
	        } catch (JSONException e) {
	        }
	
	        try {
	            gender = allInfo.getString(UserAttributes.GENDER);
	        } catch (JSONException e) {
	        }
	
	        try {
	            fbid = allInfo.getString(UserAttributes.FBID);
	        } catch (JSONException e) {
	        }
	
	        try {
	            imageURL = allInfo.getString(UserAttributes.IMAGEURL);
	        } catch (JSONException e) {
	        }
	
	        try {
	            worksAt = allInfo.getString(UserAttributes.WORKSAT);
	        } catch (JSONException e) {
	        }
	
	        try {
	            livesIn = allInfo.getString(UserAttributes.LIVESIN);
	        } catch (JSONException e) {
	        }
	
	        try {
	            studiedAt = allInfo.getString(UserAttributes.STUDYAT);
	        } catch (JSONException e) {
	        }
	
	        try {
	            hometown = allInfo.getString(UserAttributes.HOMETOWN);
	        } catch (JSONException e) {
	        }
	        
	        try {
	            current_city = allInfo.getString(UserAttributes.CURRENTCITY);
	        } catch (JSONException e) {
	        }
	        
	        try {
	            fbusername = allInfo.getString(UserAttributes.FBUSERNAME);
	        } catch (JSONException e) {
	        }
	        
	        try {
	            phone = allInfo.getString(UserAttributes.PHONE);
	        } catch (JSONException e) {
	        }
	        try {
	            email = allInfo.getString(UserAttributes.EMAIL);
	        } catch (JSONException e) {
	        }
	        
	        try {
	            mutual_friend_count = allInfo.getString(UserAttributes.MUTUALFRIENDSCOUNT);
	        } catch (JSONException e) {
	        }
	        
	        if(getNumberOfMutualFriends()>0)
	        	mMutualFriendList = JSONHandler.GetMutualFriendsFromJSONObject(allInfo);  
	        
	        try {
	            friend_count = allInfo.getString(UserAttributes.FRIENDSCOUNT);
	        } catch (JSONException e) {
	        }
	        
	            
	        try {
	            email_verified = allInfo.getString(UserAttributes.EMAILVERIFICATION);
	        } catch (JSONException e) {
	        }
	        
	        mHopinFriendList = JSONHandler.GetHopinFriendsFromJSONObject(allInfo); 
        }
    }
	
	public boolean FBInfoAvailable() {  
		if(fbinfoavailable.equals("1"))
			return true;
		else
			return false;
    }
	
	public boolean isPhoneAvailable() {  
		if(!phone.equalsIgnoreCase("null"))
			return true;
		else
			return false;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {    	
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFbid() {
        return fbid;
    }

    public String getImageURL() {
        return StringUtils.getFBPicURLFromFBID(fbid);
    }


    public String getWorksAt() {
        return worksAt;
    }


    public String getLivesIn() {
        return livesIn;
    }


    public String getStudiedAt() {
    	if(studiedAt.equals("null"))
    		return "Unknown";
    	else			
    		return studiedAt;
     }


    public String getHometown() {
    	if(hometown.equals("null"))
    		return "Unknown";
    	else			
    		return hometown;
    }
    
    public String getCurrentCity() {
    	if(hometown.equals("null"))
    		return "Unknown";
    	else			
    		return current_city;
    }
    
    public String getEmailVerificationStatus() {
    	if(email_verified.equals("0"))
    		return "Not Verified";
    	else			
    		return "Verified";
    }
    
    public String getFBUsername() {
        return fbusername;
    }
    
    public int getNumberOfMutualFriends()
    {
    	return Integer.parseInt(mutual_friend_count);
    }
    
    public int getNumberOfFriends()
    {
    	return Integer.parseInt(friend_count);
    }
    
    public String getEmail() {
        return email;
    }
    
    public List<Friend> getMutualFriends() {
        return mMutualFriendList;
    }
    
    public List<Friend> getHopinFriends() {
        return mHopinFriendList;
    }
    
    @Override
   	public int hashCode() {
   		final int prime = 31;
   		int result = 1;
   		result = prime * result + ((fbid == null) ? 0 : fbid.hashCode());
   		return result;
   	}

   	@Override
   	public boolean equals(Object obj) {
   		if (this == obj)
   			return true;
   		if (obj == null)
   			return false;
   		if (getClass() != obj.getClass())
   			return false;
   		UserFBInfo other = (UserFBInfo) obj;
   		if (fbid == null) {
   			if (other.fbid != null)
   				return false;
   		} else if (!fbid.equals(other.fbid))
   			return false;
   		return true;
   	}


}
