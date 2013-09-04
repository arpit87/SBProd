package in.co.hopin.Adapter;

import in.co.hopin.R;
import in.co.hopin.Activities.Tutorial;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.InviteFriendRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Users.Friend;
import in.co.hopin.Util.HopinTracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InviteFriendsListViewAdapter extends BaseAdapter{

	List<Friend> mFriendsList = new ArrayList<Friend>();	
	Activity underLyingActivity;
	private static LayoutInflater inflater=null;
	public InviteFriendsListViewAdapter(Activity activity,List<Friend> friendList)
	{
		underLyingActivity = activity;
		mFriendsList = friendList;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFriendsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void clear()
	{		
		mFriendsList.clear();
		notifyDataSetChanged();
	}
	
	public void updateContents(List<Friend> friendList)
	{
		mFriendsList.clear();
		mFriendsList.addAll(friendList);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Friend thisFriend = (Friend) mFriendsList.get(position);
		View thisFriendView=convertView;
        if(thisFriendView == null)     		
        	thisFriendView = inflater.inflate(R.layout.invitefriends_list_row, null);
        final boolean hasBeenInvited = thisFriend.hasBeenInvited();
        boolean hasInstalledHopin = thisFriend.hasInstalledHopin();
        
        ImageView userImageView = (ImageView)thisFriendView.findViewById(R.id.invitefriend_list_row_image);
                
        TextView userName = (TextView)thisFriendView.findViewById(R.id.invitefriend_list_row_invitefriendname);
        userName.setText(thisFriend.getName());
        final TextView inviteTap = (TextView)thisFriendView.findViewById(R.id.invitefriend_list_row_taptoinvite);
        
        SBImageLoader.getInstance().displayImageElseStub(thisFriend.getImageURL(), userImageView, R.drawable.userpicicon);
        if(thisFriend.isSelected())
        {
        	inviteTap.setText("   Selected"); 
        	inviteTap.setTypeface(null,Typeface.BOLD);
			inviteTap.setCompoundDrawablesWithIntrinsicBounds(null, null, underLyingActivity.getResources().getDrawable(R.drawable.checkmark_green), null);
        }       
        else if(hasInstalledHopin)
        	inviteTap.setText("Hopin installed");
        else
        {
        	inviteTap.setText("Tap to select");    
        	
	        inviteTap.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					if(thisFriend.isSelected())
			        {
						thisFriend.setSelected(false);
			        	inviteTap.setText("Tap to select");	
			        	inviteTap.setTypeface(null,Typeface.NORMAL);
			        	HopinTracker.sendEvent("InviteFriends","ListClick","invitefriends:listclick:deselectfriend",1L);
			        	inviteTap.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			        }  
					else
					{
						inviteTap.setText("   Selected");
						thisFriend.setSelected(true);
						inviteTap.setTypeface(null,Typeface.BOLD);
						inviteTap.setCompoundDrawablesWithIntrinsicBounds(null, null, underLyingActivity.getResources().getDrawable(R.drawable.checkmark_green), null);
						//SBHttpRequest request = new InviteFriendRequest(thisFriend.getFb_id());		
			       		//SBHttpClient.getInstance().executeRequest(request);			       		
			       		HopinTracker.sendEvent("InviteFriends","ListClick","invitefriends:listclick:selectfriend",1L);
					}
					}				
			});
        }
        
		return thisFriendView;
	}	
	

}
