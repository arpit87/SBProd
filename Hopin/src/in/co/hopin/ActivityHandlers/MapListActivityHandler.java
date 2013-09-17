package in.co.hopin.ActivityHandlers;

import in.co.hopin.R;
import in.co.hopin.Activities.InviteFriendsActivity;
import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.Adapter.NearbyUsersListViewAdapter;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.Fragments.SBListFragment;
import in.co.hopin.Fragments.SBMapFragment;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.SBHttpResponseListener;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.MapHelpers.BaseItemizedOverlay;
import in.co.hopin.MapHelpers.GourpedNearbyUsersIteamizedOverlay;
import in.co.hopin.MapHelpers.NearbyUsersItemizedOverlay;
import in.co.hopin.MapHelpers.ThisUserItemizedOverlay;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.NearbyUserGroup;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

public class MapListActivityHandler  {
	
	SBMapView mMapView;	
	private static final String TAG = "in.co.hopin.ActivityHandlers.MapActivityHandler";
	private static MapListActivityHandler instance=new MapListActivityHandler();
	private MapListViewTabActivity underlyingActivity;	
	private BaseItemizedOverlay nearbyUserGroupItemizedOverlay;
	private BaseItemizedOverlay nearbyUserItemizedOverlay;
	private MapController mapcontroller;
	private BaseItemizedOverlay thisUserOverlay;
	private boolean updateMap = false;
	private ProgressDialog progressDialog;
	AlertDialog alertDialog ;	
	private boolean mapInitialized = false;
	private SBListFragment listFrag;	
	ViewGroup mListViewContainer;	
	ImageView mListImageView;
	private TextView mDestination;
	private TextView mSource;
	private TextView mUserName;
	private TextView mtime;
	ListView mListView = null;
	ViewGroup mListViewFooter = null;
	private ViewGroup mMapViewContainer;	
	private ImageButton selfLocationButton = null;
	private NearbyUserUpdatedListener mUserUpdatedListener = null;
	
	
			
	public BaseItemizedOverlay getNearbyUserItemizedOverlay() {
		return nearbyUserItemizedOverlay;
	}

    public BaseItemizedOverlay getNearbyUserGroupItemizedOverlay() {
        return nearbyUserGroupItemizedOverlay;
    }

	public boolean isMapInitialized() {
		return mapInitialized;
	}


	public MapListViewTabActivity getUnderlyingActivity() {
		return underlyingActivity;			
	}

	public void setUnderlyingActivity(MapListViewTabActivity underlyingActivity) {
		this.underlyingActivity = underlyingActivity;
	}

	public void setListFrag(SBListFragment listFrag) {
		this.listFrag = listFrag;
	}
	
	public void setMapFrag(SBMapFragment mapFrag) {
	}

	private MapListActivityHandler(){super();}	
	
	
	public static MapListActivityHandler getInstance()
	{		
		return instance;
		
	}	
	
	public boolean isUpdateMap() {
		return updateMap;
	}


	public void setUpdateMap(boolean updateMapRealTime) {
		this.updateMap = updateMapRealTime;
	}
		
