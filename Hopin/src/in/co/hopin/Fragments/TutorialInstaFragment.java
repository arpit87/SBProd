package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Activities.Tutorial;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;

public class TutorialInstaFragment extends Fragment {
	
	private static final String TAG = "in.co.hopin.Fragments.TutorialInstaFragment";
	
	Button takeRideButton = null;
	Button offerRideButton = null;
	AutoCompleteTextView source = null;
	AutoCompleteTextView destination = null;
	RadioGroup radio_group_time = null ;
	FragmentActivity tutorialActivity ;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);     
        tutorialActivity = (Tutorial)getActivity();
	}	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		Logger.i(TAG,"oncreateview TutorialMapViewFragment");		
		ViewGroup mView = (ViewGroup) inflater.inflate(R.layout.search_users_insta_frag, null);
		destination = (AutoCompleteTextView) mView.findViewById(R.id.search_user_insta_destination);
		destination.setHint("Just a quick look(non clickable)");
		destination.setEnabled(false);
     	radio_group_time = (RadioGroup)mView.findViewById(R.id.search_user_insta_radio_group);
     	//cancelFindUsers = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_cancelfindusers);
        offerRideButton = (Button)mView.findViewById(R.id.search_usersinsta_btn_offerride);
        takeRideButton = (Button)mView.findViewById(R.id.search_usersinsta_btn_takeride);        
        radio_group_time.check(R.id.search_user_insta_radiobutton_5min);
        
        offerRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:instafrag:click:offerride",1L);
				((Tutorial) tutorialActivity).setFragmentTo(1);
				ToastTracker.showToast("Errr..this is just a quick look!!");
				ToastTracker.showToast("Scroll down and login");
				
			}
		});
        
       takeRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:instafrag:click:takeride",1L);
				((Tutorial) tutorialActivity).setFragmentTo(1);
				ToastTracker.showToast("Errr..this is just a quick look!!");
				ToastTracker.showToast("Scroll down and login");
				
			}
		});
        
       
        
		return mView;
	}	
	
}	
