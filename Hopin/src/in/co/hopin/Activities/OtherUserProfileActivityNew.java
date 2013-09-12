package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Fragments.ImageViewerDialog;
import in.co.hopin.Fragments.OtherUserMutualFriends;
import in.co.hopin.Fragments.OtheruserAboutMeFragment;
import in.co.hopin.Fragments.OtheruserCredentialFragment;
import in.co.hopin.Fragments.ShowActiveReqPrompt;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OtherUserProfileActivityNew extends FragmentActivity{
	
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Button button1;
    private Button button2;
	private String fbinfoJsonStr = "";	
	private UserFBInfo userFBInfo = null;
	private JSONObject fbInfoJSON;	
	private ImageView thumbnailView = null;
	private ImageView coverPic = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otheruser_profile_new);
        
        fbinfoJsonStr = getIntent().getStringExtra("fb_info");
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug","got json str:"+fbinfoJsonStr);
		try {
			fbInfoJSON = new JSONObject(fbinfoJsonStr);
			userFBInfo = new UserFBInfo(fbInfoJSON);
			setFBInfoOnWindow();
		} catch (JSONException e) {
			Toast.makeText(this, "Sorry problem fetching profile", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.otheruser_profilenew_viewpager);
        List<Fragment> fragments = getFragments();       
        mPagerAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);      
        mPager.setAdapter(mPagerAdapter);        
        
        coverPic = (ImageView) findViewById(R.id.otheruser_profilenew_cover);
        SBImageLoader.getInstance().displayCoverImage(userFBInfo.getFbid(), coverPic,null);
        
        thumbnailView = (ImageView) findViewById(R.id.otheruser_profilenew_thumbnail);
        
        thumbnailView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String imgUrl = StringUtils.getLargeFBPicURLFromFBID(userFBInfo.getFbid());
				showImage(imgUrl);				
			}
		});
        
        // Watch for button clicks.
        button1 = (Button)findViewById(R.id.otheruser_profilenew_abountme);
        button2 = (Button)findViewById(R.id.otheruser_profilenew_mutualfriendtab);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	aboutSelected();
            	mPager.setCurrentItem(0);
            }
        });        
        
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mutualFriendsSelected();
            	mPager.setCurrentItem(1);
            }
        });
        
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	switch(position)
        		{
        			case 0: //about button selected
        				aboutSelected();
        			break;
        			case 1:
        				mutualFriendsSelected();
        			break;
        			default:
        				aboutSelected();    		
        		}
            }

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
        });
        
        aboutSelected(); // initially select "about" view
        mPager.setCurrentItem(0);
    }
    
    private void showImage(String imgURL)
    {
    	ImageViewerDialog imgviewer_dialog =  ImageViewerDialog.newInstance(imgURL);
    	imgviewer_dialog.show(getSupportFragmentManager(), "show_img");
    }
    
    private List<Fragment> getFragments() {
		List<Fragment> frag_list= new ArrayList<Fragment>(); 
		Fragment aboutMeFrag = new OtheruserAboutMeFragment();
		Fragment credentialsFrag = new OtheruserCredentialFragment();
		Bundle fbInfoBundle = new Bundle();
		fbInfoBundle.putString("fb_info", userFBInfo.getJsonObj().toString());
		aboutMeFrag.setArguments(fbInfoBundle);
		credentialsFrag.setArguments(fbInfoBundle);
		frag_list.add(aboutMeFrag);
		frag_list.add(credentialsFrag);
		return frag_list;
	}

       
	@Override
    public void onBackPressed() {
		HopinTracker.sendEvent("Profile","BackButton","userprofile:other:click:back",1L);
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
	
	public void aboutSelected()
	{
		button1.setTypeface(null,Typeface.BOLD);
    	button2.setTypeface(null,Typeface.NORMAL);
    	button2.setSelected(false);    
    	button1.setSelected(true);
	}
	
	public void mutualFriendsSelected()
	{
		button1.setTypeface(null,Typeface.NORMAL);
    	button1.setSelected(false);
    	button2.setTypeface(null,Typeface.BOLD);    
    	button2.setSelected(true);
	}
	
	
	
	private void setFBInfoOnWindow()
	{		
		
		TextView nameTextView;		
		TextView mutualFriendsTextView;
		ImageView friendImageView;
		ImageView maleIcon;
		ImageView femaleIcon;
		
		friendImageView = (ImageView)findViewById(R.id.otheruser_profilenew_thumbnail);
        nameTextView = (TextView)findViewById(R.id.otheruser_profilenew_name);        
       // mutualFriendsTextView = (TextView)findViewById(R.id.otheruser_profilenew_nummutualfriend);
       // maleIcon = (ImageView)findViewById(R.id.otheruser_profilenew_maleicon);
        //femaleIcon = (ImageView)findViewById(R.id.otheruser_profilenew_femaleicon);
    	
		SBImageLoader.getInstance().displayImageElseStub(userFBInfo.getImageURL(), friendImageView, R.drawable.nearbyusericon);
		nameTextView.setText(userFBInfo.getFullName());	
		//mutualFriendsTextView.setText(userFBInfo.getNumberOfMutualFriends()+ " mutual friends");
		if(userFBInfo.getGender().equals("female"))
		{
			//default is male
			//maleIcon.setVisibility(View.GONE);
			//femaleIcon.setVisibility(View.VISIBLE);
		}
		
	}

	 @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("OtherUserProfile");
	        Map<String, Object> trackArgMap = new HashMap<String,Object>();
	        trackArgMap.put(HopinTracker.FBID, userFBInfo.getFbid());
	        HopinTracker.sendEvent("Profile","ScreenOpen","userprofile:other:open",1L,trackArgMap);
	        //EasyTracker.getInstance().activityStart(this);
	    }
    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }

    class MyPageAdapter extends FragmentPagerAdapter {
    	
    	private List<Fragment> fragments;
    	public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {    
    	super(fm);
    
    	this.fragments = fragments;    	
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
