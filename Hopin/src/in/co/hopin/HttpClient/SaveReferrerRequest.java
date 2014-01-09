package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.InstaResponse;
import in.co.hopin.Server.SaveReferralResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;

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

public class SaveReferrerRequest extends SBHttpRequest{

    private static String RESTAPI="saveReferrer";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQuerySaveReferral;	
	JSONObject jsonobj;
	HttpClient httpclient = new DefaultHttpClient();
	SaveReferralResponse saveReferralResponse;
	String jsonStr;
	SBHttpResponseListener mListener = null;
	public SaveReferrerRequest(SBHttpResponseListener listener)
	{
		
		super(URL,RESTAPI);
		queryMethod = QueryMethod.Post;
		mListener = listener;		
		//prepare getnearby request		
		httpQuerySaveReferral = new HttpPost(URL);
		jsonobj = GetServerAuthenticatedJSON();;
		String referralStr = ThisAppConfig.getInstance().getString(ThisAppConfig.REFERRER_STRING);	
		
		try {			
			jsonobj.put(UserAttributes.REFERRALSTRING, referralStr);			
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
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+httpQuerySaveReferral.toString());	
		httpQuerySaveReferral.setEntity(postEntitygetNearbyUsers);
		
	}
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
			try {
				response=httpclient.execute(httpQuerySaveReferral);
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
			
			saveReferralResponse =	new SaveReferralResponse(response,jsonStr,RESTAPI);
			saveReferralResponse.setReqTimeStamp(this.reqTimeStamp);
			saveReferralResponse.setResponseListener(mListener);
			return saveReferralResponse;
		
	}
	
	

}
