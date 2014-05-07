package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Util.HopinTracker;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class VisitWebsiteActivity extends Activity{
		
	ImageView btn_close;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
        setContentView(R.layout.visitwebsite_dialog);        
        btn_close = (ImageView)findViewById(R.id.visitwebsite_close_button);
    	// if button is clicked, close the custom dialog
        btn_close.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {			
    			finish();
    		}
    	});
      		
	}
	
	@Override
    public void onResume(){
    	super.onResume();
		
}	   

   @Override
   public void onStart(){
       super.onStart();
       HopinTracker.sendView("VisitWebsite");
       HopinTracker.sendEvent("VisitWebsite","ScreenOpen","visitwebsite:open",1L);
   }

   @Override
   public void onStop(){
       super.onStop();
       //EasyTracker.getInstance().activityStop(this);
   }
}
