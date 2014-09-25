package com.sep.buyphonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sck.maininterface.R;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayNumbersActivity extends ListActivity {

	// Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    
    ArrayList<HashMap<String, String>> phoneNumberList;
    String country;
    
 
    // URL to find phone numbers list
    private static String url_send_sms = "http://sepwecom.preview.kyrondesign.com/twiliosmssend/findphonenumber.php";
    
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ALLNUMBERSNODE = "allnumbersnode";
    private static final String TAG_PHONENUMBER = "phonenumber";
    
    //for Intent
    public final static String EX_SELECTEDNUMBER = "com.example.buyphonenumber";
    
    // numbers JSONArray
    JSONArray numbersArray = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j_numbers_list);
		
		Intent getCountry = getIntent(); //Return the intent that started this activity.
	    country = getCountry.getStringExtra(BuyPhoneMainActivity.EX_COUNTRY);
		
		// Hashmap for ListView
        phoneNumberList = new ArrayList<HashMap<String, String>>();
        
        // executing asynchronous task
        new SendCountryAndGetNumbers().execute();
        
        // Get listview
        ListView lv = getListView();
        
        lv.setOnItemClickListener(new OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String phoneno = ((TextView) view.findViewById(R.id.name)).getText().toString();
 
                // Starting new intent
                Intent phoneNoIntent = new Intent(getApplicationContext(),ConfirmPurchaseActivity.class);
                // sending pid to next activity
                phoneNoIntent.putExtra(EX_SELECTEDNUMBER, phoneno);
				startActivity(phoneNoIntent);
				finish();
            }
        });
	}

	//Background Async Task to Send Country Code and get Corresponding Numbers
    class SendCountryAndGetNumbers extends AsyncTask<String, String, String> {
 
        // Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayNumbersActivity.this);
            pDialog.setMessage("Sending Country Data...");
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
            params.add(new BasicNameValuePair("country", country));
            
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
					 numbersArray = json.getJSONArray(TAG_ALLNUMBERSNODE);
					 
					// looping through All Products
	                    for (int i = 0; i < numbersArray.length(); i++) {
	                        JSONObject c = numbersArray.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        String phno = c.getString(TAG_PHONENUMBER);
	                        
	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        map.put(TAG_PHONENUMBER, phno);
	                        
	 	                    // adding HashList to ArrayList
	                        phoneNumberList.add(map);
	                    }
	                
		            } else {
		                // no data to download
		            	DisplayNumbersActivity.this.runOnUiThread(new Runnable() {
		              	  public void run() {
		              	    Toast.makeText(DisplayNumbersActivity.this, "No Phone Numbers Available", Toast.LENGTH_SHORT).show();
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
                    ListAdapter adapter = new SimpleAdapter(
                            DisplayNumbersActivity.this, phoneNumberList,
                            R.layout.j_list_item, new String[] {TAG_PHONENUMBER},
                            new int[] {R.id.name});
                    // updating listview
                    setListAdapter(adapter);
                }
            });
            
        }
 
    }
    
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_numbers, menu);
		return true;
	}*/

	
}
