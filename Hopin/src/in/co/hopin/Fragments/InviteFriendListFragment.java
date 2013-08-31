package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Adapter.InviteFriendsListViewAdapter;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.GetFriendListToInviteRequest;
import in.co.hopin.HttpClient.InviteFriendRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.Friend;
import in.co.hopin.Users.FriendsToInvite;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	GetMoreFriendsListListener getMorelistener = null;
	int count = 0;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
		//update listview
        Log.i(TAG,"on create invite friend list view");
        inviteFriendlist = FriendsToInvite.getInstance().getAllFriends();
        userIsLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
        getMorelistener = new GetMoreFriendsListListener();
        if(!inviteFriendlist.isEmpty())
        {
			mAdapter = new InviteFriendsListViewAdapter(getActivity(), inviteFriendlist);
			setListAdapter(mAdapter);			
			Logger.i(TAG,"invitefriendlist size:"+inviteFriendlist.size());
        }
	}
	
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview chatlistview");		
		mListViewContainer = (ViewGroup) inflater.inflate(R.layout.invitefriendfragment_listview, null);	
		sendMail = (Button)mListViewContainer.findViewById(R.id.inviteFriends_listFrag_viaemail);
		sendFbInvite = (Button)mListViewContainer.findViewById(R.id.inviteFriends_listFrag_viafbl);
		sendMail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");					
				i.putExtra(Intent.EXTRA_SUBJECT, "Check out this android carpool application");
				String text = "Looks useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link);
				i.putExtra(Intent.EXTRA_TEXT, text);
				List<String> emailList = FriendsToInvite.getInstance().getAllSelectedFriendEmails();
				String [] emailArray = emailList.toArray(new String[emailList.size()]);
				i.putExtra(android.content.Intent.EXTRA_EMAIL,emailArray );
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);			
				SBHttpRequest request = new InviteFriendRequest(FriendsToInvite.getInstance().getAllSelectedFriendCommaSeparatedIDs());		
	       		SBHttpClient.getInstance().executeRequest(request);
				HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:sendemailtolist",1L);
			}
		});
		
		sendFbInvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String commaSeparatedFriendIDs = FriendsToInvite.getInstance().getAllSelectedFriendCommaSeparatedIDs();
				FacebookConnector.getInstance(getActivity()).inviteFriends(commaSeparatedFriendIDs);
				SBHttpRequest request = new InviteFriendRequest(FriendsToInvite.getInstance().getAllSelectedFriendCommaSeparatedIDs());		
	       		SBHttpClient.getInstance().executeRequest(request);
				HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:sendfbinvitetolist",1L);			
			}
		});
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
		if((lastInScreen == totalItemCount) && !loadingMoreFriends){
			loadingMoreFriends = true;
			ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Please wait..", "Loading more suggestions", getMorelistener);
			Logger.d(TAG, "scrolled to end,will fetch more friends");
			HopinTracker.sendEvent("InviteFriend", "getmorefriend", "invitefriend:scrolltobottom:more", 1L);
			count = totalItemCount;
			SBHttpRequest fetchFriendsReq = new GetFriendListToInviteRequest(0,count*2, getMorelistener);
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
			if(!hasBeenCancelled)
			{				
				Logger.d(TAG, "got more friends");
				inviteFriendlist = FriendsToInvite.getInstance().getAllFriends();				
				mAdapter.updateContents(inviteFriendlist);
				HopinTracker.sendEvent("InviteFriend", "getmorefriend", "invitefriend:scrolltobottom:more:completed", 1L);
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
