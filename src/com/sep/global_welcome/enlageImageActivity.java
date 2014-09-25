package com.sep.global_welcome;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sck.maininterface.CheckConnection;
import com.sck.maininterface.MessageActivity;
import com.sck.maininterface.R;





import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

//import com.example.profile.MainActivity;

public class enlageImageActivity extends Activity{
	
	ImageView imageEnlarged;
	Button editProImageButton;
	//private static int RESULT_LOAD_IMAGE = 1;
	private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    
    String imageName = "";
    private Context context;
    
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
    String file = "imagenamefile";
    private String file2 = "myphoneNo";
   // private int serverResponseCode = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enlarge_pro_image);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Change profile picture");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
		// Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        
		captureImageInitialization();
		
		//imageEnlarged = (ImageView) findViewById(R.id.enlargedImage);
		String imagename = read(file);
		MainActivity ma = new MainActivity();
		try{
		Bitmap bit = ma.getThumbnail(imagename);
		if(bit != null){
			imageEnlarged = (ImageView) findViewById(R.id.enlargedImage);
			imageEnlarged.setImageBitmap(bit);
		
		}
		}catch(Exception e){
			Log.e("bitmap exception", e.toString());
		}
		
		editProImageButton = (Button) findViewById(R.id.button1);
		
		editProImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				CheckConnection cc = new CheckConnection(getApplicationContext());
		     	Boolean isInternetPresent = cc.isConnectingToInternet();
		     	if(isInternetPresent){
				dialog.show();
		     	}else{
		     		showAlertDialog(enlageImageActivity.this, "No Internet Connection", "You don't have internet connection.", false);
		     	}
				 
