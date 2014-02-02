package in.co.hopin.FacebookHelpers;

import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.ChatServiceCreateUser;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SaveFBInfoRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;

public class FacebookConnector {
	
	private static final String TAG = "in.co.hopin.FacebookHelpers.FacebookConnector";
	
	public static String [] FB_PERMISSIONS = {"user_about_me","user_education_history","user_hometown","user_work_history","email"};
	//public static String FB_APP_ID = "107927182711315";	
	//public static String FB_APP_ID = "486912421326659"; //debug one
	
	private static FacebookConnector fbconnect = null;	
    Activity underlyingActivity = null;
       
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private Session.StatusCallback reloginstatusCallback = new SessionStatusReloginCallback();
       
    private void setActivity(Activity underlying_activity)
    {
    	underlyingActivity =  underlying_activity; 
    }
    
    public static FacebookConnector getInstance(Activity underlying_activity)
    {  
    	if(fbconnect == null)
    		fbconnect = new FacebookConnector();
    	fbconnect.setActivity(underlying_activity);   
    	return fbconnect;
    }
        
    public void logoutFromFB()
    { 
    	Session session = Session.getActiveSession();
    	session.closeAndClearTokenInformation();
    	eraseUserFBData();
    	
    }
    
    public boolean isSessionValid()
    {
    	 Session session = Session.getActiveSession();    	 
         Logger.i(TAG, "session null");
             if (session == null) {
                 session = new Session(underlyingActivity);
             }
             Session.setActiveSession(session);
             String access_token = session.getAccessToken();
        	 Logger.d(TAG, "expiry after is session valid"+session.getExpirationDate());
        	 Logger.d(TAG, "access token in issesson valid:"+access_token);
             Logger.d(TAG, "session state issesson valid:"+session.getState());
             if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	 session.openForRead(null);
            	 if(session.isOpened())
            	 {
            		 Logger.d(TAG, "session state issesson valid afr open:"+session.getState());
            		 return true;
            	 }
            	 else
            	 {
            		 Logger.d(TAG, "session state issesson valid aftr open:"+session.getState());
            		 return false;
            	 }
             }
             else if(session.getState().equals(SessionState.OPENED))
            	 return true;
             else if(session.getState().equals(SessionState.OPENED_TOKEN_UPDATED))
             {
            	 String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
            	 Map<String, Object> trackArgMap = new HashMap<String,Object>();
				 trackArgMap.put(HopinTracker.FBID, fbid);
            	 HopinTracker.sendEvent("FacebookLogin", "TokenUpdated", "facebook:issessionvalid:tokenupdated", 1L,trackArgMap);
            	 if(session.isOpened())
 				{ 					
 			    	ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, session.getAccessToken());
 		        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, session.getExpirationDate().getTime());
 			    	SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID),fbid , ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
 					SBHttpClient.getInstance().executeRequest(sendFBInfoRequest); 
 			        HopinTracker.sendEvent("FacebookLogin", "TokenUpdated", "facebook:tokenupdated:newtokensenttoserver", 1L,trackArgMap);
 			       return true;
 				}
            	 else
            		 return false;
            	 
             }
             else            	          
            	 return false;
            	
    }
    
    public void loginToFB()
    { 	
    	
    	//facebook.authorize(underlying_activity, permissions, new LoginDialogListener());
    	Logger.i(TAG, "login called");
    	
    	Session session = Session.getActiveSession();
    	
    	if (session == null) {
            session = new Session.Builder(underlyingActivity).build();
            Session.setActiveSession(session);            
        }
    	
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(underlyingActivity).setPermissions(FB_PERMISSIONS).setCallback(statusCallback));
        } else {
            Session.openActiveSession(underlyingActivity, true, statusCallback);
        }
       // String access_token = session.getAccessToken();
      //  Logger.d(TAG, "expiry after login"+session.getExpirationDate());
   	// Logger.d(TAG, "access token in login:"+access_token);
   //	Logger.d(TAG, "session state in login:"+session.getState());
 
    }  
    
    public void reloginToFB()
    { 	    	    	 
    	
    	Logger.i(TAG, "relogin called");
    	
		Session session = Session.getActiveSession();
    	
    	if (session == null) {
    		Logger.i(TAG, "relogin session null..cerating new");
            session = new Session(underlyingActivity);
            Session.setActiveSession(session);            
        }
    	
        if (!session.isOpened() && !session.isClosed()) {
       	Logger.i(TAG, "relogin opening session for read");
           session.openForRead(new Session.OpenRequest(underlyingActivity).setPermissions(FB_PERMISSIONS).setCallback(reloginstatusCallback));
        } else {
        	Logger.i(TAG, "relogin opening active session");
            Session.openActiveSession(underlyingActivity, false, reloginstatusCallback);
        }    	
        
        //String access_token = session.getAccessToken();
    	//Logger.d(TAG, "expiry after relogin:"+session.getExpirationDate());
   	 	//Logger.d(TAG, "access token after relogin:"+access_token);
   	// Logger.d(TAG, "session state in relogin:"+session.getState());
    } 
    
    public void authorizeCallback(int requestCode, int resultCode,Intent data)
    {
    	Session.getActiveSession().onActivityResult(underlyingActivity, requestCode, resultCode, data);
    }
    
       

    private void sendAddFBAndChatInfoToServer(String fbid) {
    	//this should only be called from fbpostloginlistener to ensure we have fbid
    	if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"in sendAddFBAndChatInfoToServer");
    	SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(fbid);
     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);
		SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), fbid, ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
		SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);			
	}
    
    private void eraseUserFBData()
    {
    	
	  //remove all fb info of this user	
	  
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, "");
	  ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES,-1);
	  ThisUserConfig.getInstance().putBool(ThisUserConfig.FBLOGGEDIN,false);			  
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FBPICURL, "");
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FBUSERNAME, "");
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FIRSTNAME, "");
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FB_LASTNAME, "");
	  ThisUserConfig.getInstance().putString(ThisUserConfig.FBUID, "");	
	  
	  //erase chat info too
	  ThisUserConfig.getInstance().putString(ThisUserConfig.CHATUSERID,"");
	  ThisUserConfig.getInstance().putString(ThisUserConfig.CHATPASSWORD,"");			  
	  ProgressHandler.dismissDialoge();
	  
	  //refresh user pic to silhutte
	  Platform.getInstance().getHandler().post(new Runnable(){

		@Override
		public void run() {
			 //MapListActivityHandler.getInstance().updateThisUserMapOverlay();
			 //MapListActivityHandler.getInstance().updateUserPicInListView();
			underlyingActivity.finish();
			 ToastTracker.showToast("Successfully logged out");					
		}			 		
	  });
			 
		 
    }  
   
    
	private void requestUserData(Session session,SessionState state) {
		HopinTracker.sendEvent("FacebookLogin", "login", "facebook:login:requestdata:execute", 1L);
		Logger.i(TAG,"requestUserData");		
		Context context= Platform.getInstance().getContext();		
		//Settings.publishInstallAsync(context, context.getResources().getString(in.co.hopin.R.string.fb_app_id));
		if (state.isOpened()) {	   

		    // Request user data and show the results
		   Request.newMeRequest(session, new Request.GraphUserCallback() {

		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            if (user != null) {	
		            	     	              
		     	            Logger.i(TAG,"got my fbinfo:"+user.getInnerJSONObject().toString());
		     	            String picurl,username,first_name,last_name,id,gender,email;
		     	            id = user.getId();//jsonObject.getString("id");
		     	            username = user.getUsername();//jsonObject.getString("username");
		     	            first_name  =user.getFirstName();//jsonObject.getString("first_name");
		     	            last_name = user.getLastName();// jsonObject.getString("last_name");
		     	            gender = (String) user.getProperty("gender");
		     	            email = (String) user.getProperty("email");
		     	            picurl = "http://graph.facebook.com/" + id + "/picture?type=small";
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBUID,id );
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBPICURL, picurl);
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBUSERNAME, username);
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.GENDER, gender);
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FIRSTNAME, first_name);
		     	            ThisUserConfig.getInstance().putString(ThisUserConfig.FB_LASTNAME, last_name);
		                    ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, first_name+" "+last_name);
		                    ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FULLNAME, first_name+" "+last_name);
		                    ThisUserConfig.getInstance().putString(ThisUserConfig.EMAIL, email);
		                    Map<String, Object> trackArgMap = new HashMap<String,Object>();
		             	    trackArgMap.put(HopinTracker.FBID, id);
		             	    trackArgMap.put(HopinTracker.FBUSERNAME, username);
		                     HopinTracker.sendEvent("FacebookLogin", "login", "facebook:login:requestdata:success", 1L,trackArgMap);
		                     if(!StringUtils.isBlank(id))
		                     {
		                     	ThisUserConfig.getInstance().putBool(ThisUserConfig.FBLOGGEDIN, true);                	
		                     	String userId = ThisUserConfig.getInstance().getString(ThisUserConfig.USERID);
		                     	if(userId == "")
		                     	{
		                     		//this happens on fb login from tutorial page.
		                     		ProgressHandler.showInfiniteProgressDialoge(underlyingActivity, "Welcome "+first_name+" "+last_name+"!", "Preparing for first run..",null);
		                     		String uuid = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
		                     		SBHttpRequest request = new AddUserRequest(uuid,username,underlyingActivity);		
		                       		SBHttpClient.getInstance().executeRequest(request);
		                     	}
		                     	else
		                     	{
		                     		sendAddFBAndChatInfoToServer(id);
		                     		Platform.getInstance().getHandler().post(new Runnable() {
		                                 @Override
		                                 public void run() {
		                                 	ProgressHandler.dismissDialoge();                            	
		                                 	Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);
		                                 	showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                         	        Platform.getInstance().getContext().startActivity(showSBMapViewActivity);                    	                          	        
		                                    MapListActivityHandler.getInstance().updateUserNameInListView();
		                                    MapListActivityHandler.getInstance().updateUserPicInListView();
		                                    MapListActivityHandler.getInstance().updateThisUserMapOverlay();
		                                    if(!underlyingActivity.equals(MapListActivityHandler.getInstance().getUnderlyingActivity()))
		                         	        	underlyingActivity.finish();
		                                 }
		                             });
		                     	}
		                     }
		            	
		            }
		            else
		            {
		            	Logger.d(TAG, "user is null in facebook");		            	
		            	HopinTracker.sendEvent("FacebookLogin", "login", "facebook:login:requestdata:failure", 1L);
		            }
		        }		        
		    }).executeAsync();		    
		    	   
    }
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {        	
    			if (session.isOpened()) {
    				ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, Session.getActiveSession().getAccessToken());
    	        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, Session.getActiveSession().getExpirationDate().getTime());    	        
    	        	Logger.i(TAG, "login callback rec");
    	        	ProgressHandler.showInfiniteProgressDialoge(underlyingActivity, "Authentication successsful", "Please wait..",null);
    	        	HopinTracker.sendEvent("FacebookLogin", "login", "facebook:login:callbackreceived", 1L);
    	        	requestUserData(session,state);
    			}			
    		}        
      
        }
	
	 class SessionStatusReloginCallback implements Session.StatusCallback {
		   
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if(session.isOpened())
				{
					String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
			    	ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, session.getAccessToken());
		        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, session.getExpirationDate().getTime());
			    	SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID),fbid , ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
					SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);
					ThisUserConfig.getInstance().putBool(ThisUserConfig.FBRELOGINREQUIRED, false);
					Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);
					showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        Platform.getInstance().getContext().startActivity(showSBMapViewActivity);
			        ToastTracker.showToast("Authentication succcessful");			        
			        underlyingActivity.finish();
			        Map<String, Object> trackArgMap = new HashMap<String,Object>();
				    trackArgMap.put(HopinTracker.FBID, fbid);
			        HopinTracker.sendEvent("FacebookLogin", "relogin", "facebook:relogin:authenticationsuccessful", 1L,trackArgMap);
			        
				}
				
			}
			
	    }
	
	
	public void openFacebookPage(String fbid,String username) {
		Intent i;
		   try {
			   underlyingActivity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
		     i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+fbid));
		   } catch (Exception e) {
			   //if fb package not present then shows in browser
		    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+username));
		   }
		   i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		 			| Intent.FLAG_ACTIVITY_NEW_TASK);
		   underlyingActivity.startActivity(i);
		}	
	
	public void inviteFriends(String to)
	{
		Bundle parameters = new Bundle();
		parameters.putString("message", "Take a look at this usefull application");
		
		if(!isSessionValid())
			return;		
		
		WebDialog.RequestsDialogBuilder requestDialog = new WebDialog.RequestsDialogBuilder(underlyingActivity, Session.getActiveSession(),
		                                 parameters);
		if(!StringUtils.isBlank(to))
			requestDialog.setTo(to);

		requestDialog.setOnCompleteListener(new OnCompleteListener() {

		    @Override
		    public void onComplete(Bundle values, FacebookException error) {
		        if (error != null){
		            if (error instanceof FacebookOperationCanceledException){
		                Toast.makeText(underlyingActivity,"Request cancelled",Toast.LENGTH_SHORT).show();
		            }
		            else{
		                Toast.makeText(underlyingActivity,"Network Error",Toast.LENGTH_SHORT).show();
		            }
		        }
		        else{

		            final String requestId = values.getString("request");
		            if (requestId != null) {
		                Toast.makeText(underlyingActivity,"Request sent",Toast.LENGTH_SHORT).show();
		            } 
		            else {
		                Toast.makeText(underlyingActivity,"Request cancelled",Toast.LENGTH_SHORT).show();
		            }
		        }                       
		    }
		}).build().show();		
		
	}

    
}
	


