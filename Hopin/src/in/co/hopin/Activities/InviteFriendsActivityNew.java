package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InviteFriendsActivityNew extends FragmentActivity {
	
		
	@Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("InviteFriendsNew");
	        HopinTracker.sendEvent("InviteFriendsNew","ScreenOpen","invitefriendsnew:open",1L);
	       
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        //unregisterReceiver(inviteHandler);
	        //EasyTracker.getInstance().activityStop(this);
	    }
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(null);
		 setContentView(R.layout.invitefriendsnew_layout);	
		 String referrerid = ThisUserNew.getInstance().getUserID();
		 String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID); // this may be empty		 
		 if(!StringUtils.isEmpty(fbid))
			 referrerid = fbid;
		 
		 Button wtsapp = (Button) findViewById(R.id.invitefriendsnew_wtsapp);
		 	final String text = "Take a look at this application: " + '\n' + StringUtils.getGooglePlayReferrerString(referrerid);
		 	
		 	wtsapp.setOnClickListener(new OnClickListener() {		
		 		@Override
		 		public void onClick(View v) {	 
		 			Intent shareIntent = new Intent(Intent.ACTION_SEND);
		 		 	shareIntent.setType("text/plain");
		 			shareIntent.setPackage("com.whatsapp");
				    if (shareIntent != null) {
				    	shareIntent.putExtra(Intent.EXTRA_TEXT, text);//
				        startActivity(Intent.createChooser(shareIntent, "Share with"));
				    } else {
				        ToastTracker.showToast("Whatsapp not installed");
				    }
				    HopinTracker.sendEvent("InviteFriendsNew","ButtonClick","invitefriendsnew:click:wtsappicon",1L);
		 		}
		 	});
		 	
		 	Button wechat = (Button) findViewById(R.id.invitefriendsnew_wechat);
		 	wechat.setOnClickListener(new OnClickListener() {		
		 		@Override
		 		public void onClick(View v) {
		 			Intent shareIntent = new Intent(Intent.ACTION_SEND);
		 		 	shareIntent.setType("text/plain");
		 			shareIntent.setPackage("com.tencent.mm");
				    if (shareIntent != null) {
				    	shareIntent.putExtra(Intent.EXTRA_TEXT, text);//
				        startActivity(Intent.createChooser(shareIntent, "Share with"));
				    } else {
				        ToastTracker.showToast("WeChat not installed");
				    }
				    HopinTracker.sendEvent("InviteFriendsNew","ButtonClick","invitefriendsnew:click:wechaticon",1L);
		 		}
		 		
		 	});
		 	
		 	Button line = (Button) findViewById(R.id.invitefriendsnew_line);
		 	line.setOnClickListener(new OnClickListener() {		
		 		@Override
		 		public void onClick(View v) {
		 			Intent shareIntent = new Intent(Intent.ACTION_SEND);
		 		 	shareIntent.setType("text/plain");
		 			shareIntent.setPackage("jp.naver.line.android");
				    if (shareIntent != null) {
				    	shareIntent.putExtra(Intent.EXTRA_TEXT, text);//
				        startActivity(Intent.createChooser(shareIntent, "Share with"));
				    } else {
				        ToastTracker.showToast("Line messenger not installed");
				    }
				    HopinTracker.sendEvent("InviteFriendsNew","ButtonClick","invitefriendsnew:click:lineicon",1L);
		 		
		 		}
		 	});
		 	
		 	Button viber = (Button) findViewById(R.id.invitefriendsnew_viber);
		 	viber.setOnClickListener(new OnClickListener() {		
		 		@Override
		 		public void onClick(View v) {
		 			Intent shareIntent = new Intent(Intent.ACTION_SEND);
		 		 	shareIntent.setType("text/plain");
		 			shareIntent.setPackage("com.viber.voip");
				    if (shareIntent != null) {
				    	shareIntent.putExtra(Intent.EXTRA_TEXT, text);//
				        startActivity(Intent.createChooser(shareIntent, "Share with"));
				    } else {
				        ToastTracker.showToast("Viber not installed");
				    }
				    HopinTracker.sendEvent("InviteFriendsNew","ButtonClick","invitefriendsnew:click:vibericon",1L);
		 		
		 		}
		 	});
				
		 	Button sendFbInvite = (Button) findViewById(R.id.invitefriendsnew_facebook);			
			sendFbInvite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))					 
						CommunicationHelper.getInstance().FBLoginDialog_show(InviteFriendsActivityNew.this);					
					else						
						FacebookConnector.getInstance(InviteFriendsActivityNew.this).inviteFriends("");
								
				}
			});
			
			Button sendMail = (Button) findViewById(R.id.invitefriendsnew_email);
			sendMail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");					
					i.putExtra(Intent.EXTRA_SUBJECT, "Check out this android carpool application");
					String text = "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link);
					i.putExtra(Intent.EXTRA_TEXT, text);					
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);					
					HopinTracker.sendEvent("InviteFriendsNew","ButtonClick","invitefriendsnew:click:sendemail",1L);
				}
			});
		 
			
	 }
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
	
	
	
}
