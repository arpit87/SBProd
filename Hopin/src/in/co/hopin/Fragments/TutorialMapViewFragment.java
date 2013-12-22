package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialMapViewFragment extends Fragment {
	
	private static final String TAG = "in.co.hopin.Fragments.TutorialMapViewFragment";
	ImageView map1View;
	ImageView map2View;
	TextView tapFrameTextView;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);        
	}	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		Logger.i(TAG,"oncreateview TutorialMapViewFragment");		
		ViewGroup mView = (ViewGroup) inflater.inflate(R.layout.tutorial_mapviewfragment, null);
		
		  tapFrameTextView = (TextView) mView.findViewById(R.id.tutorial_maptaptextview);
	         map1View = (ImageView) mView.findViewById(R.id.tutorial_smallpicmapview);
	         map2View = (ImageView) mView.findViewById(R.id.tutorial_expandedpicmapview);	        
	        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
	        anim.setDuration(100); //You can manage the time of the blink with this parameter
	        anim.setStartOffset(20);
	        anim.setRepeatMode(Animation.REVERSE);
	        anim.setRepeatCount(Animation.INFINITE);
	        tapFrameTextView.startAnimation(anim);        
	        map1View.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					anim.cancel();
					HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:tapframe",1L);
					tapFrameTextView.setVisibility(View.INVISIBLE);
					map1View.setVisibility(View.GONE);
					map2View.setVisibility(View.VISIBLE);				
				}
			});
	        
		return mView;
	}	
	
}	
