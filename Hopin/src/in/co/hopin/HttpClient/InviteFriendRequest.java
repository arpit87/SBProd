package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.InstaResponse;
import in.co.hopin.Server.InviteFriendResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class InviteFriendRequest extends SBHttpRequest{

    private static String RESTAPI="inviteFriend";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQueryInviteFriend;	
	JSONObject jsonobjInviteFriend;
	HttpClient httpclient = new DefaultHttpClient();
	InviteFriendResponse inviteResponse;
	String jsonStr;
	public InviteFriendRequest(String friendFBID)
	{
		
		super();
		queryMethod = QueryMethod.Post;
		URLStr = URL;		
		//prepare getnearby request		
		httpQueryInviteFriend = new HttpPost(URL);
		jsonobjInviteFriend = GetServerAuthenticatedJSON();;
		try {						
			jsonobjInviteFriend.put(UserAttributes.INVITATIONFROM, ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
			jsonobjInviteFriend.put(UserAttributes.INVITATIONTO, friendFBID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobjInviteFriend.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+jsonobjInviteFriend.toString());	
		httpQueryInviteFriend.setEntity(postEntitygetNearbyUsers);
		
	}
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
			try {
				response=httpclient.execute(httpQueryInviteFriend);
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
			
			inviteResponse =	new InviteFriendResponse(response,jsonStr,RESTAPI);
			inviteResponse.setReqTimeStamp(this.reqTimeStamp);
			return inviteResponse;
		
	}
	
	

}
