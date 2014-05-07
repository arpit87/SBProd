package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.HopinTracker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LikeUsRateUsActivity extends FragmentActivity {
	
	FacebookConnector fbconnect;
	private static final String TAG = "in.co.hopin.Activity.LikeUsRateUsActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_rateus_layout);
        boolean showprompt = getIntent().getBooleanExtra("showprompt", false);
        if(showprompt)
        	buildRateAlertMessage();
        
        ImageView faceBookLikebutton = (ImageView) findViewById(R.id.likeusrateus_fbbutton);
        ImageView GPlayRatebutton = (ImageView) findViewById(R.id.likeusrateus_googleplaybutton);
        
        faceBookLikebutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                HopinTracker.sendEvent("LikeUsRateUs", "ButtonClick", "likeusrateus:click:fbpageopen", 1L);                
				Toast.makeText(LikeUsRateUsActivity.this, "Please wait..", Toast.LENGTH_SHORT).show();
				fbconnect = FacebookConnector.getInstance(LikeUsRateUsActivity.this);
				String fbpagename = getResources().getString(R.string.fb_page_name);
				String fbpageid = getResources().getString(R.string.fb_page_id);
				fbconnect.openFacebookPage(fbpageid, fbpagename);
			}
		}); 
        
        GPlayRatebutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 Intent openPlay = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.co.hopin"));
           	 openPlay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           	 Platform.getInstance().getContext().startActivity(openPlay);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();		
	}

	 @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("LikeUsRateUs");
	        //EasyTracker.getInstance().activityStart(this);
	    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }
    
    private void buildRateAlertMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Can you spare a moment to rate us please.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    	 dialog.cancel();
                    	 HopinTracker.sendEvent("LikeUsRateUs","ButtonClick","likeusrateus:click:wanttolikeprompt:yes",1L);                    	 
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    	dialog.cancel();
                    	HopinTracker.sendEvent("LikeUsRateUs","ButtonClick","likeusrateus:click:wanttolikeprompt:no",1L);                   	
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
