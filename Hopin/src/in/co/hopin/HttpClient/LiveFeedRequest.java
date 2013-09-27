package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Server.GetFriendListToInviteResponse;
import in.co.hopin.Server.LiveFeedResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
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

public class LiveFeedRequest extends SBHttpRequest{

    private static String RESTAPI="getRideTicker";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQueryGetFriendList;	
	JSONObject jsonobjFriendList;
	HttpClient httpclient = new DefaultHttpClient();
	LiveFeedResponse feedResponse;
	String jsonStr;
	SBHttpResponseListener mListener;
	public LiveFeedRequest(String fbid,long cuttofftime,SBHttpResponseListener listener)
	{
		
		super(URL,RESTAPI);
		queryMethod = QueryMethod.Post;
		
		mListener = listener;
		//prepare getnearby request		
		httpQueryGetFriendList = new HttpPost(URL);
		jsonobjFriendList = GetServerAuthenticatedJSON();;
		try {						
			jsonobjFriendList.put(UserAttributes.LIVEFEEDFBID, fbid);
			jsonobjFriendList.put(UserAttributes.CUTTOFFTIME, cuttofftime);		
			SBGeoPoint currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
			if(currGeo!=null)
			{
				jsonobjFriendList.put(UserAttributes.LIVEFEEDLAT, currGeo.getLatitude());	
				jsonobjFriendList.put(UserAttributes.LIVEFEEDLONG, currGeo.getLongitude());	
			}
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
			
			feedResponse =	new LiveFeedResponse(response,jsonStr,RESTAPI);
			feedResponse.setReqTimeStamp(this.reqTimeStamp);
			feedResponse.setResponseListener(mListener);
			return feedResponse;
		
	}
	
	

}
