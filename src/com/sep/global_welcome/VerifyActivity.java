package com.sep.global_welcome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sck.maininterface.CheckConnection;
import com.sck.maininterface.R;
import com.sep.buyphonenumber.BuyPhoneMainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VerifyActivity extends Activity {
	//ActionBar actionBar;
	EditText verifyCode;
	Button verifyBtn;
	private String file = "mydata";
	private String filePhoneNo = "myphoneNo";
	private String data, phonedata;
	InputStream is=null;
	public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "wallpapers";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify_number);
		
		//actionBar=getActionBar(); 
	   // actionBar.setTitle("Enter the recieved code");
	    //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	      
	    
	    
	    
		verifyBtn = (Button) findViewById(R.id.btnVerify);
		verifyCode = (EditText) findViewById(R.id.txtVerify);
		
		verifyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckConnection cc = new CheckConnection(getApplicationContext());
		     	Boolean isInternetPresent = cc.isConnectingToInternet();
		     	if(isInternetPresent){
		     		Bundle extras = getIntent().getExtras();
					if(extras!=null){
						String code = extras.getString("verifyCode");
						String text = verifyCode.getText().toString();
						
						String phoneNo = extras.getString("phone");
						
						
						//Toast.makeText(getApplicationContext(), "Code:"+code, Toast.LENGTH_LONG).show();
						
						if(code.equals(text)){
							//Toast.makeText(getApplicationContext(), "Code Matches", Toast.LENGTH_LONG).show();
							save(text, phoneNo);
							
							//send data to server database - phone number
							//insert(phoneNo);

							new insertAsync().execute();
							createDir();
					     	
							/*Intent ii = new Intent(getApplicationContext(),MainActivity.class);
							ii.putExtra("phone", phoneNo);
							startActivity(ii);*/
							
							Intent buyphoneno = new Intent(getApplicationContext(),BuyPhoneMainActivity.class);
							startActivity(buyphoneno);
							finish();
						}
						else{
							Toast.makeText(getApplicationContext(), "Invalid Code", Toast.LENGTH_LONG).show();
						}
					}
		     	}else{
		     		showAlertDialog(VerifyActivity.this, "No Internet Connection", "You don't have internet connection.", false);
		     	}
				 
				
			}
		});
		
		
	}
	
	public void createDir(){
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
				//dir.getParentFile().mkdirs();
				//dir.createNewFile();
				Log.d("pass","dir created");
			}
			
		}catch(Exception e){
			Log.d("dir cannot create",e.toString());	
		}
		
	}
	
	private class insertAsync extends AsyncTask<Void, String, Void> {
		
	     protected Void doInBackground(String... params) {
	    	
	    	 return null;
	     }

		@Override
		protected Void doInBackground(Void... arg0) {
			 Bundle extras = getIntent().getExtras();
				if(extras!=null){
					String phoneNo = extras.getString("phone");
				
	    	 
	    	 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 		nameValuePairs.add(new BasicNameValuePair("phone",phoneNo));
	    	 try{
					HttpClient httpclient = new DefaultHttpClient();
					//HttpPost httppost = new HttpPost("http://kasunbuddhima.net63.net/phpserver/insertuser.php");
					HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/insertuser.php");	
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
			        Log.e("pass 1", "connection success ");
			        
					
				}catch(Exception e){
					Log.e("Fail 1", e.toString());
			    	Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
				}
				}
			return null;
		}     
	 }
	
	
	public void insert(String phone){
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("phone",phone));
		
			try{
				HttpClient httpclient = new DefaultHttpClient();
				//HttpPost httppost = new HttpPost("http://kasunbuddhima.net63.net/phpserver/insertuser.php");
				HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/insertuser.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
		        Log.e("pass 1", "connection success ");
		        
				
			}catch(Exception e){
				Log.e("Fail 1", e.toString());
		    	Toast.makeText(getApplicationContext(), e.toString(),
				Toast.LENGTH_LONG).show();
			}
	}
	
	
	
	//to save the code
	public void save(String s, String phone){
	      data = s;
	      phonedata = phone;
	      try {
	         FileOutputStream fOut = openFileOutput(file,MODE_WORLD_READABLE);
	         fOut.write(data.getBytes());
	         fOut.close();
	         
	         ////////////////////////////////////////////////////////////
	         FileOutputStream fOut1 = openFileOutput(filePhoneNo,MODE_WORLD_READABLE);
	         fOut1.write(phonedata.getBytes());
	         fOut1.close();
	         Log.d("Success","File saved");
	      } catch (Exception e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
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
            	Intent ii = new Intent(getApplicationContext(),ContactInforActivity.class);
    	        Log.d("intent", "redirect to msg activity");
    	        startActivity(ii);
    	        finish();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
	

}
