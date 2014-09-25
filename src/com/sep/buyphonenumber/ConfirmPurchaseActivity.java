package com.sep.buyphonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sep.buyphonenumber.DisplayNumbersActivity.SendCountryAndGetNumbers;
import com.sep.global_welcome.MainActivity;
import com.sep.global_welcome.VerifyActivity;
import com.sck.maininterface.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmPurchaseActivity extends Activity {

	//kasun's
	private String file = "mydata";
	private String filePhoneNo = "myphoneNo";
	private String data, phonedata;
	
	// for json request
	// Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    
    ArrayList<HashMap<String, String>> phoneNumberList;
   
    // URL to find phone numbers list
    private static String url_send_sms = "http://sepwecom.preview.kyrondesign.com/twiliosmssend/buyactualnumber.php";
    
 // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SID = "sid";
    private static final String TAG_CONFIRMCODES = "confirmcodes";
    
    
 // numbers JSONArray
    JSONArray confirmCodesArray = null;
    
	String selectedPhoneNo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j_activity_confirm_purchase);
		
		Intent getCountry = getIntent();
		selectedPhoneNo = getCountry.getStringExtra(DisplayNumbersActivity.EX_SELECTEDNUMBER);
		
		TextView message = (TextView) findViewById(R.id.tvMessage);
		TextView number = (TextView) findViewById(R.id.textView6);
		
		message.setText("Are you sure, you want to buy this " + selectedPhoneNo + " Number?");
		number.setText(selectedPhoneNo);
		
		// Hashmap for ListView
        phoneNumberList = new ArrayList<HashMap<String, String>>();
        
        
        
		Button confirm = (Button) findViewById(R.id.btnConfirm);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "Number Purchased",Toast.LENGTH_SHORT).show();
				// executing asynchronous task
			       new SendNumberAndGetConfirm().execute();
			       
			       MainActivity ma = new MainActivity();
			       String phoneNo = ma.read(filePhoneNo);
			       
			       Intent ii = new Intent(getApplicationContext(),MainActivity.class);
					ii.putExtra("phone", phoneNo);
					startActivity(ii);
					finish();
				
			}
		});
	}
	
	//Background Async Task to Send Phone number and get Confirmation Code
    class SendNumberAndGetConfirm extends AsyncTask<String, String, String> {
 
        // Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ConfirmPurchaseActivity.this);
            pDialog.setMessage("Sending Phone Number Data...");
            pDialog.setIndeterminate(false); //In indeterminate mode, the progress bar shows a cyclic animation 
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        //Sending data and Getting Number list in Background Thread
        //The specified parameters are the parameters passed to execute(Params...) by the caller of this task. 
        protected String doInBackground(String... args) {
            
        	// Building Parameters
        	// NameValuePair - This class comforms to the generic grammar and formatting rules outlined in 
        	//the Section 2.2 and Section 3.6 of RFC 2616 
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("selectedPhoneNo", selectedPhoneNo));
            
            // getting JSON Object
            // Note that above url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_send_sms, "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            // getInt - Returns the value mapped by parameter if it exists and is an int or can be coerced to an int.
            // TAG_SUCCESS is JSON Node names created above
            int success;
			try {
				success = json.getInt(TAG_SUCCESS);
				
				 if (success == 1) {
					 
					
	                // Getting Array of Phone Numbers
					 confirmCodesArray = json.getJSONArray(TAG_CONFIRMCODES);
					 
					// looping through All Products
	                    for (int i = 0; i < confirmCodesArray.length(); i++) {
	                        JSONObject c = confirmCodesArray.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        final String sid = c.getString(TAG_SID);
	                        
	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        map.put(TAG_SID, sid);
	                        
	 	                    // adding HashList to ArrayList
	                        phoneNumberList.add(map);
	                        
	                        ConfirmPurchaseActivity.this.runOnUiThread(new Runnable() {
	  		              	  public void run() {
	  		              	    Toast.makeText(ConfirmPurchaseActivity.this, "Phone Numbers Purchased " + sid , Toast.LENGTH_SHORT).show();
	  		              	  }
	  		              	});
	                    }
	                
		            } else {
		                // no data to download
		            	ConfirmPurchaseActivity.this.runOnUiThread(new Runnable() {
		              	  public void run() {
		              	    Toast.makeText(ConfirmPurchaseActivity.this, "Phone Numbers NOT Purchased", Toast.LENGTH_SHORT).show();
		              	  }
		              	});
		            }
				 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            
           
            return null;
        }
                 
         
        protected void onPostExecute(String file_url) {
        	//After completing background task Dismiss the progress dialog
            pDialog.dismiss();
            
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    /*ListAdapter adapter = new SimpleAdapter(
                            DisplayNumbersActivity.this, phoneNumberList,
                            R.layout.list_item, new String[] {TAG_PHONENUMBER},
                            new int[] {R.id.name});
                    // updating listview
                    setListAdapter(adapter);*/
                }
            });
            
        }
 
    }
    

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirm_purchase, menu);
		return true;
	}*/

	
}
