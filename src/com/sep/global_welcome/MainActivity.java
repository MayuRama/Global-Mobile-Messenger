package com.sep.global_welcome;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
import org.json.JSONObject;

import com.sck.maininterface.CheckConnection;
import com.sck.maininterface.GplusLogin;
import com.sck.maininterface.Main;
import com.sck.maininterface.MessageActivity;
import com.sck.maininterface.R;
import com.sck.maininterface.Settings;
import com.sck.maininterface.Wallpaper;




import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ActionBar actionBar;
	protected Dialog mSplashDialog;
	Button contact;
	ImageView profileImg;
	ImageButton editName;
	EditText visibleName;
	Button editImage,btnGooglePlus;
	InputStream is=null;
	InputStream is2=null;
	InputStream is3=null;
	String line=null;
	String line2=null;
	String line3=null;
	static String result="";
	static String result2="";
	static String result3="";
	String URl;
	 String part2;
	 String imagepart;
	 String expDate;
	//private static int RESULT_LOAD_IMAGE = 1;
	private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";

    
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
    String name;
    String imageName = "";
    String appExpdate = "";
    StringBuilder sb;
    StringBuilder sb2;
    StringBuilder sb3;
    static JSONObject json_data = null;
    private Context context;
	
    private String file = "imagenamefile";
    private String file2 = "myphoneNo";
    private String file3 = "expdate";
    String data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		captureImageInitialization();
		//////
		//TODO
		//Make the connection to the inbox from this
		//create an action bar icon (arrow)
		///////////
		actionBar=getActionBar(); 
	       actionBar.setTitle("Profile");
	       actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
		
		//String userName = getResources().getString(R.string.visible_name);
		//visibleName.setText(userName);		
		 profileImg = (ImageView) findViewById(R.id.imageView1);
		 visibleName = (EditText) findViewById(R.id.editText1);
		 editImage = (Button) findViewById(R.id.button1);
		 btnGooglePlus =(Button) findViewById(R.id.btnGooglePlus);
		// if(bit != null){
		//	 editImage.setImageBitmap(bit);
		// }
		 //enlarge image by click on the image
		 profileImg.setOnClickListener(new OnClickListener() {
			 //@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(),enlageImageActivity.class);
				startActivity(ii);
			}
		});
		 
		 btnGooglePlus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				
					startActivity(new Intent(getApplicationContext(),GplusLogin.class));
				}
			});
			
		 
		// visibleName.setText(getString(R.string.visible_name)); 
		 Bundle extras = getIntent().getExtras();
		 String MyName;	
		 String phoneis = read(file2);
		 //get the passed name from the editnameactivity.class
		// if(extras!=null){
		 //		MyName = extras.getString("MyName");
		 		
		 		//visibleName.setText(MyName);
		 		//final String phoneNo = extras.getString("phone");
		 		final String phoneNo = read(file2);
		 		//check phone no
		 		//Toast.makeText(getApplicationContext(), "phone: "+ phoneNo, Toast.LENGTH_LONG).show();
				 //edit visible name 
				 editName = (ImageButton) findViewById(R.id.imageButton1);
				 editName.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//page redirection to edit name page
						Intent i = new Intent(getApplicationContext(), editNameActivity.class);
						i.putExtra("phone", phoneNo);
						startActivity(i);
						//finish();
					}
				});
		// }
		 
		 //edit the image 
		 editImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.show();	//AltertDialog object
				
			}
		});
		 //get user name and user image name 
		CheckConnection cc = new CheckConnection(getApplicationContext());
     	Boolean isInternetPresent = cc.isConnectingToInternet();
     	if(isInternetPresent){
		new getuserNameAsync().execute();
     	}else{
     		showAlertDialog(MainActivity.this, "No Internet Connection", "You don't have internet connection.", false);
     	}
	}
	//get the user name from the server if user is already registered.
	private class getuserNameAsync extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		EditText visibleName = (EditText) findViewById(R.id.editText1);
		CheckRegistration ck = new CheckRegistration();
		//Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	Dialog.setMessage("Loading data...");
            Dialog.show();
        }
        	
		@Override
		protected Void doInBackground(Void... arg0) {
		
			Bundle extras = getIntent().getExtras();
			//CheckRegistration ck = new CheckRegistration();		
			String phoneNo = read(file2);
		
			//if(extras!=null){
				//String phoneNo = extras.getString("phone");
	    	 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 		nameValuePairs.add(new BasicNameValuePair("phone",phoneNo));
	    	 try{
					HttpClient httpclient = new DefaultHttpClient();
					//HttpPost httppost = new HttpPost("http://kasunbuddhima.net63.net/phpserver/getuserName.php");
					HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/getuserName.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
			        Log.e("pass 1", "connection success ");
			       // Toast.makeText(getApplicationContext(), "phone: "+ phoneNo, Toast.LENGTH_LONG).show();
			        //to getUSerImage
			        //HttpPost httppost2 = new HttpPost("http://kasunbuddhima.net63.net/phpserver/getUserImageName.php");
			        HttpPost httppost2 = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/getUserImageName.php");
			        httppost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response2 = httpclient.execute(httppost2);
					HttpEntity entity2 = response2.getEntity();
					is2 = entity2.getContent();
					
					//to get the expiration date
					HttpPost httppost3 = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/getExpDate.php");
					httppost3.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response3 = httpclient.execute(httppost3);
					HttpEntity entity3 = response3.getEntity();
					is3 = entity3.getContent();
				}catch(Exception e){
					Log.e("Fail 1", e.toString());
			    	Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
				}
	    	 try
	         {
	    		 
	          	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	          	sb = new StringBuilder();
	            //sb.append(reader.readLine() + "\n"); 	
	             while ((line = reader.readLine()) != null)
	             {	 
	        	    sb.append(line + "\n");
	             }
	             is.close();	
	            // result = sb.toString();
	             result = sb.toString().substring(0, sb.toString().length()-1);
	             Log.e("pass 2 result", result);
	             Log.e("pass 2", "connection success ");
	             String[] parts = result.split(">");
	             part2 = parts[1];
	             Log.e("pass 2", part2);
	             /////////////////////////////////////////////////////////////
	             BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2,"iso-8859-1"),8);
	             sb2 = new StringBuilder();
	             while ((line2 = reader2.readLine()) != null)
	             {	 
	        	    sb2.append(line2 + "\n");
	             }
	             is2.close();
	             result2 = sb2.toString().substring(0, sb2.toString().length()-1);
	             String[] parts2 = result2.split(">");
	             imagepart = parts2[1];
	             Log.e("pass 2", imagepart);
	             
	             ////////////////////////////////////////////////////////////
	             //get expiration date part 2
	             BufferedReader reader3 = new BufferedReader(new InputStreamReader(is3,"iso-8859-1"),8);
	             sb3 = new StringBuilder();
	             while ((line3 = reader3.readLine()) != null)
	             {	 
	        	    sb3.append(line3 + "\n");
	             }
	             is3.close();
	             result3 = sb3.toString().substring(0, sb3.toString().length()-1);
	             String[] parts3 = result3.split(">");
	             expDate = parts3[1];
	             Log.e("pass 2 expDate", expDate);
	         }
	         catch(Exception e){
	        	 Log.e("Fail 2", e.toString());
	 		
	     	 }     
	    	 
	    	 try
	     	{
	    		
	         	 json_data = new JSONObject(part2);
	         	
	         	Log.e("pass 3", "Entered to try block");
	         	name = (json_data.getString("name"));
	         	Log.e("pass 3", "Name: " + name);
	         	////////////////////////////////////////////////////
	         	//get image name
	         	json_data = new JSONObject(imagepart);
	         	imageName = (json_data.getString("name"));
	         	Log.e("pass 3", "got the image name");
	         	
	         	///////////////////////////////////////////////////
	         	//get expiration date
	         	json_data = new JSONObject(expDate);
	         	appExpdate = (json_data.getString("expdate"));
	         	Log.e("pass 3", "got the date:" + appExpdate);
	     	}
	         catch(Exception e){
	         	Log.e("Fail 3", e.toString());
	     	} 
		//	}
			return null;	
		}  
		
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {     
            Dialog.dismiss();
            //initialize the text  
            visibleName.setText(name); 
            //URl = "http://www.kasunbuddhima.net63.net/phpserver/Profiles/"+imageName;
            URl = "http://sepwecom.preview.t10.info/phpserver/Profiles/"+imageName;
            //new getImage().execute(URl);
            save(imageName);
            saveExpDate(appExpdate);
            try{
   			 	Bitmap bit = getThumbnail(imageName);
   			 	if(bit != null){
   			 		ImageView iView = (ImageView) findViewById(R.id.imageView1);
   			 		iView.setImageBitmap(bit);
   			 	}
   			 }catch(Exception e){
   				 Log.e("bitmap exception", e.toString());
   				//ImageView iView = (ImageView) findViewById(R.id.imageView1);
			 	//iView.setBackgroundResource(R.drawable.no_user_a);
	   			DownloadTask downloadTask = new DownloadTask();
	            downloadTask.execute(URl);
   			 }
        }  
	 }
	
	private class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		
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
                Log.d("bitmap downloaded",bitmap.toString());
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
            ImageView iView = (ImageView) findViewById(R.id.imageView1);
            iView.setImageBitmap(result);
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
	///////////////////////////////////////////////////////////////////////////////////////////
	//upload profile image to server
	private class uploadProImgAsync extends AsyncTask<Bitmap, Integer, String>{

		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		
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
			//URl = "http://www.kasunbuddhima.net63.net/phpserver/Profiles/"+phoneNo+".jpg";
			URl = "http://sepwecom.preview.kyrondesign.com/phpserver/Profiles/"+phoneNo+".jpg";
			DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URl);
		}
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////
	public boolean saveImageToExternalStorage(Bitmap image) {
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
			dir.mkdirs();
		}

		OutputStream fOut = null;
		File file = new File(fullPath, imageName);
		file.createNewFile();
		fOut = new FileOutputStream(file);
		Log.d("pass", file+"file is directory and i name"+imageName);

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
	
	
	////////////////////////////////////////////////////////////
	 //a selector dialog to display two image source options, from camera
     //Take from camera’ and from existing files ‘Select from gallery’
	private void captureImageInitialization() {
         
        final String[] items = new String[] { "Take from camera","Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int item) { // pick from camera
                               
                     if (item == 0) {
                    	 //take photo from camera
                    	 //'MediaStore.ACTION_IMAGE_CAPTURE' will open the device camera app
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                          /*
                            Also specify the Uri to save the image on specified path
                            and file name. Note that this Uri variable also used by
                            gallery app to hold the selected image path.
                            */
                            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_"
                                          + String.valueOf(System.currentTimeMillis())+ ".jpg"));

                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,mImageCaptureUri);

                            try {
                                   intent.putExtra("return-data", true);
                                   startActivityForResult(intent, PICK_FROM_CAMERA);
                                   
                            } catch (ActivityNotFoundException e) {
                                   e.printStackTrace();
                            }
                     } else if (item == 1) {
                    	 //select image from device memory by accessing to the galary
                    	 //Intent.createChooser this will open image chooser. then android automatically display supported apps.
                            Intent intent = new Intent();

                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
                     }else{
                    	 Toast.makeText(getApplicationContext(), "You haven't select a Picture", Toast.LENGTH_LONG).show();
                     }
               }
        });

        dialog = builder.create();
	}
	///////////////////////////////////////////////////////////////////////////
	
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
	                // taking a picture, do the crop
	               doCrop();
	
	               break;
	
	        case PICK_FROM_FILE:
	                //After selecting image from files, save the selected path 
	               mImageCaptureUri = data.getData();
	               doCrop();
	               break;
	
	        case CROP_FROM_CAMERA:
	               Bundle extras = data.getExtras();
	                 //After cropping the image, get the bitmap of the cropped image and
	                 //display it on imageview.
	               if (extras != null) {
	                     Bitmap photo = extras.getParcelable("data");
	                     //upload selected profile image to server
	                     new uploadProImgAsync().execute(photo);	
	                     profileImg.setImageBitmap(photo);
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
	         
	        if (size == 0) {
	        	// If there is no image cropper app, display warning message
	               Toast.makeText(this, "Can not find image crop app",Toast.LENGTH_SHORT).show();
	
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
	               
	                 //There is posibility when more than one image cropper app exist, 
	               //so we have to check for it first. If there is only one app, open then app.
	               if (size == 1) {
	                     Intent i = new Intent(intent);
	                     ResolveInfo res = list.get(0);
	
	                     i.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
	
	                     startActivityForResult(i, CROP_FROM_CAMERA);
	               } else {
	                     
	                      // If there are several app exist, create a custom chooser to
	                      // let user selects the app. 
	                     for (ResolveInfo res : list) {
	                            final CropOption co = new CropOption();	//create an object from the CropOption class
	
	                            co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
	                            co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
	                            co.appIntent = new Intent(intent);
	
	                            co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
	
	                            cropOptions.add(co);
	                     }
	                     
	                     
	                     //create a dialog to select the crop app
	                     CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
	
	                     AlertDialog.Builder builder = new AlertDialog.Builder(this);
	                     builder.setTitle("Choose Crop App");
	                     builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
	                             public void onClick(DialogInterface dialog, int item) {
	                                      startActivityForResult(cropOptions.get(item).appIntent,CROP_FROM_CAMERA);
	                                          }
	                                   });
	
	                     builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	                            @Override
	                            public void onCancel(DialogInterface dialog) {
	
	                                   if (mImageCaptureUri != null) {
	                                          getContentResolver().delete(mImageCaptureUri, null,null);
	                                          mImageCaptureUri = null;
	                                   }
	                            }
	                     });
		                     AlertDialog alert = builder.create();
	
	                     alert.show();
	               }
	        }
	 }
	 
	//to save the code
		@SuppressWarnings("deprecation")
		public void save(String s){
		      data = s;
		      try {
		         FileOutputStream fOut = openFileOutput(file,MODE_WORLD_READABLE);
		         fOut.write(data.getBytes());
		         fOut.close();
		         Log.w("file saved", "image name is saved in imagenamefine");
		        // Toast.makeText(getBaseContext(),"file saved",Toast.LENGTH_SHORT).show()
		         
		         
		      } catch (Exception e) {
		         // TODO Auto-generated catch block
		         e.printStackTrace();
		      }
		}
		//to save the code
				@SuppressWarnings("deprecation")
				public void saveExpDate(String s){
				      data = s;
				      try {
				         
				         FileOutputStream fOut2 = openFileOutput(file3,MODE_WORLD_READABLE);
				         fOut2.write(data.getBytes());
				         fOut2.close();
				         Log.w("file saved", "image name is saved in expDatefile");
				         
				         
				      } catch (Exception e) {
				         // TODO Auto-generated catch block
				         e.printStackTrace();
				      }
				}
		
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
		    	  	Log.e("Read Error", e.toString());
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

	
		  @Override
		    public boolean onCreateOptionsMenu(Menu menu) {
		       
		    	getMenuInflater().inflate(R.menu.signup_end_acions, menu);
		    	return super.onCreateOptionsMenu(menu);
		    }
		    
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		    	switch (item.getItemId()) {
				case R.id.action_next:
				startActivity(new Intent(MainActivity.this, Main.class));
				finish();
					break;
				
				default:
					return super.onOptionsItemSelected(item);
					
				}
				return false;
		    	
		    }

}
