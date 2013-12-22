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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TutorialPlanFragment extends Fragment {
	
	private static final String TAG = "in.co.hopin.Fragments.TutorialInstaFragment";
	
	RadioGroup radio_group_daily_onetime;	
	RadioButton enterDateButton;
	ToggleButton am_pm_toggle;
	TextView timeView;
	SeekBar timeSeekbar;
	Button takeRideButton = null;
	Button offerRideButton = null;
	AutoCompleteTextView source = null;
	AutoCompleteTextView destination = null;
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
		ViewGroup mView = (ViewGroup) inflater.inflate(R.layout.search_users_plan_fragment, null);
		
		radio_group_daily_onetime = (RadioGroup)mView.findViewById(R.id.search_user_plan_radio_group);
    	radio_group_daily_onetime.check(R.id.search_user_plan_radiobutton_daily);
    	enterDateButton = (RadioButton) mView.findViewById(R.id.search_user_plan_radiobutton_enterdate);
    	source = (AutoCompleteTextView) mView.findViewById(R.id.search_user_plan_source);
    	source.setHint("Just a quick look(non clickable)");
    	source.setEnabled(false);
		destination = (AutoCompleteTextView) mView.findViewById(R.id.search_user_plan_destination);
		destination.setHint("Just a quick look(non clickable)");
		destination.setEnabled(false);     	
     	//cancelFindUsers = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_cancelfindusers);
		takeRideButton = (Button)mView.findViewById(R.id.search_user_plan_takeride);
        offerRideButton = (Button)mView.findViewById(R.id.search_user_plan_offerride);       
       
        
        offerRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:planfrag:click:offerride",1L);
				((Tutorial) tutorialActivity).setFragmentTo(1);
				ToastTracker.showToast("Errr..this is just a quick look!!");
				ToastTracker.showToast("Scroll down and login");
				
			}
		});
        
        takeRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:planfrag:click:takeride",1L);
				((Tutorial) tutorialActivity).setFragmentTo(1);
				ToastTracker.showToast("Errr..this is just a quick look!!");
				ToastTracker.showToast("Scroll down and login");
				
			}
		});
        
		return mView;
	}	
	
}	