	public void initMyLocation() 
	{ 
		
		SBGeoPoint currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
		if(currGeo == null)
		{
			//location not found yet after initial screen!try more for 6 secs
			progressDialog = ProgressDialog.show(underlyingActivity, "Fetching location", "Trying,please wait..", true);			
			 Runnable fetchLocation = new Runnable() {
			      public void run() {
			    	  Location currLoc = SBLocationManager.getInstance().getLastXSecBestLocation(6*60);
			    	  progressDialog.dismiss();
			    	  if(currLoc != null)
			    	  {			    		  
			    		  ThisUserNew.getInstance().setCurrentGeoPoint(new SBGeoPoint((int) (currLoc.getLatitude() * 1e6), (int) (currLoc.getLongitude() * 1e6)));
			    		  ThisUserNew.getInstance().setCurrentGeoPointToSourceGeopoint();
			    		  if(!isMapInitialized())
			    			  putInitialOverlay();
			    	  }
			    	  else
			    	  {
			    		  HopinTracker.sendEvent("Map","LocationStatus","map:initlocation:notfound",1L);
			    		  alertDialog = new AlertDialog.Builder(underlyingActivity).create(); 
			    		  alertDialog.setTitle("Boo-hoo..");
			    		  alertDialog.setMessage("Some problem with fetching network location,please enter source location yourself in user search");
			    		  alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			    	           public void onClick(DialogInterface dialog, int id) {
			    	                dialog.cancel();
			    	           }
			    	       });
			    		  alertDialog.show();
			    	  }
			      } 
			 };			        
			Platform.getInstance().getHandler().postDelayed(fetchLocation, 2000);// post after 6 secs
		}
		else
		{				
			ThisUserNew.getInstance().setCurrentGeoPointToSourceGeopoint();
			HopinTracker.sendEvent("Map","LocationStatus","map:initlocation:found",1L);
			putInitialOverlay();		
		}
	}
	
	public void myLocationButtonClick()
	{
		HopinTracker.sendEvent("Map","ButtonClcik","map:click:mylocationbutton",1L);
		if(!mapInitialized)
		{			
			initMyLocation();	
			return;
		}
		
		//if we have source which might be different from current then zoom in to source
		SBGeoPoint sourceGeoPoint = ThisUserNew.getInstance().getSourceGeoPoint();
		if(sourceGeoPoint!=null)
		{
			centreMapTo(sourceGeoPoint);
			return;
		}
		//else to current
		int startInterval = 300;
		Location thisCurrLoc = SBLocationManager.getInstance().getLastXSecBestLocation(startInterval);
		if(thisCurrLoc == null)
		{
			progressDialog = ProgressDialog.show(underlyingActivity, "Fetching location", "Please wait..", true);
			for(int attempt = 1 ; attempt <= 4; attempt++ )
			{
				//thisCurrLoc = SBLocationManager.getInstance().getCurrentBestLocation(location)
				thisCurrLoc = SBLocationManager.getInstance().getLastXSecBestLocation(startInterval*attempt);
				if(thisCurrLoc != null)
				{
					break;
				}
			}
			progressDialog.dismiss();
		}
		if(thisCurrLoc!=null)
		{
			SBGeoPoint currGeo = new SBGeoPoint((int)(thisCurrLoc.getLatitude()*1e6),(int)(thisCurrLoc.getLongitude()*1e6));
			ThisUserNew.getInstance().setCurrentGeoPoint(currGeo);
			ThisUserNew.getInstance().setSourceGeoPoint(currGeo);
			MapListActivityHandler.getInstance().setUpdateMap(true);
			updateThisUserMapOverlay();
		}
	}
		
public void centreMapTo(SBGeoPoint centrePoint)
{
	if(centrePoint !=null)
		mapcontroller.animateTo(centrePoint);
}

