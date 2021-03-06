package in.co.hopin.HttpClient;


import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.GetNewUserInfoAndShowPopup;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetNewUserInfoAndShowPopupRequest extends SBHttpRequest{
	
	private static final String TAG = "in.co.hopin.HttpClient.GetFBInfoForUserIDAndShowPopup";
	private static String RESTAPI="getInfo";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQuery;		
	HttpClient httpclient = new DefaultHttpClient();	
	GetNewUserInfoAndShowPopup getNewUserInfoAndShowPopup;
	JSONObject jsonobj;
	String jsonStr;
	String source = "";
	String destination = "";
    String timeOfTravel = "";
    String targetUserId = "";
    int daily_insta_type;
	
	public GetNewUserInfoAndShowPopupRequest(String target_user_id,int daily_insta_type)
	{		
		super();
		queryMethod = QueryMethod.Post;
		this.daily_insta_type = daily_insta_type;		
		//prepare getnearby request		
		httpQuery = new HttpPost(URL);
		jsonobj = GetServerAuthenticatedJSON();		
		this.targetUserId = target_user_id;
		URLStr = URL;
		try {				
			jsonobj.put(UserAttributes.TARGETUSERID, targetUserId);		
			jsonobj.put(UserAttributes.DAILYINSTATYPE, daily_insta_type);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		Logger.d(TAG, "calling server:"+jsonobj.toString());	
		httpQuery.setEntity(postEntitygetNearbyUsers);
	
	}
	
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
			try {
				response=httpclient.execute(httpQuery);
			} catch (Exception e) {
				HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute:executeexception",1L);
			}

			try {
				if(response==null)
					return null;
				jsonStr = responseHandler.handleResponse(response);
			} catch (Exception e) {
				HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute:responseexception",1L);
			} 	
			
		getNewUserInfoAndShowPopup = new GetNewUserInfoAndShowPopup(response,jsonStr,daily_insta_type,RESTAPI);
		getNewUserInfoAndShowPopup.setReqTimeStamp(this.reqTimeStamp);
		return getNewUserInfoAndShowPopup;
		
	}
	
	

}



