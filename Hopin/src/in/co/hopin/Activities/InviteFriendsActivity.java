package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Fragments.InviteFriendButtonFragment;
import in.co.hopin.Fragments.InviteFriendListFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class InviteFriendsActivity extends FragmentActivity {
	
	FragmentManager fm = this.getSupportFragmentManager();	
	GetFriendsListListener mGetFriendsListener = null;
	ImageView smsIcon;
	ImageView emailIcon;
	ImageView fbIcon;
	ImageView wtsappIcon;
	InviteFriendListFragment mInviteFriendListFragment = null;
	
	

	@Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("InviteFriends");
	        HopinTracker.sendEvent("InviteFriends","ScreenOpen","invitefriends:open",1L);	        
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        //EasyTracker.getInstance().activityStop(this);
	    }
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.invitefriendslist_layout);	
		 smsIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_smsicon);
		 emailIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_fb_icon);
		 fbIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_emailicon);
		 wtsappIcon = (ImageView) findViewById(R.id.invitefriendslist_layout_wtsapp_icon);
		 smsIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		 
		 emailIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		 
		 fbIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		 
		 wtsappIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
		 
	 }
	 
	 @Override
	    public void onResume(){
	        super.onResume();
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
	        }
	    }
	 
	 public void showInviteFriendButtonLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            fragTrans.replace(R.id.invitefriendslist_layout_content, new InviteFriendButtonFragment());	            
	            fragTrans.commit();	           
	        }
	    }
		
	@Override
	public void onPause(){
    	super.onPause();    	
        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);    	
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
				showInviteFriendListLayout();			
		}
		
	}
	


}