public void centreMapToPlusLilUp(SBGeoPoint centrePoint)
{
	GeoPoint lilUpcentrePoint = new GeoPoint(centrePoint.getLatitudeE6()+90000/mMapView.getZoomLevel(), centrePoint.getLongitudeE6());
	if(lilUpcentrePoint !=null)
		mapcontroller.animateTo(lilUpcentrePoint);
}
	
	
	
	private void putInitialOverlay()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"initializing this user location");
	    mapcontroller.setZoom(14);
	    if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"setting myoverlay");        
	    thisUserOverlay = new ThisUserItemizedOverlay(mMapView); 
	    //SBGeoPoint currGeo = ThisUser.getInstance().getCurrentGeoPoint();
	    //if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"location is:"+currGeo.getLatitudeE6()+","+currGeo.getLongitudeE6());		
	    thisUserOverlay.addThisUser();	    
	    mMapView.getOverlays().add(thisUserOverlay);
	    mMapView.postInvalidate();	       
	    mapcontroller.animateTo(ThisUserNew.getInstance().getCurrentGeoPoint());
	    //onResume of mapactivity doesnt update user till its once initialized
	    mapInitialized = true;
	  
	}


	
	private void updateNearbyUsersonMap() {
		
		//caution while updating nearbyusers
		//this user may be interacting with a view so we are going to show progressbar			
					
		List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
		
		//update map view
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating earby user");
		if(nearbyUserItemizedOverlay!=null || nearbyUserGroupItemizedOverlay != null)
		{
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"removing prev nearby users overlay");			
			if (nearbyUserItemizedOverlay != null) {
                mMapView.getOverlays().remove(nearbyUserItemizedOverlay);
            }
            if (nearbyUserGroupItemizedOverlay != null) {
                mMapView.getOverlays().remove(nearbyUserGroupItemizedOverlay);
            }
			mMapView.removeAllNearbyUserView();
		}
      
		if(nearbyUsers == null || nearbyUsers.size()==0)
			return;
	
        Context context = Platform.getInstance().getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        float  ppi = displayMetrics.densityDpi * density;
        float factor = displayMetrics.densityDpi / 160;
        double gridWidth = ppi * 0.2; //pixels in 5 mm

        Map<String,List<NearbyUser>> gridToUsersMap = new HashMap<String, List<NearbyUser>>();
        for (NearbyUser nearbyUser : nearbyUsers) {
            SBGeoPoint geoPoint = nearbyUser.getUserLocInfo().getGeoPoint();
            Point point = mMapView.getProjection().toPixels(geoPoint, null);
            float x = point.x * factor;
            float y = point.y * factor;

            int column = (int)(x/gridWidth);
            int row = (int) (y/gridWidth);
            String key = column + ":" + row;
            List<NearbyUser> users = gridToUsersMap.get(key);
            if (users == null) {
                users = new ArrayList<NearbyUser>();
                gridToUsersMap.put(key, users);
            }
            users.add(nearbyUser);
        }
		
        List<NearbyUserGroup> groups = new LinkedList<NearbyUserGroup>();
        List<NearbyUser> individualUsers = new LinkedList<NearbyUser>();
        for (Map.Entry<String, List<NearbyUser>> entry : gridToUsersMap.entrySet()){
            if (entry.getValue().size() == 1){
                individualUsers.add(entry.getValue().get(0));
            } else {
                groups.add(new NearbyUserGroup(entry.getValue()));
            }
        }
		
        if(!individualUsers.isEmpty())
        {
	        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"adding individualUsers useroverlay,no indi:"+individualUsers.size());	
	        nearbyUserItemizedOverlay = new NearbyUsersItemizedOverlay(mMapView);
	        nearbyUserItemizedOverlay.addList(individualUsers);
	        mMapView.getOverlays().add(nearbyUserItemizedOverlay);
        }
        
        if(!groups.isEmpty())
        {
        	if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"adding groups useroverlay,no.groups:"+groups.size());
			nearbyUserGroupItemizedOverlay = new GourpedNearbyUsersIteamizedOverlay(mMapView);
			nearbyUserGroupItemizedOverlay.addList(groups);
			 mMapView.getOverlays().add(nearbyUserGroupItemizedOverlay);
        }
      
		
		//show fb login popup at bottom if not yet logged in
		boolean isfbloggedin = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
		if(!isfbloggedin)
		{
			HopinTracker.sendEvent("Map","facebooklogin","map:fbloginprompt:show",1L);
			CommunicationHelper.getInstance().FBLoginpromptPopup_show(underlyingActivity,true);
		}	
		
		ProgressHandler.dismissDialoge();
		
	}
	
	public void updateOverlayOnZoomChange()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating nearby users on zoom change");
		updateNearbyUsersonMap();		
		mMapView.postInvalidate();
	}
	
	public void updateNearbyUsersOnUsersChange()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating nearby users on user change");
		updateNearbyUsersonMap();
		updateNearbyUserOnList();
		centerMap();
		mMapView.postInvalidate();
	}

    private void updateNearbyUserOnList() {
    	List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if (nearbyUsers == null){
            nearbyUsers = Collections.emptyList();
        }

        NearbyUsersListViewAdapter adapter = new NearbyUsersListViewAdapter(underlyingActivity, nearbyUsers);

        if(listFrag != null)
        {
            listFrag.setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

   
	public void updateThisUserMapOverlay()
	{		
		//be careful here..do we have location yet?
		Logger.i(TAG,"update this user called");
		if(mMapViewContainer!=null)
		{
			if(thisUserOverlay == null)	
			{			
				thisUserOverlay = new ThisUserItemizedOverlay(mMapView);
				thisUserOverlay.updateThisUser();	    
			    mMapView.getOverlays().add(thisUserOverlay);		   
			}
			else
			{
			    thisUserOverlay.updateThisUser();
			    if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"this user map overlay updated");
			}
		    mMapView.postInvalidate();	    
		    //dont centre here else on every automatic update it centres
		    //mapcontroller.animateTo(ThisUser.getInstance().getSourceGeoPoint());
		}
	}	
	
		
	private void centerMap() {

		int mylat = ThisUserNew.getInstance().getSourceGeoPoint().getLatitudeE6();
		int mylon = ThisUserNew.getInstance().getSourceGeoPoint().getLongitudeE6();
        int minLat = mylat;
        int maxLat = mylat;
        int minLon = mylon;
        int maxLon = mylon;
        
        List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if (nearbyUsers != null) {
            for (NearbyUser n : nearbyUsers) {
                    SBGeoPoint geoPoint = n.getUserLocInfo().getGeoPoint();
                    int lat = (int) (geoPoint.getLatitudeE6());
                    int lon = (int) (geoPoint.getLongitudeE6());

                    maxLat = Math.max(lat, maxLat);
                    minLat = Math.min(lat, minLat);
                    maxLon = Math.max(lon, maxLon);
                    minLon = Math.min(lon, minLon);
            }
        }

        mapcontroller.zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));        
        mapcontroller.animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        mMapView.setOldZoomLevel(mMapView.getZoomLevel());
}
	
