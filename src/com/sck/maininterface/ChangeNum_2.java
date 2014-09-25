package com.sck.maininterface;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.sep.global_welcome.MainActivity;



import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeNum_2 extends Activity{

	ActionBar actionBar;
	TelephonyManager tel;
	private EditText oldCounCode, newCounCode, newPhone, oldPhone;
	private String file2 = "myphoneNo";
	private String fileNewPhoneNo = "myNewPhoneNo";
	final Context context = this;
	StringBuilder sb;
	static String result="";
	static JSONObject json_data = null;
	String part2;
	String name;
	String line=null;
	boolean val;
	String newP;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_num2);
		
		actionBar=getActionBar(); 
		actionBar.setTitle("Change Number");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));  
	    
	    tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		final String counCode = tel.getSimCountryIso().toString();
		Log.d("country code", counCode);
		String phone = read(file2);	//get the saved phone number from file
		Log.d("phone",phone);
		String numbers = phone.substring(Math.max(0, phone.length() - 9));  //extract the last 9 digits
		Log.d("splitted",numbers);
		String[] parts = phone.split(numbers);	//split entire number by last 9 digits to get country code
		oldCounCode = (EditText) findViewById(R.id.txtOldcounCode);
		oldCounCode.setText(parts[0]);
		newCounCode = (EditText) findViewById(R.id.txtNewcounCode);
		newCounCode.setText(parts[0]);
		
		oldPhone = (EditText) findViewById(R.id.txtOldPhone);
		newPhone = (EditText) findViewById(R.id.txtNewPhone);
	}
	
	public boolean changePhoneNum(){
		CheckConnection cc = new CheckConnection(getApplicationContext());
     	Boolean isInternetPresent = cc.isConnectingToInternet(); //check Internet connection availability
     	String oldCode,newCode,oldNum,newNum;
     	oldCode = oldCounCode.getText().toString();
     	newCode = newCounCode.getText().toString();
     	oldNum = oldPhone.getText().toString();
     	newNum = newPhone.getText().toString();
     	
     	Pattern pattern = Pattern.compile("\\d{9}");
     	Matcher matcher1 = pattern.matcher(oldNum);	//check whether user entered 9 digits number
     	Matcher matcher2 = pattern.matcher(newNum);	//check whether user entered 9 digits number
     	if(isInternetPresent){
     		if(oldCode.equals("")||newCode.equals("")||oldNum.equals("")||newNum.equals("")){
     			Toast.makeText(getApplicationContext(), "Please enter valid numbers for all fields", Toast.LENGTH_SHORT).show();
     			return false;
     		}else if(matcher1.matches() && matcher2.matches()){
     			String oldP = oldCode + oldNum;
     			newP = newCode + newNum;
     			String phone = read(file2);	//get the saved phone number from file
     			if(!oldP.equals(phone)){
     				Toast.makeText(getApplicationContext(), "Please provide your correct old phone number", Toast.LENGTH_SHORT).show();
         			return false;
     				
     			}
				// create an alert dialog    
                 
                boolean val1 = getState(oldP,newP);	//generate the alert dialog to confirm the generated code
     			
     			
     			return val1;
     			
     		}else{
     			Toast.makeText(getApplicationContext(), "Please enter valid phone numbers", Toast.LENGTH_SHORT).show();
     			return false;
     		}
     	}else{
     		showAlertDialog(ChangeNum_2.this, "No Internet Connection", "You don't have internet connection.", false);
     		return false;
     	}
	}
	//generate alert dialog for code verification for the new number
	public boolean getState(final String oldP, final String newP){

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.activity_verify_new_num, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Enter verification code");
		// set activity_verify_new_num.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);
		final EditText input = (EditText) promptView.findViewById(R.id.txtVerify);
			
		Random random = new Random();
		int value = random.nextInt(10000);
		final String v = Integer.toString(value);
		Log.d("generated code", v);
		sendMsg(newP,v);	//send verify code to new phone number
		// setup a dialog window to get the verify code 
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
            public void onClick(DialogInterface dialog, int id) {
                // get user input and set it to result
                String enteredCode = input.getText().toString();
                if(enteredCode.equals(v)){
                	new changePhoneNumAsync().execute(oldP,newP);
         			save(newP);
         			val = true;
                }else{
                	Toast.makeText(getApplicationContext(), "code Doesn't match. Couldn't change number", Toast.LENGTH_SHORT).show();
                	
                }
            }
        });

		alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		            }
		        });
		
		AlertDialog alertD = alertDialogBuilder.create();
		alertD.show();
		
		return val;
	}
	
	protected void sendMsg(String phone,String code){
		String mobile = "+" + phone;
		String Verifyno = code;
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(mobile, null, Verifyno, null, null);
			
			//Toast.makeText(getApplicationContext(), "SMS sent.",
					//Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	//add new phone number to server
	private class changePhoneNumAsync extends AsyncTask<String, String, String>{
		private ProgressDialog Dialog = new ProgressDialog(ChangeNum_2.this);
		
		protected void onPreExecute() {
	    	 Dialog.setMessage("updating number...");
	         Dialog.show();
	     }
		protected String doInBackground(String... nums ){
			InputStream is = null;
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String oldNum = nums[0];
			String newNum = nums[1];
			nameValuePairs.add(new BasicNameValuePair("oldphone",oldNum));
			Log.d("old phone",oldNum);
			Log.d("new phone",newNum);
			nameValuePairs.add(new BasicNameValuePair("newphone",newNum));
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/changeUserPhone.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.v("log_tag", "In the try Loop" );
			
			}catch(Exception e){
				Log.e("Async task error1", e.toString());
			}
			
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	          	sb = new StringBuilder();
	          	while ((line = reader.readLine()) != null)
	             {	 
	        	    sb.append(line + "\n");
	             }
	             is.close();
	             result = sb.toString().substring(0, sb.toString().length()-1);
	             Log.e("pass 2", "connection success ");
	             String[] parts = result.split(">");
	             part2 = parts[1];
	             Log.e("pass 2", part2);
			}catch(Exception e){
				Log.e("Async task error2", e.toString());
			}
			 
			try{
				json_data = new JSONObject(part2);
				Log.e("pass 3", "Entered to try block");
	         	name = (json_data.getString("code"));
	         	Log.e("pass 3", "Code: " + name);
			}catch(Exception e){
	         	Log.e("Fail 3", e.toString());
	     	}
			
			return name;
		}
		 @Override  
	        protected void onPostExecute(String result)  
	        {    
	            Dialog.dismiss();
	            //initialize the text 
	            oldPhone = (EditText) findViewById(R.id.txtOldPhone);
	    		newPhone = (EditText) findViewById(R.id.txtNewPhone);
	    		oldPhone.setText("");
	    		newPhone.setText("");
	    		Log.d("code in onPostExecute()", result);
	    		if(result.equals("1")){
	    			showAlertDialog(ChangeNum_2.this, "Successfully updated","You have Scccessfully change the number to: +"+newP, true);
	    		}else{
	    			showAlertDialog(ChangeNum_2.this, "Couldn't update!", "Phone number cannot be updated.", false);
	    		}
	        }
	}
	
	
	//save new phone number to the file.
	public void save(String newphone){
		String nPhone = newphone;
		try{
			FileOutputStream fOut1 = openFileOutput(fileNewPhoneNo,MODE_WORLD_READABLE);
	         fOut1.write(nPhone.getBytes());
	         fOut1.close();
	         Log.d("Success","File saved");
		}catch(Exception e){
			Log.e("file save error", e.toString());
		}
	}
	
	//read the old phone number
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
	
	//display Internet not available dialog
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
            	Intent ii = new Intent(getApplicationContext(),Account.class);
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
			if(changePhoneNum()){
		startActivity(new Intent(ChangeNum_2.this, Account.class));
		finish();
			}else{
				Log.d("false", "return false from changePhoneNum()");
			}
			break;
		
		default:
			return super.onOptionsItemSelected(item);
			
		}
		return false;
    	
    }
	
}


