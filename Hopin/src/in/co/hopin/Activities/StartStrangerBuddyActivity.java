package in.co.hopin.Activities;


import in.co.hopin.R;
import in.co.hopin.Adapter.HistoryAdapter;
import in.co.hopin.ChatClient.ChatWindow;
import in.co.hopin.ChatClient.SBChatMessage;
import in.co.hopin.ChatService.Message;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ActiveChat;
import in.co.hopin.HelperClasses.ChatHistory;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisAppInstallation;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SaveReferrerRequest;
import in.co.hopin.HttpClient.UploadContactsRequest;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.provider.HistoryContentProvider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.util.Log;

public class StartStrangerBuddyActivity extends Activity {
	

	private static final String TAG = "in.co.hopin.Activities.StartStrangerBuddyActivity";
	Runnable startMapActivity;
	Intent showSBMapViewActivity;
	//Timer timer;
	AtomicBoolean mapActivityStarted = new AtomicBoolean(false);
	boolean upGradeMsgShown = false;
	AtomicBoolean mInitialized = new AtomicBoolean(false);

    private static Uri mHistoryUri = Uri.parse("content://" + HistoryContentProvider.AUTHORITY + "/db_fetch_only");
    private static String[] columns = new String[]{
            "sourceAddress",
            "destinationAddress",
            "timeOfTravel",
            "dateOfTravel",
            "dailyInstantType",
            "planInstantType",
            "takeOffer",
            "reqDate",
            "radioButtonId",
            "date"
    };
	
	
	
    /** Called when the activity is first created. */
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        //referral
        Intent intent = this.getIntent();
        Uri uri = intent.getData();
        
