package in.co.hopin.Server;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.*;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.Logger;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddUserResponse extends ServerResponseBase{

	
	String user_id;	
	Activity tutorial_activity;//needed to stop activity
	
	private static final String TAG = "in.co.hopin.Server.AddUserResponse";
	public AddUserResponse(HttpResponse response,String jobjStr,Activity tutorial_activity,String api) {
		super(response,jobjStr,api);
		this.tutorial_activity = tutorial_activity;
	}
	
	@Override
	public void process() {
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());
		
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());		
		try {
			body = jobj.getJSONObject("body");
			user_id = body.getString(UserAttributes.USERID);
			ThisUserConfig.getInstance().putString(ThisUserConfig.USERID, user_id);
            // starts the gcm service once the userid is available
            Platform.getInstance().startGCMService();

            //upload contacts
            Platform.getInstance().getHandler().postDelayed(new Runnable() {
                public void run() {
                    uploadContacts();
                }
            }, 10000);

			ThisUserNew.getInstance().setUserID(user_id);	
			//ToastTracker.showToast("Got user_id:"+user_id);
			
			//now we will start map activity
			final Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);	
			showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 Platform.getInstance().getHandler().post(new Runnable() {
	          public void run() { 
	              Platform.getInstance().getContext().startActivity(showSBMapViewActivity);
	              ProgressHandler.dismissDialoge();
	 			 tutorial_activity.finish();
	          }
	        });
			 
			 //this happends on fblogin from tutorial activity direct
			 String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
			 if(fbid != "")
			 {
				 sendAddFBAndChatInfoToServer(fbid);
			 }
			 logSuccess();
		} catch (JSONException e) {
			logServererror();
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server on user add");
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Unable to communicate to server,try again later");
			e.printStackTrace();
		}
		
	}
	
	 private void sendAddFBAndChatInfoToServer(String fbid) {
	    	//this should only be called from fbpostloginlistener to ensure we have fbid
	    	if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"in sendAddFBAndChatInfoToServer");
	    	SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(fbid);
	     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);
			SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), fbid, ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
			SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);			
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
}
