package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.InviteFriendsActivityHandler;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.InviteFriendButtonFragment;
import in.co.hopin.Fragments.InviteFriendListFragment;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

public class InviteFriendsActivity extends FragmentActivity {
	
	FragmentManager fm = this.getSupportFragmentManager();	
	GetFriendsListListener mGetFriendsListener = null;
	ImageView smsIcon;
	ImageView emailIcon;
	ImageView fbIcon;
	ImageView wtsappIcon;
	InviteFriendListFragment mInviteFriendListFragment = null;
	InviteFriendsActivityHandler inviteHandler = null;
	

	@Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("InviteFriends");
	        HopinTracker.sendEvent("InviteFriends","ScreenOpen","invitefriends:open",1L);	        
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        unregisterReceiver(inviteHandler);
	        //EasyTracker.getInstance().activityStop(this);
	    }
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(null);
		 setContentView(R.layout.invitefriendslist_layout);	
		 smsIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_smsicon);
		 emailIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_emailicon);
		 fbIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_fb_icon);
		 wtsappIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_wtsapp_icon);
		 inviteHandler = new InviteFriendsActivityHandler(this);		
		 smsIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("smsto:");
			    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			    String text = "Check out this ride share application, looks useful: " + '\n' + getResources().getString(R.string.http_tiny_app_link);
			    intent.putExtra("sms_body", text);  
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:smsicon",1L);
			}
		});
		 
		 emailIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");					
					i.putExtra(Intent.EXTRA_SUBJECT, "Check out this android carpool application");
					String text = "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link);
					i.putExtra(Intent.EXTRA_TEXT, text);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
					HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:emailicon",1L);
				}
			});
		 
		 fbIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FacebookConnector.getInstance(InviteFriendsActivity.this).openFacebookPage("", "");
					HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:facebookicon",1L);
					
				}
			});
		 
		 wtsappIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//sendIntent.setAction(Intent.ACTION_SEND);
			    	 //sendIntent.putExtra(Intent.EXTRA_TEXT, "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link));
			    	 //sendIntent.setType("text/plain");
					 Intent waIntent = new Intent(Intent.ACTION_SEND);
					    waIntent.setType("text/plain");
					    String text = "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link);
					    waIntent.setPackage("com.whatsapp");
					    if (waIntent != null) {
					        waIntent.putExtra(Intent.EXTRA_TEXT, text);//
					        startActivity(Intent.createChooser(waIntent, "Share with"));
					    } else {
					        ToastTracker.showToast("Wtsapp not installed");
					    }
					    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:wtsappicon",1L);
					
				}
			});
		 
	 }
	 
	 @Override
	    public void onResume(){
	        super.onResume();
	        registerReceiver(inviteHandler, new IntentFilter(BroadCastConstants.FRIEND_INVITATION_SENT));
	        if(FriendsToInvite.getInstance().getAllFriends().isEmpty())
	        	showInviteFriendButtonLayout();
	        else
	        	showInviteFriendListLayout();
	 }
	 
	 public void showInviteFriendListLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            mInviteFriendListFragment = new InviteFriendListFragment();
	            fragTrans.replace(R.id.invitefriendslist_layout_content,mInviteFriendListFragment );	            
	            fragTrans.commit();	
	            HopinTracker.sendEvent("InviteFriends","ScreenOpen","invitefriends:open:list",1L);
	        }
	    }
	 
	 public void showInviteFriendButtonLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            fragTrans.replace(R.id.invitefriendslist_layout_content, new InviteFriendButtonFragment());	            
	            fragTrans.commit();	   
	            HopinTracker.sendEvent("InviteFriends","ScreenOpen","invitefriends:open:getsuggestion",1L);
	        }
	    }
		
	@Override
	public void onPause(){
    	super.onPause();
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
	
	
	public GetFriendsListListener getReqListener()
	{
		if(mGetFriendsListener == null)
			mGetFriendsListener = new GetFriendsListListener();
		return mGetFriendsListener;
	}
	
	  public InviteFriendListFragment getInviteFriendListFragment() {
			return mInviteFriendListFragment;
		}
	
	class GetFriendsListListener extends SBHttpResponseListener
	{

		@Override
		public void onComplete(String response) {
			if(!hasBeenCancelled)
			{
				showInviteFriendListLayout();				
			}
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
	}

	
}
