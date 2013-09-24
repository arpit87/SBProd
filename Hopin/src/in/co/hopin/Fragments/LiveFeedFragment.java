package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.CurrentFeed;
import in.co.hopin.HelperClasses.LiveFeed;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.LiveFeedRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LiveFeedFragment extends Fragment{
	
	private static final String TAG = "in.co.hopin.Fragments.LiveFeedFragment";
	private List<LiveFeed> liveFeedlist = null;
	private ViewGroup mFeedContainer;
	
	private ImageView closeFeed = null;	
	private FeedListener feedListener = null;
	int currentFeedShowing = -1;
	long currentfeedcuttofftime = 0;
	int FEEDINTERVAL = 5*1000;	
	int pixelsInRow = 0;
	LayoutInflater inflater = null;
	ViewGroup livefeed1 = null;
	ViewGroup livefeed2 = null;
	ViewGroup livefeedshowing = null;
	ViewGroup livefeednext = null;
	ViewGroup livefeedtemp = null;
	ViewHolder liveFeedViewHolder1 = new ViewHolder();
	ViewHolder liveFeedViewHolder2 = new ViewHolder();
	Animation upinanim = null;
	Animation upoutanim = null;
	int feedCounter = -1;
	ProgressBar progress = null;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
		//update listview
        Logger.i(TAG,"on created live feed frag");
        liveFeedlist = CurrentFeed.getInstance().getLiveFeedList();
        currentfeedcuttofftime = CurrentFeed.getInstance().getCuttoffTime();
        feedListener = getReqListener();     
        upinanim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_in);
        upoutanim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_out);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );
		Logger.i(TAG,"oncreateview livefeed");		
		mFeedContainer = (ViewGroup) inflater.inflate(R.layout.livefeed_layout, null);	
		progress = (ProgressBar) mFeedContainer.findViewById(R.id.livefeed_loading);
		closeFeed = (ImageView) mFeedContainer.findViewById(R.id.livefeed_close);
		livefeed1 = (ViewGroup) mFeedContainer.findViewById(R.id.livefeed_one);
		livefeed2 = (ViewGroup) mFeedContainer.findViewById(R.id.livefeed_two);		
		liveFeedViewHolder1.populateViews(livefeed1);
		liveFeedViewHolder2.populateViews(livefeed2);
		livefeed1.setTag(liveFeedViewHolder1);
		livefeed2.setTag(liveFeedViewHolder2);
		
		closeFeed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				feedListener.hasBeenCancelled = true;				
				getParentFragment().getChildFragmentManager().beginTransaction().hide(LiveFeedFragment.this).commit();
				MapListActivityHandler.getInstance().showLiveFeedButton(true);
				startAutoScroll(false);
			}
		});
		showTimedFeed();		
		return mFeedContainer;
	}
	
	Runnable showNextFeed = new Runnable() {
		
		@Override
		public void run() {
			Logger.i(TAG, "showing next feed");
			currentFeedShowing = (currentFeedShowing+1)%liveFeedlist.size();
			feedCounter = feedCounter+1;
			LiveFeed thisFeed = liveFeedlist.get(currentFeedShowing);
			String fbid = thisFeed.getFbid();			
			if(feedCounter == 0)
			{				
				livefeedshowing = livefeed1;
				livefeednext = livefeed2;
				ViewHolder inFeedHolder = (ViewHolder) livefeed1.getTag();
				inFeedHolder.getNameLoc().setText(thisFeed.getNameLocation());
				inFeedHolder.getSrc().setText(thisFeed.getSource());
				inFeedHolder.getDst().setText(thisFeed.getDestination());
				inFeedHolder.getTime() .setText(thisFeed.getTime());
				 if(!StringUtils.isEmpty(thisFeed.getFbid()))			        	
					 SBImageLoader.getInstance().displayImageElseStub(StringUtils.getFBPicURLFromFBID(thisFeed.getFbid()), liveFeedViewHolder1.getImage(),R.drawable.userpicicon_white);				
			}			
			else
			{
				Logger.d(TAG, "Feed Counter:"+feedCounter);
				//flip the reference of views				
				AnimateOut(livefeedshowing);
				populateDataAndAnimateIn(livefeednext, thisFeed);
				livefeedtemp = livefeedshowing;
				livefeedshowing = livefeednext;
				livefeednext = livefeedtemp;
			}
			Platform.getInstance().getHandler().postDelayed(showNextFeed, FEEDINTERVAL);						
		}
	};
	
	private void populateDataAndAnimateIn(final ViewGroup inFeed,LiveFeed thisFeed)
	{
		ViewHolder inFeedHolder;	
		
		if(inFeed!=null)
		{
			inFeedHolder = (ViewHolder) inFeed.getTag();
			inFeedHolder.getNameLoc().setText(thisFeed.getNameLocation());
			inFeedHolder.getSrc().setText(thisFeed.getSource());
			inFeedHolder.getDst().setText(thisFeed.getDestination());
			inFeedHolder.getTime() .setText(thisFeed.getTime());
			 if(!StringUtils.isEmpty(thisFeed.getFbid()))			        	
				 SBImageLoader.getInstance().displayImage(StringUtils.getFBPicURLFromFBID(thisFeed.getFbid()), inFeedHolder.getImage());				
			 
			 upinanim.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					inFeed.setVisibility(View.VISIBLE);
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {										
				}
			});
		 inFeed.startAnimation(upinanim);
		}
	}
	
	private void AnimateOut(final ViewGroup outFeed)
	{
		if(outFeed!=null)
		{
			Logger.d(TAG, "animating out");
			
			upoutanim.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					outFeed.setVisibility(View.GONE);					
				}
			});
			outFeed.startAnimation(upoutanim);
		}
	}
	
	
	public void startAutoScroll(boolean start)
	{
		if(start)
			Platform.getInstance().getHandler().postDelayed(showNextFeed, FEEDINTERVAL);
		else
		{
			Platform.getInstance().getHandler().removeCallbacks(showNextFeed);
			
		}
	}
	public void showTimedFeed()
	{
		liveFeedlist = CurrentFeed.getInstance().getLiveFeedList();
		if(!liveFeedlist.isEmpty())
		{
			progress.setVisibility(View.GONE);
			Logger.i(TAG, "feed not empty,will show");	
			Platform.getInstance().getHandler().post(showNextFeed);
			startAutoScroll(true);
		}
		else
		{
			progress.setVisibility(View.VISIBLE);
			Logger.i(TAG, "feed empty,will call api");
			String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);			
			LiveFeedRequest feedReq = new LiveFeedRequest(fbid, currentfeedcuttofftime, getReqListener());
			SBHttpClient.getInstance().executeRequest(feedReq);
		}
	}
	
	@Override
    public void onDestroyView() {
        super.onDestroyView();
        feedListener.hasBeenCancelled = true;
		Platform.getInstance().getHandler().removeCallbacks(showNextFeed);
	}
	
	
	public FeedListener getReqListener()
	{
		if(feedListener == null)
			feedListener = new FeedListener();
		return feedListener;
	}
	
	class FeedListener extends SBHttpResponseListener
	{

		@Override
		public void onComplete(String response) {
			Logger.i(TAG, "feed fetch complete");
			if(!hasBeenCancelled)
			{
				showTimedFeed();			
			}
		}
		
	}
	
	private class ViewHolder
	{
		public TextView nameLoc;
		public TextView time;
		public TextView src;
		public TextView dst;
		public ImageView image;
		
		public void populateViews(ViewGroup layout)
		{
			nameLoc = (TextView)layout.findViewById(R.id.livefeedrow_namelocation);
			time = (TextView)layout.findViewById(R.id.livefeedrow_time);
			src = (TextView)layout.findViewById(R.id.livefeedrow_src);
			dst = (TextView)layout.findViewById(R.id.livefeedrow_dst);
			image = (ImageView)layout.findViewById(R.id.livefeedrow_image);			
		}
		
		public TextView getNameLoc() {
			return nameLoc;
		}

		public TextView getTime() {
			return time;
		}

		public TextView getSrc() {
			return src;
		}

		public TextView getDst() {
			return dst;
		}

		public ImageView getImage() {
			return image;
		}
	}

}
