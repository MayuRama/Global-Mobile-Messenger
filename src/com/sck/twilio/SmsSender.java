package com.sck.twilio;

import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;





import com.sck.maininterface.Main;
import com.sck.maininterface.MessageActivity;
import com.sck.maininterface.R;
import com.sck.twilio.JSONParser;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsSender extends Activity {

	// Progress Dialog
    private ProgressDialog pDialog;
    
   private static String Senderno="";
    
    
 
    JSONParser jsonParser = new JSONParser();
   
    String fromNumber="+12086470713";
 
    // url to create new SMS
    //private static String url_create_sms = "http://10.0.2.2/twiliosmssend/create_sms.php";
    //working url 
    //private static String url_send_sms = "http://10.0.2.2/twiliosmssend/sendnotifications.php";
    private static String url_send_sms = "http://sepwecom.preview.t10.info/twiliosmssend/sendnotifications.php";
    //private static String url_send_sms = "http://kasunbuddhima.net63.net/phpserver/twiliosmssend/sendnotifications.php";
    
    //private static String url_create_product = "http://aspenk.netne.net/twiliosmssend/create_sms.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_sms_sender);
        
     // Edit Text
        //Senders no (twilion no)
       // fromNumber = (EditText) findViewById(R.id.inputName);
        //my no
      //  toNumber = (EditText) findViewById(R.id.inputPrice);
        //smsBody = Message_Body;
        
        if (Main.CONTACT_NUMBER.startsWith("07"))
        {
        	String trim=Main.CONTACT_NUMBER.substring(0, 2);
        	Senderno="+947"+trim;
        	//Toast.makeText(SmsSender.this, Senderno, Toast.LENGTH_LONG).show();
        }
        else
        {
        	Toast.makeText(SmsSender.this, "Cannot send meesages to that particular number", Toast.LENGTH_LONG).show();
        	
        }
 
       
        
    }

    //Background Async Task to Create new SMS
    //This class allows to perform background operations and publish results on the UI thread 
    //without having to manipulate threads and/or handlers.
    //AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)), and 
    //most often will override a second one (onPostExecute(Result).)
    public class CreateNewSMS extends AsyncTask<String, String, String> {
 
        // Before starting background thread Show Progress Dialog
        // onPreExecute - Runs on the UI thread after publishProgress(Progress...) is invoked. 
    	// publishProgress(Progress...) - This method can be invoked from doInBackground(Params...) to publish updates on the 
    	// UI thread while the background computation is still running. 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SmsSender.this);
            pDialog.setMessage("Sending SMS..");
            pDialog.setIndeterminate(false); // Change the indeterminate mode for this progress bar.
            //A progress bar can also be made indeterminate. In indeterminate mode, the progress bar shows a cyclic animation 
            //without an indication of progress. This mode is used by applications when the length of the task is unknown. 
            //The indeterminate progress bar can be either a spinning wheel or a horizontal bar.
            pDialog.setCancelable(true);
            //pDialog.show();
        }
 
        // Creating SMS
        //Override this method to perform a computation on a background thread. The specified parameters are 
        //the parameters passed to execute(Params...) by the caller of this task. This method can call publishProgress(Progress...) 
        //to publish updates on the UI thread.
        protected String doInBackground(String... args) {
           // String fromphoneno = fromNumber.getText().toString();
        	
           // String tophoneno = toNumber.getText().toString();
            //String smsbody = smsBody.getText().toString();
        	
        	// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fromphoneno", fromNumber));
            params.add(new BasicNameValuePair("tophoneno", Senderno));
            params.add(new BasicNameValuePair("smsbody", MessageActivity.MESSSAGE_CONTENT));
            Toast.makeText(SmsSender.this, fromNumber +","+ Main.CONTACT_NUMBER+","+ MessageActivity.MESSSAGE_CONTENT , Toast.LENGTH_LONG).show();
            // getting JSON Object
            // Note that create sms url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_send_sms, "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
         // getInt - Returns the value mapped by parameter if it exists and is an int or can be coerced to an int.
         // TAG_SUCCESS is JSON Node names created above
            int success;
			try {
				success = json.getInt(TAG_SUCCESS);
				
				 if (success == 1) {
		            	SmsSender.this.runOnUiThread(new Runnable() {
		                	  public void run() {
		                	    Toast.makeText(SmsSender.this, "SMS is Sent", Toast.LENGTH_SHORT).show();
		                	  }
		                	});
		            } else {
		                // failed to create sms
		            	SmsSender.this.runOnUiThread(new Runnable() {
		              	  public void run() {
		              	    Toast.makeText(SmsSender.this, "SMS is NOT Sent", Toast.LENGTH_SHORT).show();
		              	  }
		              	});
		            }
				 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            
           
            return null;
        }
 
        
          //After completing background task Dismiss the progress dialog
         
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
    
    
    
}
