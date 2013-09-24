package in.co.hopin.HttpClient;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.DailyCarPoolResponse;
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

public class DailyCarPoolRequest  extends SBHttpRequest{

    private static String RESTAPI="getCarpoolMatches";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQueryGetNearbyUsers;	
	JSONObject jsonobjGetNearbyUsers;
	HttpClient httpclient = new DefaultHttpClient();
	DailyCarPoolResponse getNearbyUsersResponse;
	String jsonStr;
	SBHttpResponseListener mListener = null;
	
	public DailyCarPoolRequest(SBHttpResponseListener listener)
	{		
		super(URL,RESTAPI);
		queryMethod = QueryMethod.Post;
				
		//prepare getnearby request		
		httpQueryGetNearbyUsers = new HttpPost(URL);
		jsonobjGetNearbyUsers = GetServerAuthenticatedJSON();
		mListener = listener;
			
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobjGetNearbyUsers.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+jsonobjGetNearbyUsers.toString());	
		httpQueryGetNearbyUsers.setEntity(postEntitygetNearbyUsers);
		
	}
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
			try {
				response=httpclient.execute(httpQueryGetNearbyUsers);
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
			
			getNearbyUsersResponse =	new DailyCarPoolResponse(response,jsonStr,RESTAPI);
			getNearbyUsersResponse.setReqTimeStamp(this.reqTimeStamp);
			getNearbyUsersResponse.setResponseListener(mListener);
			return getNearbyUsersResponse;
		
	}
	
	

}
