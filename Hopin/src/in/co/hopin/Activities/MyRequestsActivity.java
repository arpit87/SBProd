package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Activities.InviteFriendsActivity.GetFriendsListListener;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.DailyCarPoolRequest;
import in.co.hopin.HttpClient.DeleteRequest;
import in.co.hopin.HttpClient.InstaRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyRequestsActivity extends Activity {
	
	private final String TAG = "in.co.hopin.Avtivity.MyRequestActivity";
	TextView carpoolsource;
	TextView carpooldestination;
	TextView carpooltime;
	TextView instasource;
	TextView instadestination;
	TextView instatime;
	Button deleteCarpoolReq;
	Button deleteInstaReq;	
	View instaActiveLayout;
	View carPoolActiveLayout;
	TextView carPoolNoActiveReq;
	TextView instaNoActiveReq;	
    private Button showUsersInsta;
    private Button showUsersCarpool;
    private RequestDeletedListener mReqDeleteListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_requests_layout);  
        carpoolsource = (TextView) findViewById(R.id.my_requests_carpool_source);
        carpooldestination = (TextView) findViewById(R.id.my_requests_carpool_destination);
        carpooltime = (TextView) findViewById(R.id.my_requests_carpool_details);
        instasource = (TextView) findViewById(R.id.my_requests_insta_source);
        instadestination = (TextView) findViewById(R.id.my_requests_insta_destination);
        instatime = (TextView) findViewById(R.id.my_requests_insta_details);
        showUsersInsta = (Button) findViewById(R.id.show_users_insta);
        showUsersCarpool = (Button) findViewById(R.id.show_users_carpool);
        deleteCarpoolReq = (Button) findViewById(R.id.my_requests_carpool_deletereq);
        deleteInstaReq = (Button) findViewById(R.id.my_requests_insta_deletereq);      
        instaActiveLayout = (View)findViewById(R.id.my_requests_instareq_layout);
        carPoolActiveLayout = (View)findViewById(R.id.my_requests_carpoolreq_layout);
        carPoolNoActiveReq = (TextView)findViewById(R.id.my_requests_carpool_noactivereq);
        instaNoActiveReq = (TextView)findViewById(R.id.my_requests_insta_noactivereq);        
        
        deleteCarpoolReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				
				ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting carpool request", "Please wait",getReqListener());
				DeleteRequest deleteRequest = new DeleteRequest(0,getReqListener());
                SBHttpClient.getInstance().executeRequest(deleteRequest);
                
			}
		});
        
		deleteInstaReq.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View paramView) {
						HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:dailycarpool:delete",1L);
						HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:onetime:delete",1L);
						
						ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting insta request", "Please wait",getReqListener());
						DeleteRequest deleteRequest = new DeleteRequest(1,getReqListener());
		                SBHttpClient.getInstance().executeRequest(deleteRequest);
		                
					}
				});

        showUsersCarpool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:dailycarpool:showusers",1L);
                try {
                    String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
                    final JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
                    MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
                    ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching carpool matches", "Please wait",MapListActivityHandler.getInstance().getNearbyUserUpdatedListener());
                    SBHttpRequest getNearbyUsersRequest = new DailyCarPoolRequest(MapListActivityHandler.getInstance().getNearbyUserUpdatedListener());
                    SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
                    finish();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        showUsersInsta.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                	HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:onetime:showusers",1L);
                    String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
                    final JSONObject responseJsonObj = new JSONObject(instaReqJson);
                    MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
                    ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching matches", "Please wait",MapListActivityHandler.getInstance().getNearbyUserUpdatedListener());
                    SBHttpRequest getNearbyUsersRequest = new InstaRequest(MapListActivityHandler.getInstance().getNearbyUserUpdatedListener());
                    SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
                    finish();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
       
    }

    @Override
    protected void onResume(){
    	super.onResume();    	
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);   
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"carpooljson:"+carpoolReqJson);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"instajson:"+instaReqJson);
        if(!StringUtils.isBlank(carpoolReqJson))
        {
        	carPoolActiveLayout.setVisibility(View.VISIBLE);
        	carPoolNoActiveReq.setVisibility(View.GONE);
        	
        	try {
				JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
				String source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
				String destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
				carpoolsource.setText(source);
				carpooldestination.setText(destination);
				carpooltime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!StringUtils.isBlank(instaReqJson))
        {   
        	instaActiveLayout.setVisibility(View.VISIBLE);
        	instaNoActiveReq.setVisibility(View.GONE);
        	try {
				JSONObject responseJsonObj = new JSONObject(instaReqJson);
				String source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
				String destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
				instasource.setText(source);
				instadestination.setText(destination);
				instatime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM, hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    
    public void setCarpoolReqLayoutToNoActiveReq()
    {
    	carPoolActiveLayout.setVisibility(View.GONE);
    	carPoolNoActiveReq.setVisibility(View.VISIBLE);    	
    }
    
    public void setInstaReqLayoutToNoActiveReq()
    {
    	instaActiveLayout.setVisibility(View.GONE);
    	instaNoActiveReq.setVisibility(View.VISIBLE);    	
    }

    @Override
    public void onStart(){
        super.onStart();
        HopinTracker.sendView("MyRequests");
        HopinTracker.sendEvent("MyRequests","ScreenOpen","myrequests:open",1L);
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();      
        //EasyTracker.getInstance().activityStop(this);
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
    	HopinTracker.sendEvent("MyRequest","BackButton","myrequests:click:back",1L);
    }
    
    private void reInitialize(){
        MapListActivityHandler.getInstance().clearAllData();
        ThisUserNew.getInstance().reset();
        MapListActivityHandler.getInstance().myLocationButtonClick();
        MapListActivityHandler.getInstance().centreMapTo(ThisUserNew.getInstance().getSourceGeoPoint());
    }
    
    public RequestDeletedListener getReqListener()
	{
		if(mReqDeleteListener == null)
			mReqDeleteListener = new RequestDeletedListener();
		return mReqDeleteListener;
	}
    
    class RequestDeletedListener extends SBHttpResponseListener
	{

		@Override
		public void onComplete(String response) {
			if(!hasBeenCancelled)
			{
				if(response.equals(BroadCastConstants.CARPOOLREQ_DELETED))
				{
					setCarpoolReqLayoutToNoActiveReq();	
					reInitialize();
				}
				if(response.equals(BroadCastConstants.INSTAREQ_DELETED))
				{
					setInstaReqLayoutToNoActiveReq();
					reInitialize();
				}				
			}
		}
		
	}
}
