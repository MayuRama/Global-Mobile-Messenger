package com.sck.maininterface;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.sep.global_welcome.enlageImageActivity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ContactUs1 extends Activity{
	ActionBar actionBar;
	private Button faqBtn;
	private EditText txtQuestion;
	private ImageView sendImage1, sendImage2, sendImage3;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private int number;
	public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "emailScreenshots";
    private Context context;
    private String image1,image2,image3;
    private String file2 = "myphoneNo";
    static String netType = "";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		
		actionBar=getActionBar(); 
		actionBar.setTitle("Contact Us");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	    
	    faqBtn = (Button) findViewById(R.id.btnfaq);
	    faqBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW, 
			             Uri.parse("http://sepwecom.preview.kyrondesign.com/phpserver/customer/faq.php"));
				startActivity(i);
				
			}
		});
	    txtQuestion = (EditText) findViewById(R.id.txtquestion);
	    sendImage1 = (ImageView) findViewById(R.id.imageView1);
	    sendImage2 = (ImageView) findViewById(R.id.imageView2);
	    sendImage3 = (ImageView) findViewById(R.id.imageView3);
	    
	    sendImage1.setOnClickListener(new View.OnClickListener() {

	    	  @Override
	    	  public void onClick(View view) {
	    		  number = 1;
	    		  Intent intent = new Intent();
                  intent.setType("image/*");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent,
                          "Select Picture"), SELECT_PICTURE);
	    	  }

	    	});
	    sendImage2.setOnClickListener(new View.OnClickListener() {

	    	  @Override
	    	  public void onClick(View view) {
	    	    number = 2;
	    	    Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
	    	  }

	    	});
	    sendImage3.setOnClickListener(new View.OnClickListener() {

	    	  @Override
	    	  public void onClick(View view) {
	    		  number = 3;
	    		  Intent intent = new Intent();
                  intent.setType("image/*");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent,
                          "Select Picture"), SELECT_PICTURE);
	    	  }

	    	});
	  
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                
                try{
                Bitmap bm = BitmapFactory.decodeStream(
	            	       getContentResolver().openInputStream(selectedImageUri));
                boolean x = false;
                //load images to correspond image view which selected by the user
                if(number == 1){
	                sendImage1.setImageBitmap(bm);
	                image1 = selectedImagePath;
	                x=saveImageToExternalStorage(bm);
                }else if(number == 2){
                	sendImage2.setImageBitmap(bm);
                	image2 = selectedImagePath;
                	x=saveImageToExternalStorage(bm);
                }else if(number == 3){
                	sendImage3.setImageBitmap(bm);
                	image3 = selectedImagePath;
                	x=saveImageToExternalStorage(bm);
                }else{
                	Log.d("image selection problem", "number variable is not set");
                	if(x==true)
                		x=false;
                }
                
                }catch(Exception e){
                	Log.e("bitmap warnning", e.toString());
                }
            }
        }
    }

	
    // helper to retrieve the path of an image URI 
    public String getPath(Uri uri) {
            // just some safety built in 
            if( uri == null ) {
                // TODO perform some logging or show user feedback
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            @SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            // this is our fallback here
            return uri.getPath();
    }
    
    //save loaded screenshots
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
		if(number == 1){
			File file = new File(fullPath, "screen1.jpg");
			Log.d("pass", file+"file is directory"+"screen1.jpg");
			file.createNewFile();
			
			//.getParentFile().createNewFile();
			Log.d("pass", "file createtd");
			fOut = new FileOutputStream(file);
			
			// 100 means no compression, the lower you go, the stronger the compression
			image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
	
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
		}else if(number == 2){
			File file = new File(fullPath, "screen2.jpg");
			Log.d("pass", file+"file is directory"+"screen2.jpg");
			file.createNewFile();
			
			//.getParentFile().createNewFile();
			Log.d("pass", "file createtd");
			fOut = new FileOutputStream(file);
			
			// 100 means no compression, the lower you go, the stronger the compression
			image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();

			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
			
			
		}else if(number == 3){
			File file = new File(fullPath, "screen3.jpg");
			Log.d("pass", file+"file is directory"+"screen3.jpg");
			file.createNewFile();
			
			//.getParentFile().createNewFile();
			Log.d("pass", "file createtd");
			fOut = new FileOutputStream(file);
			
			// 100 means no compression, the lower you go, the stronger the compression
			image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();

			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
		}else{
			Log.d("number == null", "no number assigned");
		}
		return true;

		} catch (Exception e) {
			Log.e("saveToExternalStorage()", e.getMessage());
			return false;
		}
	}
	//check whether the external storage readability
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
    //////////////////////////////////////////////////////////////////////////////////
	//send mail to administrator with question details
    public void sendMail(){
    	String address = "info@sepwecom.preview.kyrondesign.com";
    	String subject = "Customer Question about Global";
    	String message = txtQuestion.getText().toString();
    	String emailAddresses[] = { address };
    	//get the sim card information
    	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	String operatorName = telephonyManager.getNetworkOperatorName();
    	String deviceName = android.os.Build.MODEL;
    	String deviceMan = android.os.Build.MANUFACTURER;
    	
    	ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    	//mobile
    	State mobile = conMan.getNetworkInfo(0).getState();

    	//wifi
    	State wifi = conMan.getNetworkInfo(1).getState();
    	if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
    	    netType = "Mobile Network connection";
    	} else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
    		netType = "WiFi connection";
    	}
    	Log.d("net type", netType);
    	String phone = read(file2);
    	String details = message+"\n\n--Support information--\n"+"Registered No: +"+phone+"\nDevice name: "+deviceName+"\nDevice Manufacturer: "+deviceMan
    			+"\nNetwork Service Provider: "+operatorName+"\nNetwork type: "+netType;
    	
    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, details);
    	Uri img1,img2,img3;
    	ArrayList<Uri> uris = new ArrayList<Uri>();
    	//load images to email if images are added by user
    	if(image1!=null){
    		img1 = Uri.parse("file://" +image1);
    		Log.d("image1", image1);
    		uris.add(img1);
    		//emailIntent.putExtra(Intent.EXTRA_STREAM, img1); //this can send only one image
    	}
    	if(image2!=null){
    		img2 = Uri.parse("file://" +image2);
    		Log.d("image2", image2);
    		uris.add(img2);
    	}
    	if(image3!=null){
    		img3 = Uri.parse("file://" +image3);
    		Log.d("image3", image3);
    		uris.add(img3);
    	}
    	if(uris!=null){
    	emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);	//load all the selected images to email app
    	}
    	startActivity(emailIntent);
    }
  
    
	//get the saved file names
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
  	            	Intent ii = new Intent(getApplicationContext(),ContactUs1.class);
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
			CheckConnection cc = new CheckConnection(getApplicationContext());
	     	Boolean isInternetPresent = cc.isConnectingToInternet();
	     	if(isInternetPresent){
			sendMail();
	     	}else{
	     		showAlertDialog(ContactUs1.this, "No Internet Connection", "You don't have internet connection.", false);
	     	}
			
		//finish();
			break;
		
		default:
			return super.onOptionsItemSelected(item);
			
		}
		return false;
    	
    }
    

}
