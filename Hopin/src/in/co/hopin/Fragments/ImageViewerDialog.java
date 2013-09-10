package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Activities.MyChatsActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.DailyCarPoolRequest;
import in.co.hopin.HttpClient.InstaRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ImageViewerDialog extends DialogFragment{
	
	//private static final String TAG = "in.co.hopin.Fragments.ShowActiveReqPrompt";
	ImageView pic = null;
	ProgressBar progressBar = null;
	static String mImgURL = "";
	
	public static ImageViewerDialog newInstance(String imgURL)
	{
		ImageViewerDialog thisDialog = new ImageViewerDialog();
		mImgURL = imgURL;
		return thisDialog;
	
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.imageviewer, container);
       	pic = (ImageView) dialogView.findViewById(R.id.imageviewer_img);
       	progressBar = (ProgressBar) dialogView.findViewById(R.id.imageviewer_progressbar);
       	SBImageLoader.getInstance().displayImageElseStub(mImgURL, pic, R.id.imageviewer_progressbar);
		return dialogView;
	}
	
	  @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendEvent("ImageViewerDialog","ScreenOpen","showactivereqprompt:open",1L);
	    }
	
	
}
