package in.co.hopin.HelperClasses;

import in.co.hopin.Platform.Platform;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Store {
	
	private static final String TAG = "in.co.hopin.HelperClasses.Store";
	private Context context = Platform.getInstance().getContext();
	private static Store instance = null;
	
	public static Store getInstance()
	{
		if(instance == null)
			instance = new Store();
		return instance;
		
	}
	
	public void saveBitmapToFile(Bitmap bm,String filename)
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"saving bitmap to file");
	try
	{
		if(bm!=null && filename!=null)
			{				
				FileOutputStream fos = context.openFileOutput(filename, context.MODE_PRIVATE);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();			
			}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"Bitmap,filename is null!!");
        }
	}
	catch(Exception e)
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG,"Error saving bitmap"+e);
		e.printStackTrace();
	}
	}
	
	public Bitmap getBitmapFromFile(String filename)
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"getting bitmap from file");
		Bitmap bitmap = null;
		try
		{
			String filePath = context.getFilesDir().getAbsolutePath()+"/"+filename;
			File f = new File(filePath);
			if(f.exists())
				{					
					bitmap = BitmapFactory.decodeFile(filePath);				
				}
			else {
				if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"file doesnt exist!!");
            }
		}
		catch(Exception e)
		{
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG,"Error fetching bitmap"+e);
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	public void deleteFile(String filename)
	{
		try
		{
			String filePath = context.getFilesDir().getAbsolutePath()+"/"+filename;
			File f = new File(filePath);
			if(f.exists())
				{					
					f.delete();				
				}
			else {
				if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"file doesnt exist!!");
            }
		}
		catch(Exception e)
		{
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG,"Error fetching bitmap"+e);
			e.printStackTrace();
		}
	}
}


