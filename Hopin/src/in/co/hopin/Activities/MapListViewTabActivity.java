package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.Fragments.SBListFragment;
import in.co.hopin.Fragments.SBMapFragment;
import in.co.hopin.Fragments.ShowActiveReqPrompt;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.DeleteRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;


public class MapListViewTabActivity extends SherlockFragmentActivity {
	//public View mMapViewContainer;
	
	private static final String TAG = "in.co.hopin.Activities.MapListViewTabActivity";
	
	MapListActivityHandler mapListActivityHandler = MapListActivityHandler.getInstance();	
	
	
	
	FragmentManager fm = getSupportFragmentManager();
	private boolean isMapShowing = true;
   
    private SBMapFragment sbMapFragment;
    private SBListFragment sbListFragment;
   
    private Menu mMenu;
    ActionBar ab;
    
	//private FacebookConnector fbconnect;
	
	

	/** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
       // requestWindowFeature((int) Window.FEATURE_ACTION_BAR & ~Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.maplistview);   
        
        ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_black));   
        
        //ToastTracker.showToast("Your userid:"+ThisUserNew.getInstance().getUserID());
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);       
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        
        showMapView();
       
        this.registerReceiver(mapListActivityHandler,new IntentFilter(BroadCastConstants.NEARBY_USER_UPDATED));    
       
        
        //show prompt if any of req active
        
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
        if(!StringUtils.isBlank(instaReqJson) || !StringUtils.isBlank(carpoolReqJson))
        {
        	ShowActiveReqPrompt activereq_dialog = new ShowActiveReqPrompt();
       		activereq_dialog.show(getSupportFragmentManager(), "active_req_prompt");
        }
        
        mapListActivityHandler.setUnderlyingActivity(this);
        //checkIfGPSIsEnabled();
        
    }
    
    @Override
    public void onStart(){
        super.onStart();
        //EasyTracker.getInstance().activityStart(this);
    }

    private void checkIfGPSIsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void buildOnExitAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        final String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
        if(StringUtils.isBlank(instaReqJson) && StringUtils.isBlank(carpoolReqJson))
        {
           Platform.getInstance().stopChatService();
           finish();
           return;
        }       
        builder.setMessage("Do you want to delete the carpool request(s) you placed?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
            	if(!StringUtils.isBlank(instaReqJson))
            	{
            		DeleteRequest deleteInstaRequest = new DeleteRequest(1);
            		SBHttpClient.getInstance().executeRequest(deleteInstaRequest);
            	}
            	if(!StringUtils.isBlank(carpoolReqJson))
            	{
            		DeleteRequest deleteCarPoolRequest = new DeleteRequest(0);
            		SBHttpClient.getInstance().executeRequest(deleteCarPoolRequest);
            	}
            	Platform.getInstance().stopChatService();
            	finish();
            	return;
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
            	finish();
                return;
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);       
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	//we update realtime when on map activity
    	SBLocationManager.getInstance().StartListeningtoNetwork(); 
    	
    }

    //test
    @Override
	public void onPause(){
    	super.onPause();
    	//MapListActivityHandler.getInstance().setUpdateMap(false);
    	SBLocationManager.getInstance().StopListeningtoGPS();    	
        SBLocationManager.getInstance().StopListeningtoNetwork();
        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);
    	//mymapview.getOverlays().clear();
    	//mymapview.postInvalidate();
    }
    
    @Override
    public void onDestroy()
    {    
    	super.onDestroy();    	
    	this.unregisterReceiver(mapListActivityHandler);
    	mapListActivityHandler.clearAllData();  
    	ThisUserNew.clearAllData();
    	CurrentNearbyUsers.getInstance().clearAllData();    	  
        int count = ThisAppConfig.getInstance().getInt(ThisAppConfig.APPOPENCOUNT);     	
     	ThisAppConfig.getInstance().putInt(ThisAppConfig.APPOPENCOUNT,count++);
     	if(count%5==0)
     	{
     		//show msg every fifth time ap is closed
     		Intent i = new Intent(Platform.getInstance().getContext(),FeedbackActivity.class);
 			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     	
 			i.putExtra("showprompt", true);
 			Platform.getInstance().getContext().startActivity(i);
     		
     	}   
     	EasyTracker.getTracker().setStartSession(false);
     	EasyTracker.getInstance().dispatch(); //remove this after test
    }
    
	@Override
	public void onBackPressed() {
		HopinTracker.sendEvent("Map","ButtonClick","map:click:back",1L);
        if (!isMapShowing){
            isMapShowing = true;
            showMapView();
            MenuItem menuItem = mMenu.findItem(R.id.main_menu_btn_listview);
            menuItem.setIcon(R.drawable.maptolist);
        } else {
                
                MapListViewTabActivity.super.onBackPressed();
        }
        
	}
  
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	HopinTracker.sendEvent("MainMenu","MenuOpen","mainmenu:open",1L);
        switch (menuItem.getItemId())
        {
        case R.id.main_menu_btn_search:
        	//onSearchRequested();        	
        	HopinTracker.sendEvent("Map","ButtonClick","map:click:searchusers",1L);
	    	 Intent searchInputIntent = new Intent(this,SearchInputActivityNew.class);	
	    	 searchInputIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	   		 startActivity(searchInputIntent);
        	break;
        case R.id.main_menu_btn_listview:
        	HopinTracker.sendEvent("Map","ButtonClick","map:click:togglemaplist",1L);
        	toggleMapListView(menuItem);
        	break;
       /* case R.id.fb_login_menuitem:
        	if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
        	{
        		Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
        		break;
        	}
        	FBLoginDialogFragment fblogin_dialog = new FBLoginDialogFragment();
			fblogin_dialog.show(getSupportFragmentManager(), "fblogin_dialog");
			break;
        case R.id.main_menu_logout:
        	//logout from chat server?
			FacebookConnector fbconnect = new FacebookConnector(MapListViewTabActivity.this);
        	fbconnect.logoutFromFB();
        	break;*/
        case R.id.main_menu_settings:
        	HopinTracker.sendEvent("MainMenu","MenuClick","mainmenu:click:settings",1L);
        	Intent i = new Intent(this,SettingsActivity.class);
        	i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(i);
        	break;
        case R.id.main_menu_exit:
        	HopinTracker.sendEvent("MainMenu","MenuClick","mainmenu:click:exit",1L);
        	//delete user request,close service
        	buildOnExitAlertDialog();
                     	
