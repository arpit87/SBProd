package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Server.GetFriendListToInviteResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class GetFriendListToInviteRequest extends SBHttpRequest{

    private static String RESTAPI="getUserFriends";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQueryGetFriendList;	
	JSONObject jsonobjFriendList;
	HttpClient httpclient = new DefaultHttpClient();
	GetFriendListToInviteResponse friendListResponse;
	String jsonStr;
	SBHttpResponseListener mListener;
	public GetFriendListToInviteRequest(int offset,int limit,SBHttpResponseListener listener)
	{
		
		super();
		queryMethod = QueryMethod.Post;
		URLStr = URL;		
		mListener = listener;
		//prepare getnearby request		
		httpQueryGetFriendList = new HttpPost(URL);
		jsonobjFriendList = GetServerAuthenticatedJSON();;
		try {						
			jsonobjFriendList.put(UserAttributes.FBID, ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
			jsonobjFriendList.put(UserAttributes.OFFSET, offset);
			jsonobjFriendList.put(UserAttributes.LIMIT, limit);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobjFriendList.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		Logger.d("debug", "calling server:"+jsonobjFriendList.toString());	
		httpQueryGetFriendList.setEntity(postEntitygetNearbyUsers);
		
	}
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
			try {
				response=httpclient.execute(httpQueryGetFriendList);
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
			
			friendListResponse =	new GetFriendListToInviteResponse(response,jsonStr,RESTAPI);
			friendListResponse.setReqTimeStamp(this.reqTimeStamp);
			friendListResponse.setResponseListener(mListener);
			return friendListResponse;
		
	}
	
	

}
