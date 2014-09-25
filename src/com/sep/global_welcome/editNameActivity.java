package com.sep.global_welcome;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import com.sck.maininterface.Main;
import com.sck.maininterface.MessageActivity;
import com.sck.maininterface.R;
import com.sck.maininterface.Settings;
import com.sep.global_welcome.MainActivity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class editNameActivity extends Activity {
	
	EditText editname;
	InputStream is=null;
	String line=null;
	String result=null;	
    StringBuilder sb;
    static JSONObject json_data = null;
    int code;
    String name;
    String part2;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		 
	    actionBar.setTitle("Change Name");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	      
		// Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        
		setContentView(R.layout.edit_name);
		
		//ImageButton editOk = (ImageButton) findViewById(R.id.editNameOk);
		//ImageButton editCancel = (ImageButton) findViewById(R.id.editNameCancel);
		editname = (EditText) findViewById(R.id.editText1);
		
		//Get the String from values add set in the editName TextBox(strings.xml)
		//String username = getResources().getString(R.string.visible_name);
		//editname.setText(username);
		//get the user name from the server
		CheckConnection cc = new CheckConnection(getApplicationContext());
     	Boolean isInternetPresent = cc.isConnectingToInternet();
     	if(isInternetPresent){
		new getuserNameAsync().execute();
     	}else{
     		showAlertDialog(editNameActivity.this, "No Internet Connection", "You don't have internet connection.", false);
	        
     	}
		
		//editOk, save the string of the text and return back to profile page
		/*editOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = ((EditText)findViewById(R.id.editText1)).getText().toString();
			//Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
				//update name on the server
				new updateNameAsync().execute();
				
				 Bundle extras = getIntent().getExtras();
					if(extras!=null){
						String phoneNo = extras.getString("phone");
				
						//String MyName = null;
						//page redirection to profile page
						Intent ii = new Intent(getApplicationContext(),MainActivity.class);
						//send the text name to redirection page
						ii.putExtra("MyName", name);
						ii.putExtra("phone", phoneNo);
						startActivity(ii);
						finish();
					}
				
			}
		});*/
		
	/*	//editCancel return back to the profile page
		editCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle extras = getIntent().getExtras();
				if(extras!=null){
					String phoneNo = extras.getString("phone");
					Intent returntoProfile = new Intent(getApplicationContext(),MainActivity.class);
					returntoProfile.putExtra("phone", phoneNo);
					startActivity(returntoProfile);
					finish();
				}
			}
		});*/
	}

	//get the user name from the server if user is already registered.
		private class getuserNameAsync extends AsyncTask<Void, String, Void> {
			private ProgressDialog Dialog = new ProgressDialog(editNameActivity.this);
			EditText editname = (EditText) findViewById(R.id.editText1);
			
		     protected Void doInBackground(String... params) {
		    	
		    	 return null;
		     }
		     protected void onPreExecute() {
		    	 Dialog.setMessage("Loading data...");
		         Dialog.show();
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
								//HttpPost httppost = new HttpPost("http://kasunbuddhima.net63.net/phpserver/getuserName.php");
								HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/getuserName.php");
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
				    	 try
				         	{
				          	BufferedReader reader = new BufferedReader
				 				(new InputStreamReader(is,"iso-8859-1"),8);
				             	StringBuilder sb = new StringBuilder();
				             	while ((line = reader.readLine()) != null)
				             	{
				        		    sb.append(line + "\n");
				            	}
				             	is.close();
				             	result = sb.toString();
				             	String[] parts = result.split(">");
					            part2 = parts[1];
				             	Log.e("pass 2", "connection success ");
				         	}
				         catch(Exception e)
					     {
					 		Log.e("Fail 2", e.toString());
					     } 
				    	 try
					     	{
					    		 //Log.e("Fail 3", result);
					    		 
					         	 json_data = new JSONObject(part2);
					         	
					         	Log.e("pass 3", "Entered to try block");
					         	name = (json_data.getString("name"));
					         	Log.e("pass 3", "Name: " + name);
					         	
					         	
					         	
					     	}
					         catch(Exception e)
					     	{
					         	Log.e("Fail 3", e.toString());
					     	} 
				    	 
				    	 
					}
				return null;
			}
			
			
			//after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {    
	            Dialog.dismiss();
	            //initialize the text  
	            editname.setText(name);
	        }
		 }
		
		//update user name
		private class updateNameAsync extends AsyncTask<Void, Integer, Void> {
			
		     protected Void doInBackground(String... params) {
		    	
		    	 return null;
		     }
		  
		     
			@Override
			protected Void doInBackground(Void... arg0) {
				
				 Bundle extras = getIntent().getExtras();
					if(extras!=null){
						String phoneNo = extras.getString("phone");
					
						String name = ((EditText)findViewById(R.id.editText1)).getText().toString();
						
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("phone",phoneNo));
						nameValuePairs.add(new BasicNameValuePair("username", name));
		 		
		    	 try{
						HttpClient httpclient = new DefaultHttpClient();
						//HttpPost httppost = new HttpPost("http://kasunbuddhima.net63.net/phpserver/editName.php");
						HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/editName.php");
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						is = entity.getContent();
				        Log.e("pass 1", "connection success ");
				        Log.e("pass 1", "user:"+ name);
				        Log.e("pass 1", "phone:"+ phoneNo);
						
				}catch(Exception e){
						Log.e("Fail 1", e.toString());
				    	Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
					}
		    	 
		    	 try
		         {
		     		BufferedReader reader = new BufferedReader
		 				(new InputStreamReader(is,"iso-8859-1"),8);
		             	StringBuilder sb = new StringBuilder();
		             	while ((line = reader.readLine()) != null)
		             	{
		                     sb.append(line + "\n");
		             	}
		             	is.close();
		             	result = sb.toString();
		             	 String[] parts = result.split(">");
			             part2 = parts[1];
		             	Log.e("pass 2", "connection success ");
		         }
		         catch(Exception e)
		     	 {
		         	 	Log.e("Fail 2", e.toString());
		     	 }     
		        
		    	try
		     	{
		    		// Log.e("Fail 3", part2);
		         	JSONObject json_data = new JSONObject(part2);
		         	code=(json_data.getInt("code"));
		 			
		         	
		     	}
		         catch(Exception e)
		     	{
		         	Log.e("Fail 3", e.toString());
		     	}
		    	 
			}
			return null;
				
			}     
			/*
			//Update the progress  
	        @Override  
	        protected void onProgressUpdate(Integer... values)  
	        {  
	            //set the current progress of the progress dialog  
	            progressDialog.setProgress(values[0]);  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {  
	        	 
	            //close the progress dialog  
	            progressDialog.dismiss();  
	            //initialize the View  
	            setContentView(R.layout.activity_main);  
	        }  
	        */
	        
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
	       
	    	getMenuInflater().inflate(R.menu.edit_nam_actions, menu);
	    	return super.onCreateOptionsMenu(menu);
	    }
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
			case R.id.action_next:
				String name = ((EditText)findViewById(R.id.editText1)).getText().toString();
				//Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
					//update name on the server
					new updateNameAsync().execute();
					
					 Bundle extras = getIntent().getExtras();
						if(extras!=null){
							String phoneNo = extras.getString("phone");
					
							//String MyName = null;
							//page redirection to profile page
							Intent ii = new Intent(getApplicationContext(),MainActivity.class);
							//send the text name to redirection page
							ii.putExtra("MyName", name);
							ii.putExtra("phone", phoneNo);
							startActivity(ii);
							finish();
						}
				break;
			
			default:
				return super.onOptionsItemSelected(item);
				
			}
			return false;
	    	
	    }
}