        	break; 
   	/* case R.id.test_app_menuitem:
   		ShowActiveReqPrompt activereq_dialog = new ShowActiveReqPrompt();
   		activereq_dialog.show(getSupportFragmentManager(), "fblogin_dialog");
   		 break;*/
     case R.id.main_menu_my_requests:    	 
    	 HopinTracker.sendEvent("MainMenu","MenuClick","mainmenu:click:activerequest",1L);
         Intent myRequestIntent = new Intent(this, MyRequestsActivity.class);
         myRequestIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         startActivity(myRequestIntent);
         break;
     case R.id.main_menu_my_chats:
    	 HopinTracker.sendEvent("MainMenu","MenuClick","mainmenu:click:chatslist",1L);
         Intent myChatsIntent = new Intent(this, MyChatsActivity.class);
         myChatsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         startActivity(myChatsIntent);
         break;         
     case R.id.main_menu_shareapp: 
    	 HopinTracker.sendEvent("MainMenu","MenuClick","mainmenu:click:tellafriend",1L);
    	 Intent sendIntent = new Intent();
    	 sendIntent.setAction(Intent.ACTION_SEND);
    	 sendIntent.putExtra(Intent.EXTRA_TEXT, "Very useful, take a look: " + '\n' + getResources().getString(R.string.http_app_link));
    	 sendIntent.setType("text/plain");
    	 startActivity(sendIntent);
    	 break;         
     
        } 
        return super.onOptionsItemSelected(menuItem);
    }   
    
    public void buttonOnMapClick(View button)
    {
    	switch(button.getId())
    	{
    	case R.id.my_location_button:
    		MapListActivityHandler.getInstance().myLocationButtonClick();    		
    		break; 	
    	}
    }

    private void toggleMapListView(MenuItem menuItem)
    {
    	if(!isMapShowing)
    	{    		
    		isMapShowing = true;
    		showMapView();
    		menuItem.setIcon(R.drawable.maptolist);
    	}
    	else
    	{    		
    		isMapShowing = false;    		
    		showListView();
    		menuItem.setIcon(R.drawable.listtomap);
    	}
    		
    }
    
    private void showMapView()
    {        
        HopinTracker.sendView("MapView");
        HopinTracker.sendEvent("MapView","ScreenOpen","map:open",1L);
    	if (fm != null) {
            
            FragmentTransaction ft = fm.beginTransaction();
            if (sbMapFragment == null) {
                sbMapFragment = new SBMapFragment();
                ft.add(R.id.maplistviewcontent, sbMapFragment);
            } else {
                ft.replace(R.id.maplistviewcontent, sbMapFragment);
            }
            ft.commit();            
        }
    }
    
    private void showListView()
    {
        HopinTracker.sendView("ListView");
        HopinTracker.sendEvent("ListView","ScreenOpen","listmatchingusers:open",1L);
        if (fm != null) {
            
            FragmentTransaction ft = fm.beginTransaction();
            if (sbListFragment == null) {
                sbListFragment = new SBListFragment();
            }
            ft.add(R.id.maplistviewcontent, sbListFragment);
            ft.commit();           
        }
    }
   
	
      
}
