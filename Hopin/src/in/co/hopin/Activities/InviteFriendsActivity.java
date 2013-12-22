package in.co.hopin.Activities;

import java.util.List;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.InviteFriendButtonFragment;
import in.co.hopin.Fragments.InviteFriendListFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.GetFriendListToInviteRequest;
import in.co.hopin.HttpClient.InviteFriendRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class InviteFriendsActivity extends FragmentActivity {
	
	FragmentManager fm = this.getSupportFragmentManager();	
	GetFriendsListListener mGetFriendsListener = null;
	
	ImageView shareIcon;
	Button sendFbInvite;

	InviteFriendListFragment mInviteFriendListFragment = null;
	//InviteFriendsActivityHandler inviteHandler = null;
	

	@Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("InviteFriends");
	        HopinTracker.sendEvent("InviteFriends","ScreenOpen","invitefriends:open",1L);
	       
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
		 setContentView(R.layout.invitefriendslist_layout);		
		 sendFbInvite = (Button) findViewById(R.id.invitefriendslist_layout_viafbl);		 
		 shareIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_shareicon);		
		 shareIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPopupMenu(v);				
			}
		});
						
			sendFbInvite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String commaSeparatedFriendIDs = FriendsToInvite.getInstance().getAllSelectedFriendCommaSeparatedIDs();
					if(StringUtils.isBlank(commaSeparatedFriendIDs))
					{
						ToastTracker.showToast("Select atleast 1 friend");
						return;
					}
					FacebookConnector.getInstance(InviteFriendsActivity.this).inviteFriends(commaSeparatedFriendIDs);
					SBHttpRequest request = new InviteFriendRequest(FriendsToInvite.getInstance().getAllSelectedFriendCommaSeparatedIDs());		
		       		SBHttpClient.getInstance().executeRequest(request);
					HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:sendfbinvitetolist",1L);			
				}
			});
		 
			shareIcon.post(new Runnable() {
				
				@Override
				public void run() {
					showPopupMenu(shareIcon);					
				}
			});
	 }
	 
	 
	 
	 private void showPopupMenu(View v)
	 { 
	 	LayoutInflater inflater = getLayoutInflater();
	 	View layout = inflater.inflate(R.layout.invitefriends_share_popup, null);	
	 	PopupWindow popUpMenu = new PopupWindow(layout,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);	
	 	popUpMenu.setFocusable(true);
	 	popUpMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	 	popUpMenu.showAsDropDown(v);
	 	Button wtsapp = (Button) layout.findViewById(R.id.invitefriends_share_popup_wtsapp);
	 	final String text = "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link);
	 	
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
			    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:wtsappicon",1L);
	 		}
	 	});
	 	
	 	Button wechat = (Button) layout.findViewById(R.id.invitefriends_share_popup_wechat);
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
			    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:wechaticon",1L);
	 		}
	 		
	 	});
	 	
	 	Button line = (Button) layout.findViewById(R.id.invitefriends_share_popup_line);
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
			    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:lineicon",1L);
	 		
	 		}
	 	});
	 	
	 	Button viber = (Button) layout.findViewById(R.id.invitefriends_share_popup_viber);
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
			    HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:vibericon",1L);
	 		
	 		}
	 	});
	 	
	 	
	 }	
	 
	 @Override
	    public void onResume(){
	        super.onResume();
	       // registerReceiver(inviteHandler, new IntentFilter(BroadCastConstants.FRIEND_INVITATION_SENT));
	        if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
	        	showInviteFriendButtonLayout();
	        else if(!FriendsToInvite.getInstance().getAllFriends().isEmpty())
	        {
	        	showInviteFriendListLayout();
	        }	        
	        else
			{			
				ProgressHandler.showInfiniteProgressDialoge(this, "Please wait..", "Fetching friends..",getReqListener());
				SBHttpRequest fetchFriendsReq = new GetFriendListToInviteRequest(0,15, getReqListener());
				SBHttpClient.getInstance().executeRequest(fetchFriendsReq);					
			}
	 }
	 
	 public void showInviteFriendListLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            mInviteFriendListFragment = new InviteFriendListFragment();
	            fragTrans.replace(R.id.invitefriendslist_layout_content,mInviteFriendListFragment );	            
	            fragTrans.commitAllowingStateLoss();	
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
	    
	}

	
}
