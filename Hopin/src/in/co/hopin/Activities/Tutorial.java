package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Activities.SearchInputActivityNew.MyPageAdapter;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.TutorialInstaFragment;
import in.co.hopin.Fragments.TutorialMapViewFragment;
import in.co.hopin.Fragments.TutorialPlanFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.Session.StatusCallback;

public class Tutorial extends FragmentActivity{
	
	FacebookConnector fbconnect;
	ScrollView mScrollView; 
	private StatusCallback skipLoginCallback;
	private ViewPager mInsta_Plan_ViewPager;
	private MyPageAdapter mInsta_Plan_Adapter;
	Button instatut;
	Button plantut;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        HopinTracker.sendView("Tutorial");
        HopinTracker.sendEvent("Tutorial","ScreenOpen","tutorial:open",1L);
        setContentView(R.layout.tutorial_layout);
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        final String uuid = b.getString("uuid");
        
        final EditText userNameView = (EditText) findViewById(R.id.tutorial_name_edittext);
        instatut = (Button) findViewById(R.id.tutorial_instabutton);
        plantut = (Button) findViewById(R.id.tutorial_planbutton);
        mScrollView = (ScrollView) findViewById(R.id.tutorial_scroll);
        mInsta_Plan_ViewPager = (ViewPager)findViewById(R.id.tutorial_viewpager);
        //final EditText phoneView = (EditText) findViewById(R.id.tutorial_mobile_edittext);
        mInsta_Plan_Adapter = new MyPageAdapter(getSupportFragmentManager()); 
        mInsta_Plan_Adapter.setFragments(getFragments());
        mInsta_Plan_ViewPager.setAdapter(mInsta_Plan_Adapter);
        mInsta_Plan_ViewPager.setCurrentItem(1);
        
        instatut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:instabutton",1L);
				mInsta_Plan_ViewPager.setCurrentItem(0);				
			}
		});
        
        plantut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:planbutton",1L);
				mInsta_Plan_ViewPager.setCurrentItem(2);				
			}
		});

        
        userNameView.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:enternamebox",1L);
				// scroll whole view down
				mScrollView.postDelayed(new Runnable() {
				        @Override
				        public void run() {
				        	mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				        }
				    },1000L);
				return false;
			}
		});
       
        
        Button startButton = (Button) findViewById(R.id.tutorial_startbutton);
		// if button is clicked, close the custom dialog
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                String emailText = userNameView.getText().toString();                
                if (!StringUtils.isEmailValid(emailText)) {
                	Toast.makeText(Tutorial.this,"Please enter valid email-id",Toast.LENGTH_SHORT).show();
                    return;
                }
                HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:fbloginlater",1L);
               String userNameText = emailText.substring(0, emailText.indexOf("@"));
               ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, userNameText);
               //ThisUserConfig.getInstance().putString(ThisUserConfig.MOBILE, mobile);
               SBHttpRequest request = new AddUserRequest(uuid,emailText,Tutorial.this);		
       		   SBHttpClient.getInstance().executeRequest(request);       		
       		   ProgressHandler.showInfiniteProgressDialoge(Tutorial.this, "Welcome "+userNameText+"!", "Preparing for first run",null);       		  
				
			}
		}); 
        
        Button faceBookLoginbutton = (Button) findViewById(R.id.tutorial_signInViaFacebook);
        faceBookLoginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("Tutorial","ButtonClick","tutorial:click:fblogin",1L);
				Toast.makeText(Tutorial.this, "Logging...please wait..", Toast.LENGTH_SHORT).show();
				fbconnect = FacebookConnector.getInstance(Tutorial.this);
				fbconnect.loginToFB();
			}
		});   
        
	}
	
	private List<Fragment> getFragments() {	
    	List<Fragment> search_frag_list = new ArrayList<Fragment>();	    	
		Fragment tutPlanFrag = new TutorialPlanFragment();
		Fragment tutInstaFrag = new TutorialInstaFragment();
		Fragment mapViewFrag = new TutorialMapViewFragment();
		search_frag_list.add(tutInstaFrag);
		search_frag_list.add(mapViewFrag);
		search_frag_list.add(tutPlanFrag);
		return search_frag_list;					
	}
	
	public void setFragmentTo(int i)
	{
		mInsta_Plan_ViewPager.setCurrentItem(i);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN) && !"".equals(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID)))
			finish();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(Tutorial.this,requestCode, resultCode, data);
    }

    @Override
    public void onStart(){
        super.onStart();        
    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }
    
    class MyPageAdapter extends FragmentStatePagerAdapter {
    	
    	private List<Fragment> fragments;
    	public List<Fragment> getFragments() {
			return fragments;
		}

		public void setFragments(List<Fragment> fragments) {
			this.fragments = fragments;
		}

		public MyPageAdapter(FragmentManager fm) {    
    	super(fm);
     	
    	}
    	
    	@Override    
    	public Fragment getItem(int position) {			
    	return this.fragments.get(position);   
    	
    	}
    
    	@Override    
    	public int getCount() {    
    	return this.fragments.size();   
    	}  	
	    
    	}

}
