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

public class OtheruserAboutMeFragment extends Fragment{
	
	 private static final String TAG = "in.co.hopin.Fragments.OtheruserAboutMeFragment";
	 UserFBInfo mNearbyUserFbInfo ;
	 TextView worksAtTextView;
	 TextView homeTownTextView;
	 TextView educationTextView;
	 
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
	                R.layout.otheruser_aboutme, container, false);
	        
	        worksAtTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_worksat);
	        homeTownTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_hometown);
	        educationTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_education);
	        Logger.d(TAG, "other user profile"+mNearbyUserFbInfo.getJsonObj().toString());
	        worksAtTextView.setText(mNearbyUserFbInfo.getWorksAt());
	        homeTownTextView.setText(mNearbyUserFbInfo.getHometown());
	        educationTextView.setText(mNearbyUserFbInfo.getStudiedAt());
	        return aboutMeView;
	    }

}