public void clearAllData()
{
    if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "clearing all data");
    mapInitialized = false;
    thisUserOverlay = null;
    nearbyUserItemizedOverlay = null;
    nearbyUserGroupItemizedOverlay = null;
    mListViewContainer = null;
    mMapViewContainer = null;
	if(mMapView!=null)
	{
		mMapView.setOldZoomLevel(-1);
		mMapView.removeAllViews();
		mMapView.getOverlays().clear();
	}
	if(listFrag!=null)
	{		
		listFrag.reset();
	}
	if(mSource!=null) {
        mSource.setText(" Source");
    }
	if(mDestination!=null) {
		mDestination.setText("  Destination");
    }
	if(mtime!=null)
		mtime.setText("  Time");
}

private void buildAlertMessageForNoUserAndInviteFriends() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(underlyingActivity);
    builder.setMessage("Sorry, No match found. Please invite your friends to increase possibility of finding match")
            .setCancelable(true)
            .setNegativeButton("Cancel", new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();					
				}
			})
            .setPositiveButton("Ok", new OnClickListener() {
                public void onClick(final DialogInterface dialog, final int id) {
                    Intent startInviteActivity = new Intent(underlyingActivity,InviteFriendsActivity.class);
                    underlyingActivity.startActivity(startInviteActivity);
                }
            })           
            ;
    final AlertDialog alert = builder.create();
    alert.show();
}


public ViewGroup getThisListContainerWithListView() {
    if (mListViewContainer == null) {
        mListViewContainer = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.nearbyuserlistview, null, false);
        mListImageView = (ImageView) mListViewContainer.findViewById(R.id.selfthumbnail);
        mUserName = (TextView) mListViewContainer.findViewById(R.id.my_name_listview);
        mDestination = (TextView) mListViewContainer.findViewById(R.id.my_destination_listview);
        mSource =  (TextView) mListViewContainer.findViewById(R.id.my_source_listview);
        mtime = (TextView) mListViewContainer.findViewById(R.id.my_time_listview); 
        //mMapViewContainer.removeView(mMapView);
    }
	return mListViewContainer;
}

public ViewGroup getThisMapContainerWithMapView()
{
	if(mMapViewContainer == null)
	{
		mMapViewContainer = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.map,null,false);
		mMapView = (SBMapView) mMapViewContainer.findViewById(R.id.map_view);		
		selfLocationButton = (ImageButton) mMapViewContainer.findViewById(R.id.my_location_button);    		   		
		mMapView.getOverlays().clear(); 
		mapcontroller = mMapView.getController();
		mMapView.setBuiltInZoomControls(false);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"initialize handler");
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"initialize mylocation");
        MapListActivityHandler.getInstance().initMyLocation();
		//mMapViewContainer.removeView(mMapView);
	}
	else
	{
		mMapViewContainer.addView(mMapView);
		mMapViewContainer.addView(selfLocationButton);		
		//mMapViewContainer.addView(offerRideButton);
		//if(currentIsOfferMode)
		//	offerRideButton.setChecked(true);
	}	
	return mMapViewContainer;
}

public void updateUserPicInListView() {
    if (mListImageView != null) {
        String fbPicURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL);
        if (fbPicURL != "") {
            SBImageLoader.getInstance().displayImageElseStub(fbPicURL, mListImageView, R.drawable.userpicicon);
        } else {
            mListImageView.setImageDrawable(Platform.getInstance().getContext().getResources().getDrawable(R.drawable.userpicicon));
        }
    }
}

public void updateUserNameInListView() {
    if (mUserName != null) {
        String userName = ThisUserConfig.getInstance().getString(ThisUserConfig.USERNAME);
        if (userName=="") {
        	//ToastTracker.showToast("haaw..username null!!");
            return;
        }
        mUserName.setText(userName);
    }
}