        if (uri != null) {
        	String referrerString = uri.getPath();
            if(uri.getQueryParameter("utm_source") != null) {    // Use campaign parameters if avaialble.
              EasyTracker.getTracker().setCampaign(referrerString); 
            } else if (uri.getQueryParameter("referrer") != null) {    // Otherwise, try to find a referrer parameter.
              EasyTracker.getTracker().setReferrer(uri.getQueryParameter("referrer"));
            }            
            ThisAppConfig.getInstance().putString(ThisAppConfig.REFERRER_STRING, referrerString);
            SaveReferrerRequest saveReferrerRequest = new SaveReferrerRequest(null);
            SBHttpClient.getInstance().executeRequest(saveReferrerRequest);
          }
        
    }
	
	@Override
	public void onResume()
    {   	
    	super.onResume();        
    	checkNetworkAndDataConnectivity();  
    	if(isLocationProviderEnabled() && SBConnectivity.isOnline() &&!mInitialized.getAndSet(true))    	
    		initialize();    	    		
    }
	
	@Override
	public void onStop()
    {
	  super.onStop();	
      mInitialized.set(false);
    }
	    
    
    private void firstRun() {
		//get user_id from the server	
    	 Logger.i(TAG, "first run") ;
		String uuid = ThisAppInstallation.id(this.getBaseContext());
		ThisAppConfig.getInstance().putString(ThisAppConfig.APPUUID,uuid);
		ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP,true);
		int appOpenCount = ThisAppConfig.getInstance().getInt(ThisAppConfig.APPOPENCOUNT);
		if(appOpenCount == -1)
			appOpenCount = 1;
		else
			appOpenCount = appOpenCount+1;
		ThisAppConfig.getInstance().putInt(ThisAppConfig.APPOPENCOUNT,appOpenCount);
		String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
		ThisAppConfig.getInstance().putString(ThisAppConfig.DEVICEID,deviceId);
		Map<String, Object> trackerMap = new HashMap<String, Object>();
		trackerMap.put(HopinTracker.APPUUID, uuid);
		trackerMap.put(HopinTracker.DEVICEID, deviceId);
		if(appOpenCount ==1 )
			HopinTracker.sendEvent("HopinInstall","Appinstall","application:install",1L,trackerMap);
		else
			HopinTracker.sendEvent("HopinInstall","AppinstallError","application:firstrun:exception",1L,trackerMap);
		//with uuid means first time start
		final Intent show_tutorial = new Intent(this,Tutorial.class);
		show_tutorial.putExtra("uuid", uuid);
		Runnable r = new Runnable() {
	          public void run() {	        		  
	        	  startActivity(show_tutorial);
	        	  finish();
	          }};
		Platform.getInstance().getHandler().postDelayed(r, 2000);	
		//chk if welcome msg not already sent
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.WELCOMENOTESENT))
		{		
			ThisUserConfig.getInstance().putBool(ThisUserConfig.WELCOMENOTESENT, true);	
		Runnable welcomeMessage = new Runnable() {
	          public void run() {	
	        	  String admin_fbid = getResources().getString(R.string.hopin_admin_girl_fbid);
	      		String admin_name = getResources().getString(R.string.hopin_admin_girl_name);
	      		String admin_welcome_message = getResources().getString(R.string.hopin_admin_girl_welcomemessage);	        	  
	        	  int admin_fbid_hash = admin_fbid.hashCode();	        	  
	        	  sendWelcomeNotification(admin_fbid_hash, admin_fbid, admin_name, admin_welcome_message);	        	  	
	          }};
	    Platform.getInstance().getHandler().postDelayed(welcomeMessage, 1*60*1000);    
		}
		
		//upload contacts
		if(!Platform.getInstance().isLoggingEnabled() && !ThisUserConfig.getInstance().getBool(ThisUserConfig.CONTACTUPLOADED))
		{
			ThisUserConfig.getInstance().putBool(ThisUserConfig.CONTACTUPLOADED, true);
			UploadContactTask uploadContactTask = new UploadContactTask();
			uploadContactTask.execute("");
		}
		
		
		       
	}
    
    public void sendWelcomeNotification(int id,String fb_id,String participant_name,String chatMessage) {

    	 NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 Intent chatIntent = new Intent(this,ChatWindow.class);
		 	chatIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		   chatIntent.putExtra(ChatWindow.PARTICIPANT, fb_id);
		   chatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, participant_name);		 
		  	
		 Logger.i(TAG, "Sending notification") ;
		 PendingIntent pintent = PendingIntent.getActivity(this, id, chatIntent, PendingIntent.FLAG_ONE_SHOT);
		 Uri sound_uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		 
		 Notification notif = new Notification(R.drawable.launchernew,"New message from "+participant_name,System.currentTimeMillis());
		 notif.flags |= Notification.FLAG_AUTO_CANCEL;
		 notif.setLatestEventInfo(this, participant_name, chatMessage, pintent);
				
		 Message welcome_message = new Message("", ServerConstants.AppendServerIPToFBID(fb_id), chatMessage, StringUtils.gettodayDateInFormat("hh:mm")
				 								,Message.MSG_TYPE_CHAT, SBChatMessage.RECEIVED,System.currentTimeMillis(),participant_name);
		 
		 ChatHistory.addtoChatHistory(welcome_message);
		 ActiveChat.addChat(fb_id, participant_name, chatMessage);
		 
			notif.ledARGB = 0xff0000ff; // Blue color
			notif.ledOnMS = 1000;
			notif.ledOffMS = 1000;
			notif.defaults |= Notification.DEFAULT_LIGHTS;	
			notif.sound = sound_uri;
      
			notificationManager.notify(id, notif);
			Logger.i(TAG, "notification sent") ;			
			
		    }

   
    private boolean isLocationProviderEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void buildAlertMessageForLocationProvider() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location access is required to run the application, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "clicked yes..");
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void buildAlertMessageForNoNetwork() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Network connection not detected. Please check network connecton and reopen Hopin")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                })           
                ;
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void checkNetworkAndDataConnectivity()
    {
    	 if (!isLocationProviderEnabled()){
             buildAlertMessageForLocationProvider();           
         }
         else  if(!SBConnectivity.isOnline())
         {
         	buildAlertMessageForNoNetwork();	           
         }         
    }

    private void initialize()
    {    	
    	
    	 Logger.i(TAG,"started network listening ");
	        SBLocationManager.getInstance().StartListeningtoNetwork();
         loadHistoryFromDB();        
	       
	
	        //map activity can get started from 3 places, timer task if location found instantly
	        //else this new runnable posted after 3 seconds
	        //else on first run
	        showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);
	        showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	
	        startMapActivity = new Runnable() {
	            public void run() {	            	
	            	Platform.getInstance().getContext().startActivity(showSBMapViewActivity);
	                finish();
	            }};
	
	       // Logger.i(TAG, "FB session valid:"+FacebookConnector.isSessionValid());  
	       // Logger.i(TAG, "FB token:"+ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
	       // Logger.i(TAG, "FB token expires:"+ThisUserConfig.getInstance().getLong(ThisUserConfig.FBACCESSEXPIRES));
	        if("".equals(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID)))
	        {
	        	firstRun();
	            
	        }
	        else if (ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN) && !FacebookConnector.getInstance(this).isSessionValid())
	        {	
	        	// if user had atleast once logged in then we want to enter here on expiry of session
	        	Logger.d(TAG, "FB session is not valid");
	        	ThisUserConfig.getInstance().putBool(ThisUserConfig.FBRELOGINREQUIRED,true);
	        	Intent fbReloginIntent = new Intent(Platform.getInstance().getContext(),ReloginActivity.class);
	        	fbReloginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 			Platform.getInstance().getContext().startActivity(fbReloginIntent);
	 			finish();
	        }
	        else
	        {
	            ThisUserNew.getInstance().setUserID(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID));
	            //timer = new Timer();
	            //timer.scheduleAtFixedRate(new GetNetworkLocationFixTask(), 500, 500);
	            if(!mapActivityStarted.getAndSet(true))
	        	  {
	            	Platform.getInstance().getHandler().postDelayed(startMapActivity,2000);
	        	  }
	    		
	            //send upgrade msg
	            if(isVersionUpgraded() && !ThisUserConfig.getInstance().getBool(ThisUserConfig.UPGRADENOTESENT))
		        {
	            	ThisUserConfig.getInstance().putBool(ThisUserConfig.WELCOMENOTESENT, true);
		        	Logger.d(TAG, "app upgraded, sending upgrade msg");
		        	Runnable upgradeMessage = new Runnable() {
		  	          public void run() {	   
		  	        	String admin_fbid = getResources().getString(R.string.hopin_admin_girl_fbid);
		  	      		String admin_name = getResources().getString(R.string.hopin_admin_girl_name);
		  	      		String admin_upgrade_message = getResources().getString(R.string.hopin_admin_girl_upgrademessage);	        	  
		  	        	  int admin_fbid_hash = admin_fbid.hashCode();	    
		  	        	String username = ThisUserConfig.getInstance().getString(ThisUserConfig.USERNAME);
		  	        	username = username.split(" ")[0];
		  	        	admin_upgrade_message = "Hi "+ username+", "+admin_upgrade_message;
		  	        	  sendWelcomeNotification(admin_fbid_hash, admin_fbid, admin_name, admin_upgrade_message);
			  	          }};
			  	    Platform.getInstance().getHandler().postDelayed(upgradeMessage, 1*60*1000);    
			  	}	        
	        }  
	        
    }  
  
    
    private boolean isVersionUpgraded()
    {
    	//GCM upgrades this version so we are not doing here
    	int registeredVersion = ThisAppConfig.getInstance().getInt(ThisAppConfig.PROPERTY_APP_VERSION);
    	int currentAppVersion = Platform.getInstance().getThisAppVersion();
    	if(currentAppVersion > registeredVersion)
    		return true;
    	else 
    		return false; 
    }
    
    public void onPause()
    {
    	super.onPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        HopinTracker.sendView("LogoScreen");
        //EasyTracker.getInstance().activityStart(this);
    } 
    
   /* private class GetNetworkLocationFixTask extends TimerTask
    { 
    	private int counter = 0;
         public void run() 
         {
        	 counter++;
        	 if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "timer task counter:"+counter);
        	 SBGeoPoint currGeo;
        	 
        	 //check if it got location by singleUpdateintent which works for froyo+
        	 currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
        	 
        	 if(currGeo == null)
        	 {
        		 Location lastBestLoc = SBLocationManager.getInstance().getLastXSecBestLocation(10*60);  
        		 if(lastBestLoc!=null)
        			 currGeo = new SBGeoPoint((int)(lastBestLoc.getLatitude()*1e6), (int)(lastBestLoc.getLongitude()*1e6));
        	 }
        	 
        	 if(currGeo != null || counter>5)
        	 {       
        		 timer.cancel();
       		  	 timer.purge();
        		 //ToastTracker.showToast("starting activity in counter:"+counter);  
        		 if(currGeo != null)
        			 ThisUserNew.getInstance().setCurrentGeoPoint(currGeo);        		 
        		 if(!mapActivityStarted.getAndSet(true))
	        	  {
        			 Platform.getInstance().getHandler().post(startMapActivity);
	        	  }
        	 }
          }
     }*/

    private void loadHistoryFromDB() {
        LinkedList<HistoryAdapter.HistoryItem> historyItemList = null;
        if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Fetching searches");
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(mHistoryUri, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Empty result");
        } else {
            LinkedList<HistoryAdapter.HistoryItem> historyItems = new LinkedList<HistoryAdapter.HistoryItem>();
            if (cursor.moveToFirst()) {
                do {
                    HistoryAdapter.HistoryItem historyItem = new HistoryAdapter.HistoryItem(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4),cursor.getInt(5),
                            cursor.getInt(6), cursor.getString(7),cursor.getInt(8));
                    historyItems.add(historyItem);
                } while (cursor.moveToNext());

            }
            if(historyItems.size()>0)
                historyItemList = historyItems;
        }

        if (cursor!= null) {
            cursor.close();
        }

        if (historyItemList == null) {
            historyItemList = new LinkedList<HistoryAdapter.HistoryItem>();
        }

        ThisUserNew.getInstance().setHistoryItemList(historyItemList);
    }
    
    public void uploadContacts() {
        Log.d(TAG, "Uploading Contacts");

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        ContentResolver contentResolver = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        Logger.d(TAG, "Total Contacts:" + cursor.getCount());
        // Loop for every contact in the phone
        //update in batches of 50 contacts
        JSONArray jsonArray = new JSONArray();
        int count = 0;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (count == 50) {
                    count = 0;
                    UploadContactsRequest uploadContactsRequest = new UploadContactsRequest(jsonArray);
                    SBHttpClient.getInstance().executeRequest(uploadContactsRequest);
                    jsonArray = new JSONArray();
                }

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));

                // Query and loop for every email of the contact
                Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                if (emailCursor != null && emailCursor.getCount() > 0) {
                    count++;
                    JSONObject jsonObject = new JSONObject();
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    try {
                        jsonObject.put("Name", name);
                        emailCursor.moveToNext();
                        String email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        jsonObject.put("Email", email);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        //do nothing
                    }
                }

                if (emailCursor != null) {
                    emailCursor.close();
                }

            }
        }

        cursor.close();

        if (count != 0) {
            UploadContactsRequest uploadContactsRequest = new UploadContactsRequest(jsonArray);
            SBHttpClient.getInstance().executeRequest(uploadContactsRequest);
        }
    }
    
    private class UploadContactTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... urls) {
        	uploadContacts();
			return 0l;
            
        }

        protected void onProgressUpdate(Integer... progress) {
            
        }

        protected void onPostExecute(Long result) {
            
        }
    }

}
