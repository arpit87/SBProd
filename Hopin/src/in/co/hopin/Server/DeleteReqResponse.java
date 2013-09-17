package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;

public class DeleteReqResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.DeleteUserResponse";
	int daily_insta_type;

	public DeleteReqResponse(HttpResponse response,String jobjStr,int daily_insta_type,String api) {
		super(response,jobjStr,api);
		this.daily_insta_type = daily_insta_type;
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing PostUserReqDataResponse");
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"server response:"+jobj.toString());
		try {
			//body = jobj.getJSONObject("body");
			String body = jobj.getString("body");
			ToastTracker.showToast("Request deleted successfully");		
			if(daily_insta_type == 1) {
                ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_INSTA, "");
                if(mListener!=null)
                	mListener.onComplete(BroadCastConstants.INSTAREQ_DELETED);
            }
			else {
                ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_CARPOOL, "");
                if(mListener!=null)
                	mListener.onComplete(BroadCastConstants.CARPOOLREQ_DELETED);
            }			
			logSuccess();
		} catch (JSONException e) {
			logServererror();
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Some error occured in delete request");
			e.printStackTrace();
		}
	}
}
