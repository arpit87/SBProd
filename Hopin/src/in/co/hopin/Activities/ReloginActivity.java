package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.Util.HopinTracker;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ReloginActivity extends FragmentActivity {
	
	FacebookConnector fbconnect;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refblogin_layout);
        
        Button faceBookLoginbutton = (Button) findViewById(R.id.refblogin_signInViaFacebook);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.refblogin_progressbar);
        faceBookLoginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                HopinTracker.sendEvent("FBRelogin", "ButtonClick", "facebook:click:relogin", 1L);
                progressBar.setVisibility(View.VISIBLE);
				Toast.makeText(ReloginActivity.this, "Logging...please wait..", Toast.LENGTH_SHORT).show();
				fbconnect = FacebookConnector.getInstance(ReloginActivity.this);
				fbconnect.reloginToFB();
			}
		});   
	}
	
	@Override
	public void onResume() {
		super.onResume();		
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(ReloginActivity.this, requestCode, resultCode, data);
    }

	 @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("Relogin");
	        //EasyTracker.getInstance().activityStart(this);
	    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }

}
