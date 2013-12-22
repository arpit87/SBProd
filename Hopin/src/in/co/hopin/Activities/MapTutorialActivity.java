package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.Util.HopinTracker;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MapTutorialActivity extends Activity{
	
	CheckBox dontShowTutBox = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);       
        setContentView(R.layout.map_tutorial_transparent_layout); 
        ThisAppConfig.getInstance().putBool(ThisAppConfig.MAPTUTORIALSHOWN,true);
	}
	
	public void onScreenTap(View V)
	 {		
		finish();
	 }
	
	@Override
	 public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("MapTutorialView");
	    }
	
	

}
