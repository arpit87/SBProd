package in.co.hopin.HelperClasses;

import in.co.hopin.R;
import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.ChatClient.ChatWindow;
import in.co.hopin.ChatClient.SBChatMessage;
import in.co.hopin.ChatService.Message;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.FBLoginDialogFragment;
import in.co.hopin.HttpClient.ChatServiceCreateUser;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SaveFBInfoRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/****
 * 
 * @author arpit87
 * handler code for various scenaiors on chat click here
 * like not logged in to server,not fb login yet etc
 * to start chat from anywhere call this class
 */
public class CommunicationHelper {
	
	private static String TAG = "in.co.hopin.ActivityHandler.ChatHandler";
	static CommunicationHelper instance = new CommunicationHelper();
	Context context = Platform.getInstance().getContext(); 
	private boolean isFBPromptShowing = false;
	PopupWindow fbPopupWindow;
	View popUpView = null;
	//private FacebookConnector fbconnect;
		
	public static CommunicationHelper getInstance()
	{
		return instance;
	}
	
	
	public void onChatClickWithUser(FragmentActivity underLyingActivity,String fbid,String full_name)
	{
		//chat username and id are set only after successful addition to chat server
		//if these missing =?not yet added on chat server
		
		String thiUserChatUserName = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
		String thisUserChatPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
		HopinTracker.sendEvent("ChatClicks","ButtonClick",underLyingActivity.getClass().toString()+":click:chatinitiate",1L);
		if(thiUserChatUserName == "" || thisUserChatPassword == "")
		{
			if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			{
				//make popup 
				FBLoginpromptPopup_show(underLyingActivity,true);
			}
			else 
			{
				Logger.d(TAG,"FBLogged in but not chat!!Server working properly for chat req?sending again");
				//sending fbinfo n chatreq again
				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
				{
					//server couldnt receive fbinfo
					SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
					SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);
				}
				
				SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
		     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);							
			}
			//Intent fbLoginIntent = new Intent(context,LoginActivity.class);			
			//MapListActivityHandler.getInstance().getUnderlyingActivity().startActivity(fbLoginIntent);
		}	
		else 
		{
			if(fbid!="" && full_name!="")
			{
				Intent startChatIntent = new Intent(Platform.getInstance().getContext(),ChatWindow.class);					
				startChatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
			 			| Intent.FLAG_ACTIVITY_NEW_TASK);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT, fbid);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, full_name);
				context.startActivity(startChatIntent);
			}
			else
				ToastTracker.showToast("Sorry, user is not logged in");
		}
		
	
	}
	
	public void onHopinProfileClickWithUser(FragmentActivity underLyingActivity,UserFBInfo fbInfo)
	{		    
		HopinTracker.sendEvent("HopinProfileClicks","ButtonClick",underLyingActivity.getClass().toString()+":click:hopinIcon",1L);
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			FBLoginpromptPopup_show(underLyingActivity,true);
		}
		else if(fbInfo.FBInfoAvailable())
		{			
			Intent hopinNewProfile = new Intent(underLyingActivity,OtherUserProfileActivityNew.class);
	    	hopinNewProfile.putExtra("fb_info", fbInfo.getJsonObj().toString());
	    	hopinNewProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	underLyingActivity.startActivity(hopinNewProfile);
		}
		else
			ToastTracker.showToast("Sorry, user is not logged in");
	}
	
	public void onFBIconClickWithUser(FragmentActivity underlyingActivity, String userFBID, String userFBName)
	{
		HopinTracker.sendEvent("FacebookProfileClicks","ButtonClick",underlyingActivity.getClass().toString()+"click:facebookIcon",1L);
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			FBLoginpromptPopup_show(underlyingActivity,true);
		}
		else if(userFBID!="" || userFBName !="")
		{
			FacebookConnector.getInstance(underlyingActivity).openFacebookPage(userFBID,userFBName);
		}
		else
			ToastTracker.showToast("Not available, user not FB logged in");
	}
	
	public void sendChatNotification(int id,String fb_id,String participant_name,String chatMessage) {

	   	 NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			 Intent chatIntent = new Intent(context,ChatWindow.class);
			 	chatIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			   chatIntent.putExtra(ChatWindow.PARTICIPANT, fb_id);
			   chatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, participant_name);		 
			  	
			 Logger.i(TAG, "Sending notification") ;
			 PendingIntent pintent = PendingIntent.getActivity(context, id, chatIntent, PendingIntent.FLAG_ONE_SHOT);
			 Uri sound_uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			 
			 Notification notif = new Notification(R.drawable.launchernew,"New message from "+participant_name,System.currentTimeMillis());
			 notif.flags |= Notification.FLAG_AUTO_CANCEL;
			 notif.setLatestEventInfo(context, participant_name, chatMessage, pintent);
					
			 Message welcome_message = new Message("", ServerConstants.AppendServerIPToFBID(fb_id), chatMessage, StringUtils.gettodayDateInFormat("hh:mm")
					 								,Message.MSG_TYPE_CHAT, SBChatMessage.RECEIVED,System.currentTimeMillis(),participant_name);
			 
			 ChatHistory.addtoChatHistory(welcome_message);
			 ActiveChat.addChat(fb_id, participant_name, chatMessage);
			 
				notif.ledARGB = 0xff0000ff; // Blue color
				notif.ledOnMS = 1000;
				notif.ledOffMS = 1000;
				notif.defaults |= Notification.DEFAULT_LIGHTS;	
				notif.sound = sound_uri;
	     
				notificationManager.notify(id, notif);
				Logger.i(TAG, "notification sent") ;			
				
			    }
	
	public void FBLoginDialog_show(final FragmentActivity underlyingActivity)
	{		
		MapListActivityHandler.getInstance().closeExpandedViews();
		FBLoginDialogFragment fblogin_dialog = FBLoginDialogFragment.newInstance(FacebookConnector.getInstance(underlyingActivity));
		fblogin_dialog.show(underlyingActivity.getSupportFragmentManager(), "fblogin_dialog");		
	}
	
	public void FBLoginpromptPopup_show(final FragmentActivity underlyingActivity , final boolean show)
		{		 	
		   //we keeping a track for which all activities we are showing prompt
		   //if already showing then blink it
		   Logger.d(TAG, "showing fb login popup:"+show);		    
			if(show)
			{
				HopinTracker.sendEvent("FacebookLoginpromptPopup","ShowPrompt","fblogin_popup:show_on:"+underlyingActivity.getClass().toString(),1L);
				if(!isFBPromptShowing )
				{	
					isFBPromptShowing = true;
					Logger.i(TAG,"showing fblogin prompt");	
					popUpView = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.fbloginpromptpopup, null);					
					fbPopupWindow = new PopupWindow(popUpView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false); //Creation of popup					
					fbPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);   
					fbPopupWindow.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);    // Displaying popup							
			        fbPopupWindow.setTouchable(true);
			        fbPopupWindow.setFocusable(false);
			        //fbPopupWindow.setOutsideTouchable(true);
			        ViewGroup fbloginlayout = (ViewGroup)popUpView.findViewById(R.id.fbloginpromptloginlayout);
			        fbloginlayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							FBLoginDialog_show(underlyingActivity);
							fbPopupWindow.dismiss();
							isFBPromptShowing = false;	
							HopinTracker.sendEvent("FacebookLoginpromptPopup","ButtonClick",underlyingActivity.getClass().toString()+":fblogin_popup:click:fblogin",1L);
							Logger.i(TAG,"fblogin prompt clicked");
						}
					});
			        ImageView buttonClosefbprompt = (ImageView) popUpView.findViewById(R.id.fbloginpromptclose);		        
			        buttonClosefbprompt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							HopinTracker.sendEvent("FacebookLoginpromptPopup","ButtonClick",underlyingActivity.getClass().toString()+":fblogin_popup:click:close",1L);
							fbPopupWindow.dismiss();
							isFBPromptShowing = false;	
						}
					});
				}
				else
				{
					//will flicker prompt here if already showing
					TextView fblogintext = (TextView) popUpView.findViewById(R.id.fbloginprompttext);
					Animation anim = new AlphaAnimation(0.0f, 1.0f);
			        anim.setDuration(50); //You can manage the time of the blink with this parameter
			        anim.setStartOffset(20);
			        anim.setRepeatMode(Animation.REVERSE);
			        anim.setRepeatCount(6);
			        fblogintext.startAnimation(anim);				
				}
				//popUpView.setBackgroundResource(R.drawable.transparent_black);
			}
			else
			{
				if(isFBPromptShowing && fbPopupWindow!=null && fbPopupWindow.isShowing())
					fbPopupWindow.dismiss();
				isFBPromptShowing = false;
			}
							
		}

	public void authorizeCallback(FragmentActivity underlaying_activity,int requestCode, int resultCode, Intent data) {
		FacebookConnector.getInstance(underlaying_activity).authorizeCallback(requestCode, resultCode, data);
		
	}
		
	

}
