package in.co.hopin.Users;

import org.json.JSONException;
import org.json.JSONObject;


public class UserOtherInfo {

	JSONObject allInfo = null;
	private String type = "0";
	private String percent_match = "0";	
	private String username ="";
	private String userid = "";
	private String is_available = "0";
	
	
		
	public UserOtherInfo(JSONObject jsonObject) {
		allInfo = jsonObject;
				
		try {
			type = allInfo.getString(UserAttributes.SHAREOFFERTYPE);			
		} catch (JSONException e) {	}
		
		try {
			percent_match = allInfo.getString(UserAttributes.PERCENTMATCH);			
		} catch (JSONException e) {	}
						
		try {
			username = allInfo.getString(UserAttributes.USERNAME);			
		} catch (JSONException e) {	}
		
		try {
			userid = allInfo.getString(UserAttributes.USERID);			
		} catch (JSONException e) {	}
		
		try {
			is_available = allInfo.getString(UserAttributes.ISAVAILABLE);			
		} catch (JSONException e) {	}
				
	}
	
	public UserOtherInfo() {
		// TODO Auto-generated constructor stub
	}

	public boolean isOfferingRide()
	{
		if(type.equals("0"))
			return false;
		else
			return true;
	}
	
	public boolean isOnline()
	{
		if(is_available.equals("1"))
			return true;
		else
			return false;
	}
	
	
	
	public int getPercentMatch()
	{		
		return Integer.parseInt(percent_match);
	}
	
	public String getUserID()
	{
		return userid;
	}
	
	public String getRideType()
	{
		return type;
	}
	
	public String getUserName()
	{
		return username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;		
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		UserOtherInfo other = (UserOtherInfo) obj;		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
		
}


