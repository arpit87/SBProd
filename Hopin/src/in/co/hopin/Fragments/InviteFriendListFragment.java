package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Adapter.InviteFriendsListViewAdapter;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.GetFriendListToInviteRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;

public class InviteFriendListFragment extends ListFragment implements android.widget.AbsListView.OnScrollListener{
	
	private static final String TAG = "in.co.hopin.Fragments.InviteFriendListFragment";
	private ViewGroup mListViewContainer;
	private List<Friend> inviteFriendlist = null;
	Button sendMail = null;
	Button sendFbInvite = null;
	InviteFriendsListViewAdapter mAdapter;
	boolean userIsLoggedIn = false;
	boolean loadingMoreFriends = false;
	boolean allFriendsLoaded = false;
	GetMoreFriendsListListener getMorelistener = new GetMoreFriendsListListener();	
	int prevSize = 0;
	int newSize = 0;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
		//update listview
        Log.i(TAG,"on create invite friend list view");
        inviteFriendlist = FriendsToInvite.getInstance().getAllFriends();
        userIsLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);      
        if(!inviteFriendlist.isEmpty())
        {
			mAdapter = new InviteFriendsListViewAdapter(getActivity(),inviteFriendlist);
			setListAdapter(mAdapter);			
			Logger.i(TAG,"invitefriendlist size:"+inviteFriendlist.size());
        }
	}
	
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview chatlistview");		
		mListViewContainer = (ViewGroup) inflater.inflate(R.layout.invitefriendfragment_listview, null);
		
		return mListViewContainer;
	}

	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        getListView().setOnScrollListener(this);
	 }

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;		
		if((lastInScreen == totalItemCount) && !loadingMoreFriends && !allFriendsLoaded){
			loadingMoreFriends = true;
			ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Please wait..", "Loading more suggestions", getMorelistener);
			Logger.d(TAG, "scrolled to end,will fetch more friends");
			HopinTracker.sendEvent("InviteFriend", "getmorefriend", "invitefriend:scrolltobottom:more", 1L);			
			SBHttpRequest fetchFriendsReq = new GetFriendListToInviteRequest(totalItemCount,totalItemCount*2, getMorelistener);
			SBHttpClient.getInstance().executeRequest(fetchFriendsReq);
		}
		
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}	
	
	class GetMoreFriendsListListener extends SBHttpResponseListener
	{
		@Override
		public void onComplete(String response) {
			loadingMoreFriends = false;
			newSize = FriendsToInvite.getInstance().getAllFriends().size();
			Logger.d(TAG, "prevsize:"+prevSize+",newsize:"+newSize);
			if(!hasBeenCancelled)
			{				
				Logger.d(TAG, "got more friends");				
				inviteFriendlist = FriendsToInvite.getInstance().getAllFriends();	
				if(prevSize == newSize)
					allFriendsLoaded = true;
				else				
				{
					mAdapter.updateContents(inviteFriendlist);
					prevSize = newSize;
				}
				HopinTracker.sendEvent("InviteFriends", "getmorefriend", "invitefriend:scrolltobottom:more:completed", 1L);
				ProgressHandler.dismissDialoge();
			}
		}
		
		@Override
		public void onCancel()
		{
			super.onCancel();
			loadingMoreFriends = false;
			Logger.d(TAG, "cancelled to get friends");
			HopinTracker.sendEvent("InviteFriend", "getmorefriend", "invitefriend:scrolltobottom:more:cancelled", 1L);
		}
		
	}
	
	
}	