				//Intent i = new Intent(getApplicationContext(), MainActivity.class);
				//i.putExtra("", value);
				//startActivity(i);
                
			}
		});
		
	}
	//////////////////////////////////////////
	//upload image to server
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////
	//upload profile image to server
	private class uploadProImgAsync extends AsyncTask<Bitmap, Integer, String>{
	
		private ProgressDialog Dialog = new ProgressDialog(enlageImageActivity.this);
		
		@Override  
		protected void onPreExecute()  
		{  
			Dialog.setMessage("Uploading Image...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(Bitmap... bit) {
			Bitmap bitmap = bit[0];
			InputStream is;
			BitmapFactory.Options bfo;
			Bitmap bitmapOrg;
			ByteArrayOutputStream bao ;
			
			bfo = new BitmapFactory.Options();
			bfo.inSampleSize = 2;
			
			bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte [] ba = bao.toByteArray();
			String ba1 = Base64.encodeBytes(ba);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			String phoneNo = read(file2);
			nameValuePairs.add(new BasicNameValuePair("image",ba1));
			nameValuePairs.add(new BasicNameValuePair("cmd",phoneNo+".jpg"));
			Log.v("log_tag", System.currentTimeMillis()+".jpg");
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new 
				//HttpPost("http://www.kasunbuddhima.net63.net/phpserver/Profiles/uploadProImage.php");
				HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/Profiles/uploadProImage.php");		
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.v("log_tag", "In the try Loop" );
			}catch(Exception e){
				Log.v("log_tag", "Error in http connection "+e.toString());
			}
			
			return "Success";
		}
		
		protected void onPostExecute(String result) {
			Dialog.dismiss();
			String phoneNo = read(file2);
			//String URl = "http://www.kasunbuddhima.net63.net/phpserver/Profiles/"+phoneNo+".jpg";
			String URl = "http://sepwecom.preview.kyrondesign.com/phpserver/Profiles/"+phoneNo+".jpg";
			DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URl);
		}
	
	}
	//////////////////////
	private class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
		private ProgressDialog Dialog = new ProgressDialog(enlageImageActivity.this);
		
		@Override  
        protected void onPreExecute()  
        {  
        	Dialog.setMessage("Loading data...");
            Dialog.show();
        }
		@Override
		protected Bitmap doInBackground(String... uri) {
			String url = uri[0];
			Bitmap bitmap = null;
			try{
                bitmap = downloadUrl(url);
                Log.d("Background Task","pass downloadUrl()");
               
                saveImageToExternalStorage(bitmap);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return bitmap;
            
			//return null;
		}
		@Override
        protected void onPostExecute(Bitmap result) {
			Dialog.dismiss();
           // ImageView iView = (ImageView) findViewById(R.id.imageView1);
           // iView.setImageBitmap(result);
        }
		
	}
	//download userImage uri
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
	/////////////////////////////////////////
	
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
		imageName = read(file);
		File file = new File(fullPath, imageName);
		Log.d("pass", file+"file is directory"+imageName);
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
	
	public Bitmap getThumbnail(String filename) {
		
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
		Bitmap thumbnail = null;
		
		// Look for the file on the external storage
		try {
		if (isSdReadable() == true) {
		thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
		}
		} catch (Exception e) {
		Log.e("getThumbnail() on external storage", e.getMessage());
		}
		
		// If no file on external storage, look in internal storage
		if (thumbnail == null) {
		try {
		File filePath = context.getFileStreamPath(filename);
		FileInputStream fi = new FileInputStream(filePath);
		thumbnail = BitmapFactory.decodeStream(fi);
		} catch (Exception ex) {
		Log.e("getThumbnail() on internal storage", ex.getMessage());
		}
		}
		
		return thumbnail;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	

	
	
	private void captureImageInitialization() {
        
         // a selector dialog to display two image source options, from camera
         // ‘Take from camera’ and from existing files ‘Select from gallery’
         
        final String[] items = new String[] { "Take from camera",
                     "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                     android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int item) { // pick from camera
                               
                     if (item == 0) {
                            
                             // To take a photo from camera, pass intent action
                             // ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
                             
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            
                             // Also specify the Uri to save the image on specified path
                             // and file name. Note that this Uri variable also used by
                             // gallery app to hold the selected image path.
                             
                            mImageCaptureUri = Uri.fromFile(new File(Environment
                                          .getExternalStorageDirectory(), "tmp_avatar_"
                                          + String.valueOf(System.currentTimeMillis())
                                          + ".jpg"));
                           

                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                          mImageCaptureUri);

                            try {
                                   intent.putExtra("return-data", true);

                                   startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (ActivityNotFoundException e) {
                                   e.printStackTrace();
                            }
                     } else {
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
                     }
               }
        });

        dialog = builder.create();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
               super(context, R.layout.crop_selector, options);

               mOptions = options;

               mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
               if (convertView == null)
                     convertView = mInflater.inflate(R.layout.crop_selector, null);

               CropOption item = mOptions.get(position);

               if (item != null) {
                     ((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
                     //((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);

                     return convertView;
               }

               return null;
        }
	}

	 public class CropOption {
	        public CharSequence title;
	        public Drawable icon;
	        public Intent appIntent;
	 }

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode != RESULT_OK)
	               return;
	
	        switch (requestCode) {
	        case PICK_FROM_CAMERA:
	                // After taking a picture, do the crop
	               doCrop();
	
	               break;
	
	        case PICK_FROM_FILE:
	                // After selecting image from files, save the selected path
	               mImageCaptureUri = data.getData();
	               doCrop();
	               break;
	
	        case CROP_FROM_CAMERA:
	               Bundle extras = data.getExtras();
	                // After cropping the image, get the bitmap of the cropped image and
	                // display it on imageview.
	               if (extras != null) {
	                     Bitmap photo = extras.getParcelable("data");
	                     new uploadProImgAsync().execute(photo);
	                    imageEnlarged.setImageBitmap(photo);
	               }
	
	               File f = new File(mImageCaptureUri.getPath());
	                // Delete the temporary image
	               if (f.exists())
	                     f.delete();
	
	               break;
	
	        }
	 }

	 private void doCrop() {
	        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
	         // Open image crop app by starting an intent
	         // ‘com.android.camera.action.CROP‘.
	        Intent intent = new Intent("com.android.camera.action.CROP");
	        intent.setType("image/*");
	
	         // Check if there is image cropper app installed.
	        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
	
	        int size = list.size();
	
	         // If there is no image cropper app, display warning message
	        if (size == 0) {
	
	               Toast.makeText(this, "Can not find image crop app",
	                            Toast.LENGTH_SHORT).show();
	
	               return;
	        } else {
	                // Specify the image path, crop dimension and scale
	        	
	               intent.setData(mImageCaptureUri);
	               
	               intent.putExtra("outputX", 200);
	               intent.putExtra("outputY", 200);
	               intent.putExtra("aspectX", 1);
	               intent.putExtra("aspectY", 1);
	               intent.putExtra("scale", true);
	               intent.putExtra("return-data", true);
	               
	                // There is posibility when more than one image cropper app exist,
	                // so we have to check for it first. If there is only one app, open then app.
	               if (size == 1) {
	                     Intent i = new Intent(intent);
	                     ResolveInfo res = list.get(0);
	
	                     i.setComponent(new ComponentName(res.activityInfo.packageName,
	                                   res.activityInfo.name));
	
	                     startActivityForResult(i, CROP_FROM_CAMERA);
	               } else {
	                      // If there are several app exist, create a custom chooser to
	                      // let user selects the app.
	                     for (ResolveInfo res : list) {
	                            final CropOption co = new CropOption();
	
	                            co.title = getPackageManager().getApplicationLabel(
	                                          res.activityInfo.applicationInfo);
	                            co.icon = getPackageManager().getApplicationIcon(
	                                          res.activityInfo.applicationInfo);
	                            co.appIntent = new Intent(intent);
	
	                            co.appIntent
	                                          .setComponent(new ComponentName(
	                                                        res.activityInfo.packageName,
	                                                        res.activityInfo.name));
	
	                            cropOptions.add(co);
	                     }
	
	                     CropOptionAdapter adapter = new CropOptionAdapter(
	                                   getApplicationContext(), cropOptions);
	
	                     AlertDialog.Builder builder = new AlertDialog.Builder(this);
	                     builder.setTitle("Choose Crop App");
	                     builder.setAdapter(adapter,
	                                   new DialogInterface.OnClickListener() {
	                                          public void onClick(DialogInterface dialog, int item) {
	                                                 startActivityForResult(
	                                                               cropOptions.get(item).appIntent,
	                                                               CROP_FROM_CAMERA);
	                                          }
	                                   });
	
	                     builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	                            @Override
	                            public void onCancel(DialogInterface dialog) {
	
	                                   if (mImageCaptureUri != null) {
	                                          getContentResolver().delete(mImageCaptureUri, null,
	                                                        null);
	                                          mImageCaptureUri = null;
	                                   }
	                            }
	                     });
	
	                     AlertDialog alert = builder.create();
	
	                     alert.show();
	               }
	        }
	 }
	//to read the image name
	 public String read(String file){
	     try{
	        FileInputStream fin = openFileInput(file);
	        int c;
	        String temp="";
	        while( (c = fin.read()) != -1){
	           temp = temp + Character.toString((char)c);
	        }
	        //et.setText(temp);
	       
	        return temp;
	     }catch(Exception e){
	
	     }
	     return "";
	 }
	 
	//display Internet not available alert dialog
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
