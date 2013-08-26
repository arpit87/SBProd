package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Activities.InviteFriendsActivity;
import in.co.hopin.Adapter.InviteFriendsListViewAdapter;
import in.co.hopin.HelperClasses.ActiveChat;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.Friend;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class InviteFriendListFragment extends ListFragment {
	
	private static final String TAG = "in.co.hopin.Fragments.SBInviteFriendListFragment";
	private ViewGroup mListViewContainer;
	private List<Friend> inviteFriendlist = null;
	InviteFriendsListViewAdapter mAdapter;
	boolean userIsLoggedIn = false;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
		//update listview
        Log.i(TAG,"on create invite friend list view");
        inviteFriendlist = FriendsToInvite.getInstance().getAllFriends();
        userIsLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
        if(!inviteFriendlist.isEmpty())
        {
			mAdapter = new InviteFriendsListViewAdapter(getActivity(), inviteFriendlist);
			setListAdapter(mAdapter);
			Logger.i(TAG,"invitefriendlist size:"+inviteFriendlist.size());
        }
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview chatlistview");		
		mListViewContainer = (ViewGroup) inflater.inflate(R.layout.invitefriendfragment_listview, null);		
		return mListViewContainer;
	}	
	
}	
