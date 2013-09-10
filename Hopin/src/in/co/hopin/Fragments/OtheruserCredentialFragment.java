package in.co.hopin.Fragments;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.hopin.R;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OtheruserCredentialFragment extends Fragment{
	
	 private static final String TAG = "in.co.hopin.Fragments.OtheruserCredentialFragment";
	 UserFBInfo mNearbyUserFbInfo ;
	 TextView emailverificationTextView;
	 TextView friendsNumTextView;
	 TextView mutualFriendsNumTextView;
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);
	        Bundle data = getArguments();
	        String fb_info = data.getString("fb_info");
	        JSONObject fbJson = null;
			try {
				fbJson = new JSONObject(fb_info);
			} catch (JSONException e) {
				// in case fbinfo is malformed
				try {
					fbJson = new JSONObject("{\"fb_info_available\":0}");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
	        mNearbyUserFbInfo = new UserFBInfo(fbJson);
	 }
	 	 
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup aboutMeView = (ViewGroup) inflater.inflate(
	                R.layout.otheruser_credentials, container, false);
	        
	        emailverificationTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_credentials_emailstatus);
	        friendsNumTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_credentials_friendnum);
	        mutualFriendsNumTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_credentials_mutualfriendsnum);
	        Logger.d(TAG, "other user profile"+mNearbyUserFbInfo.getJsonObj().toString());
	        emailverificationTextView.setText(mNearbyUserFbInfo.getEmailVerificationStatus());
	        friendsNumTextView.setText(Integer.toString(mNearbyUserFbInfo.getNumberOfFriends()));
	        mutualFriendsNumTextView.setText(Integer.toString(mNearbyUserFbInfo.getNumberOfMutualFriends()));
	        return aboutMeView;
	    }

}
