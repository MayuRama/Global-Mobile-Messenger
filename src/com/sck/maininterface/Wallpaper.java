package com.sck.maininterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sep.global_welcome.MainActivity;
import com.sep.global_welcome.enlageImageActivity;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Wallpaper extends Activity {
	
	//private static int RESULT_LOAD_IMAGE = 1;
	//private static int REQUEST_CAMERA = 1;
	private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
	public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "wallpapers";
	
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
	// new part 1
	public static SharedPreferences pref;
	public static Editor editor;
	public static String pp="";
	String  foo = null;
	private Context context;
	// end of new part 1
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		captureImageInitialization();
		dialog.show();
	//	
	}
	
	@TargetApi(16)
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode != RESULT_OK)
	               return;

	        switch (requestCode) {
	        case PICK_FROM_CAMERA:
	        	Log.d("PICK_FROM_CAMERA", "entered to case 1");
	        	if(data!= null){
	        		
	        	}
	                /*
	        	 Bundle extras = data.getExtras();
	                // After cropping the image, get the bitmap of the cropped image and
	                // display it on imageview.
	               if (extras != null) {
	                     Bitmap photo = extras.getParcelable("data");
	                     saveImageToExternalStorage(photo);
	                     
	               }
	               File f = new File(mImageCaptureUri.getPath());
	                // Delete the temporary image
	               if (f.exists())
	                     f.delete();
	        	*/
	               break;

	        case PICK_FROM_FILE:
	                // After selecting image from files, save the selected path
	               mImageCaptureUri = data.getData();
	               Log.d("uri", mImageCaptureUri.toString());
	               try{
	               Bitmap bm = BitmapFactory.decodeStream(
	            	       getContentResolver().openInputStream(mImageCaptureUri));
	               saveImageToExternalStorage(bm);
	               }catch(Exception e){
	                Log.e("error",e.toString());
	               }
	               break;

	        case CROP_FROM_CAMERA:
	        		/*
	               Bundle extras = data.getExtras();
	                // After cropping the image, get the bitmap of the cropped image and
	                // display it on imageview.
	               if (extras != null) {
	                     Bitmap photo = extras.getParcelable("return-data");
	                     saveImageToExternalStorage(photo);
	                     
	               }
					*/
	               //File f = new File(mImageCaptureUri.getPath());
	                // Delete the temporary imag
				
	               break;
	               

	        }
	        Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
	        Log.d("intent", "redirect to msg activity");
	        startActivity(ii);
	        finish();
	 }
	
	private void captureImageInitialization() {
        
        // a selector dialog to display two image source options, from camera
        // ‘Take from camera’ and from existing files ‘Select from gallery’
        
       //final String[] items = new String[] { "Take from camera",
       //             "Select from gallery", "Get Global seasonal wallpaper", "Remove wallpaer" };
       final String[] items = new String[] {
               "Select from gallery", "Get Global seasonal wallpaper", "Remove wallpaer" };
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, items);
       AlertDialog.Builder builder = new AlertDialog.Builder(this);

       builder.setTitle("Select Wallpaper");
       builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int item) { // pick from camera
            	  /*          
                    if (item == 0) {
                           
                            // To take a photo from camera, pass intent action
                            // ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
                            
                           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"wallpaper.jpg"));
                           intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                         mImageCaptureUri);

                           try {
                                  intent.putExtra("return-data", true);
                        	   	 
                                  startActivityForResult(intent, PICK_FROM_CAMERA);
                           } catch (ActivityNotFoundException e) {
                                  e.printStackTrace();
                           }
                           /////////////////////
                           String root = Environment.getExternalStorageDirectory().toString();
                           new File(root + "/photofolder").mkdirs();
                           File outputfile=new File(root+"/photofolder/", "wallpaper.jpg");
                           Uri outputFileUri = Uri.fromFile(outputfile);
                           Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                           cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                           startActivityForResult(cameraIntent, PICK_FROM_CAMERA); 
                           /////////////////////
                            
                    }*/
                     if(item==0){
                           /*
                             To select an image from existing files, 
                             use Intent.createChooser to open image chooser. Android will
                             automatically display a list of supported applications,
                             such as image gallery or file manager. */
                           Intent intent = new Intent();

                           intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                           startActivityForResult(Intent.createChooser(intent,
                                         "Complete action using"), PICK_FROM_FILE);
                          
                    }else if(item == 1){
                    	CheckConnection cc = new CheckConnection(getApplicationContext());
                    	Boolean isInternetPresent = cc.isConnectingToInternet();
                    	if(isInternetPresent){
                    	DownloadTask downloadTask = new DownloadTask();
                    	downloadTask.execute();
                    	}else{
                    		showAlertDialog(Wallpaper.this, "No Internet Connection",
                                    "You don't have internet connection.", false);
                    		
                    		
                    	}
                    }else{
                    	String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
                    	File wallPath = new File(fullPath+"/wallpaper.jpg");
                    	if(wallPath.exists()){
                    		wallPath.delete();
                    		Log.d("pass", "wallpaper removed");
                    		Toast.makeText(getApplicationContext(), "Wallpaper Removed", Toast.LENGTH_LONG).show();
                    	}
                    	Log.d("pass", fullPath+"/wallpaper.jpg");
                    	Log.d("not exist", "no wallpaper set");
                    	Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
            	        Log.d("intent", "redirect to msg activity");
            	        startActivity(ii);
            	        finish();
                    }
              }
       });

       dialog = builder.create();
	}
	
	private class DownloadTask extends AsyncTask<Void, Integer, Bitmap> {
		private ProgressDialog Dialog = new ProgressDialog(Wallpaper.this);
		
		@Override  
        protected void onPreExecute()  
        {  
        	Dialog.setMessage("Downloaing wallpaper...");
            Dialog.show();
        }
		
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			String url = "http://sepwecom.preview.kyrondesign.com/phpserver/wallpaper/wallpaper.jpg";
			Bitmap bitmap = null;
			try{
				bitmap = downloadUrl(url);
				saveImageToExternalStorage(bitmap);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
				//bitmap = null;
			}
			return bitmap;
		}
		@Override
        protected void onPostExecute(Bitmap result) {
			Dialog.dismiss();
			try{
				
				if(result == null){
					Intent iii = new Intent(getApplicationContext(),MessageActivity.class);
			        Log.d("intent with extras", "redirect to msg activity");
			        iii.putExtra("msg", "No downloadable wallpapers set");
			        startActivity(iii);
			        finish();
				}else{
					Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
			        Log.d("intent", "redirect to msg activity");
			        startActivity(ii);
			        finish();
				}
			}catch(Exception e){
				Log.e("error",e.toString());
			}
   
        }
		
	}

	
	public boolean saveImageToExternalStorage(Bitmap image) {
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
				//dir.getParentFile().mkdirs();
				//dir.createNewFile();
			}
		//dir.createNewFile();
		OutputStream fOut = null;
		//imageName = read(file);
		File file = new File(fullPath, "wallpaper.jpg");
		Log.d("pass", file+"file is directory"+"wallpaper.jpg");
		file.createNewFile();
		
		//.getParentFile().createNewFile();
		Log.d("pass", "file createtd");
		fOut = new FileOutputStream(file);
		
		// 100 means no compression, the lower you go, the stronger the compression
		image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();

		MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

		return true;

		} catch (Exception e) {
			Log.e("saveToExternalStorage()", e.getMessage());
			return false;
		}
	}
	
	public boolean isSdReadable() {
		
		try{
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = true;
			Log.i("isSdReadable", "External storage card is readable.");
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				Log.i("isSdReadable", "External storage card is readable.");
				mExternalStorageAvailable = true;
			} else {
				// Something else is wrong. It may be one of many other
				// states, but all we need to know is we can neither read nor write
				mExternalStorageAvailable = false;
		}

			return mExternalStorageAvailable;
		}catch(Exception e){
			Log.e("error in checking", e.toString());
		}
		return false;
	}

	//download userImage uri
		@SuppressWarnings("unused")
		private Bitmap downloadUrl(String strUrl) throws IOException{
	        Bitmap bitmap=null;
	        InputStream iStream = null;
	        try{
	            URL url = new URL(strUrl);
	            // Creating an http connection to communcate with url 
	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	            urlConnection.connect();
	            //Reading data from url
	            iStream = urlConnection.getInputStream();
	            //Creating a bitmap from the stream returned from the url
	            bitmap = BitmapFactory.decodeStream(iStream);
	        }catch(Exception e){
	            Log.d("Exception while downloading url", e.toString());
	        }finally{
	            iStream.close();
	        }
	        
	        return bitmap;
	    }
		
		@SuppressWarnings("deprecation")
		public void showAlertDialog(Context context, String title, String message, Boolean status) {
	        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	 
	        // Setting Dialog Title
	        alertDialog.setTitle(title);
	 
	        // Setting Dialog Message
	        alertDialog.setMessage(message);
	         
	        // Setting alert dialog icon
	        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
	 
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
        	        Log.d("intent", "redirect to msg activity");
        	        startActivity(ii);
        	        finish();
	            }
	        });
	 
	        // Showing Alert Message
	        alertDialog.show();
	    }

	
	
}
