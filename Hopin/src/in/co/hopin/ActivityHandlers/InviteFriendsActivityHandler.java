package in.co.hopin.ActivityHandlers;

import in.co.hopin.Activities.InviteFriendsActivity;
import in.co.hopin.Activities.MyRequestsActivity;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.Users.ThisUserNew;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import in.co.hopin.Adapter.InviteFriendsListViewAdapter;

public class InviteFriendsActivityHandler extends BroadcastReceiver {

	InviteFriendsActivity underlying_activity;

    public InviteFriendsActivityHandler(FragmentActivity underlying_activity) {
        this.underlying_activity = (InviteFriendsActivity) underlying_activity;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction.equals(BroadCastConstants.FRIEND_INVITATION_SENT)) {
        	InviteFriendsListViewAdapter adapter= (InviteFriendsListViewAdapter) underlying_activity.getInviteFriendListFragment().getListAdapter();
        	adapter.notifyDataSetChanged();
        }
         
    }

    
}
