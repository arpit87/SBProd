package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Activities.InviteFriendsActivity;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.GetFriendListToInviteRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class InviteFriendButtonFragment extends Fragment {
	
	private static final String TAG = "in.co.hopin.Fragments.InviteFriendButtonFragment";

	boolean userIsLoggedIn = false;
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
        userIsLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
	}	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		Logger.i(TAG,"oncreateview InviteFriendButtonFragment");		
		ViewGroup mView = (ViewGroup) inflater.inflate(R.layout.invitefriendsfragment_button, null);
		Button mEmptyListButton = (Button)mView.findViewById(R.id.invitefriendsfragment_fetchbutton);	
		mEmptyListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(userIsLoggedIn)
				{
					InviteFriendsActivity activity = (InviteFriendsActivity) getActivity();
					SBHttpResponseListener listener = activity.getReqListener();
					ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Please wait..", "Fetching friends..",listener);
					SBHttpRequest fetchFriendsReq = new GetFriendListToInviteRequest(0,15, listener);
					SBHttpClient.getInstance().executeRequest(fetchFriendsReq);					
				}
				else
				{
					CommunicationHelper.getInstance().FBLoginDialog_show(getActivity());
				}
				HopinTracker.sendEvent("InviteFriends","ButtonClick","invitefriends:click:getsuggestion",1L);
			}
		});
		 
		return mView;
	}	
	
}	