public void updateSrcDstTimeInListView() {

    if (mListViewContainer == null) {
        mListViewContainer = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.nearbyuserlistview, null, false);
        mListImageView = (ImageView) mListViewContainer.findViewById(R.id.selfthumbnail);
        mUserName = (TextView) mListViewContainer.findViewById(R.id.my_name_listview);
        mDestination = (TextView) mListViewContainer.findViewById(R.id.my_destination_listview);
        mSource = (TextView) mListViewContainer.findViewById(R.id.my_source_listview);
        mtime = (TextView) mListViewContainer.findViewById(R.id.my_time_listview);
    }

    String destination = ThisUserNew.getInstance().getDestinationFullAddress();
    if (!StringUtils.isBlank(destination)) {
        mDestination.setText(destination);
        String source = ThisUserNew.getInstance().getSourceFullAddress();
        if (StringUtils.isBlank(source))
            source = "My Location";
        mSource.setText(source);

        String date_time = ThisUserNew.getInstance().getDateAndTimeOfTravel();
        if (!StringUtils.isBlank(date_time)) {
            mtime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "h:mm a, EEE, MMM d", date_time));
        }
    }
}

    public void closeExpandedViews(){
        BaseItemizedOverlay nearbyUserOverlay = MapListActivityHandler.getInstance().getNearbyUserItemizedOverlay();
        if(nearbyUserOverlay!=null)
            nearbyUserOverlay.removeExpandedShowSmallViews();
        BaseItemizedOverlay nearbyUserGroupOverlay = MapListActivityHandler.getInstance().getNearbyUserGroupItemizedOverlay();
		if(nearbyUserGroupOverlay!=null)
			nearbyUserGroupOverlay.removeExpandedShowSmallViews();
    }

    public void setSourceAndDestination(JSONObject jsonObject) throws JSONException {
        double srcLat = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLATITUDE));
        double srcLong = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLONGITUDE));
        double destLat = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLATITUDE));
        double destLong = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLONGITUDE));
        String srcAddress = jsonObject.getString(UserAttributes.SRCADDRESS);
        String srcLocality = jsonObject.getString(UserAttributes.SRCLOCALITY);
        String dstAddress = jsonObject.getString(UserAttributes.DSTADDRESS);
        String dstLocality = jsonObject.getString(UserAttributes.DSTLOCALITY);
        String dateTime = jsonObject.getString(UserAttributes.DATETIME);
        int dailyInstaType = jsonObject.getInt(UserAttributes.DAILYINSTATYPE);
        int shareOfferType = Integer.parseInt(jsonObject.getString(UserAttributes.SHAREOFFERTYPE));

        ThisUserNew.getInstance().setSourceGeoPoint(new SBGeoPoint((int)(srcLat*1e6),(int)(srcLong*1e6)));
        ThisUserNew.getInstance().setDestinationGeoPoint(new SBGeoPoint((int) (destLat * 1e6), (int) (destLong * 1e6)));
        ThisUserNew.getInstance().setSourceFullAddress(srcAddress);
        ThisUserNew.getInstance().setSourceLocality(srcLocality);
        ThisUserNew.getInstance().setDestinationFullAddress(dstAddress);
        ThisUserNew.getInstance().setDestiantionLocality(dstLocality);
        ThisUserNew.getInstance().setDateOfTravel(StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", dateTime));
        ThisUserNew.getInstance().setTimeOfTravel(StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "HH:mm", dateTime));
        ThisUserNew.getInstance().set_Daily_Instant_Type(dailyInstaType);
        ThisUserNew.getInstance().set_Take_Offer_Type(shareOfferType);

        MapListActivityHandler.getInstance().updateThisUserMapOverlay();
        mapInitialized = true;  // just in case it was not initialized like if location couldnt be fetched
        MapListActivityHandler.getInstance().centreMapTo(ThisUserNew.getInstance().getSourceGeoPoint());
    }
    
    public NearbyUserUpdatedListener getNearbyUserUpdatedListener()
	{
		if(mUserUpdatedListener == null)
			mUserUpdatedListener = instance.new NearbyUserUpdatedListener();
		return mUserUpdatedListener;
	}
    
    class NearbyUserUpdatedListener extends SBHttpResponseListener
	{

		@Override
		public void onComplete(String response) {
			if(!hasBeenCancelled)
			{
				if(response.equals(BroadCastConstants.NEARBY_USER_UPDATED))
				{
					int size = CurrentNearbyUsers.getInstance().getAllNearbyUsers().size();
					if(size>0)
						ToastTracker.showToast(size+" match found");
					updateNearbyUsersOnUsersChange();
					if(CurrentNearbyUsers.getInstance().getAllNearbyUsers().size()==0)
						buildAlertMessageForNoUserAndInviteFriends();
				}						
			}
		}
		
	}

}
