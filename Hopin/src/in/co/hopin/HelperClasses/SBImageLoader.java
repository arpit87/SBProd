package in.co.hopin.HelperClasses;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SBImageLoader {
	
	private final String TAG = "in.co.hopin.HelperClasses.SBImageLoader";
	private static  ImageLoader imageLoader;	
	private static SBImageLoader instance = null;

	  public static SBImageLoader getInstance()
	  {
		  if(instance == null)
		  {
			  instance = new SBImageLoader();
			  ImageLoaderConfiguration config  =  ImageLoaderConfiguration.createDefault(Platform.getInstance().getContext());
			  imageLoader = ImageLoader.getInstance();
			  imageLoader.init(config);
		  }
		  
		  return instance;
	  }
	 
	  public void displayImage(String paramString, ImageView paramImageView)
	  {		  
		  imageLoader.displayImage(paramString, paramImageView);
	  }
	  
	  public void displayImageElseStub(String imageURL, ImageView imageView, int stubResource)
	  {		  
		  DisplayImageOptions options = new DisplayImageOptions.Builder()
		    .showStubImage(stubResource)
            .showImageForEmptyUri(stubResource)
		    .cacheInMemory()
		    .build();
		  imageLoader.displayImage(imageURL, imageView,options);
	  }
	  
	  public void displayCoverImage(final String fbid,final ImageView paramImageView, final View progressView)
	  {
		  	Session session = Session.getActiveSession();
		  	if(session == null)
		  		return;
	    	if (session.isOpened()) {
	    		Logger.d(TAG, "sesion is open");
	    		String graphPath = StringUtils.getFBCoverPicGraphPathFromFBID(fbid);
	    		Logger.d(TAG, "graphpath:"+graphPath);
	    		//Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);
	    		Bundle params = new Bundle();
	    		params.putString("fields", "cover");
	    		Request req = new Request(session, graphPath, params, HttpMethod.GET,new Request.Callback() {
					
					@Override
					public void onCompleted(Response response) {		
						if(response == null)
							return;
						GraphObject gObj = response.getGraphObject();
						if(gObj==null)
							return;
						JSONObject json = gObj.getInnerJSONObject();
						Logger.d(TAG, "got json :"+json.toString());
						try {
							String mCoverPicURL = json.getJSONObject("cover").getString("source");
							Logger.d(TAG, "got url :"+mCoverPicURL);
							imageLoader.displayImage(mCoverPicURL, paramImageView);
							if(progressView!=null)
								progressView.setVisibility(View.GONE);
						} catch (JSONException e) {
							Logger.d(TAG, "got json exception ");
							if(progressView!=null)
								progressView.setVisibility(View.GONE);
							return;
						}				
						
					}
				});
	    		req.executeAsync();

	        } else {
	            Logger.e(TAG, "fb session not open");
	            if(progressView!=null)
					progressView.setVisibility(View.GONE);
	            
	        }		  
		
	  }
	  
	  public void displayImage(String url, ImageView view, DisplayImageOptions options)
	  {
		  imageLoader.displayImage(url, view, options);
	  }

}
