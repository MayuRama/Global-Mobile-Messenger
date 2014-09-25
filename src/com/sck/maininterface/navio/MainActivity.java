package com.sck.maininterface.navio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
 
import org.json.JSONObject;
 
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
 


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sck.maininterface.R;

public class MainActivity extends FragmentActivity {
	
	
	ActionBar actionBar;
	AutoCompleteTextView atvPlaces;
	 
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
 
    GoogleMap googleMap;
    
    //**from proximity
    LocationManager locationManager;
    PendingIntent pendingIntent;
    SharedPreferences sharedPreferences;
    // **
    
    //** new for multiple
    int locationCount = 0;
    // **
    
    final int PLACES=0;
    final int PLACES_DETAILS=1;
    
    private static int PICK_CONTACT_REQUEST = 1;
    private static int PICK_CONTACT_NO = 1;
    
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    
    final Context context = this;
    String reminder = "" ;
    String reminderDescription = "";
    String contactNo = "";
    String contactName = "";
    
    
    View promptView;
    AlertDialog.Builder alertDialogBuilder;
    EditText rem,des,contact,etContctName;
    ImageButton openContactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j_activity_main);
		
		
		actionBar=getActionBar(); 
	    actionBar.setTitle("Location Reminder");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	    actionBar.setDisplayHomeAsUpEnabled(true);  
		
		// Getting a reference to the AutoCompleteTextView
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);
        
        final LayoutInflater layoutInflater = LayoutInflater.from(context);

		promptView = layoutInflater.inflate(R.layout.j_prompt, null);

		alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);
		
		//final EditText input = (EditText) promptView.findViewById(R.id.userInput);
		rem = (EditText) promptView.findViewById(R.id.etRem);
		des = (EditText) promptView.findViewById(R.id.etDes);
		contact = (EditText) promptView.findViewById(R.id.etConNo);
		etContctName = (EditText) promptView.findViewById(R.id.etConName);
		openContactList = (ImageButton) promptView.findViewById(R.id.openContactList);
		
		openContactList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT_REQUEST);
								
			}
		});
		
		
        // Adding textchange listener
        atvPlaces.addTextChangedListener(new TextWatcher() {
 
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);
 
                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());
 
               // Start downloading Google Places
               // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }
 
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
                // TODO Auto-generated method stub
            }
 
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
 
        // Setting an item click listener for the AutoCompleteTextView dropdown list
        atvPlaces.setOnItemClickListener(new OnItemClickListener() {
        	//Callback method to be invoked when an item in this AdapterView has been clicked.
        	@Override 
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
            long id) {
        		// arg0 - The AdapterView where the click happened.
        		// arg1 - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
        		// index - The position of the view in the adapter.
        		// id - The row id of the item that was clicked.
        		
        		//An Adapter object acts as a bridge between an AdapterView and the underlying data for that view. 
        		//A Map is a data structure consisting of a set of keys and values in which each key is mapped to a single value. 
        		//HashMap is an implementation of Map.
                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();
 
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
 
                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
 
                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));
 
                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);
 
            }
        });
        
        // ** from proximity
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
 
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }else { // Google Play Services are available
 
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
 
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
 
            // Getting LocationManager object from System Service LOCATION_SERVICE
            // for controlling locations e.g gps
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            //A SharedPreferences object points to a file containing key-value pairs and provides simple methods to read and write them. 
            // Opening the sharedPreferences object
            sharedPreferences = getSharedPreferences("location", 0);
 
            // ** new multiple alert
            // Getting number of locations already stored
            locationCount = sharedPreferences.getInt("locationCount", 0);
 
            // Getting stored zoom level if exists else return 0
            String zoom = sharedPreferences.getString("zoom", "0");
            
            // If locations are already saved
            if(locationCount!=0){

                String lat = "";
                String lng = "";

                // Iterating through all the locations stored
                for(int i=0;i<locationCount;i++){

                    // Getting the latitude of the i-th location
                    lat = sharedPreferences.getString("lat"+i,"0");

                    // Getting the longitude of the i-th location
                    lng = sharedPreferences.getString("lng"+i,"0");

                    // Drawing marker on the map
                    drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                    // Drawing circle on the map
                    drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                }

                // Moving CameraPosition to last clicked position
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

                // Setting the zoom level in the map on last position  is clicked
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
           }
            
           googleMap.setOnMapClickListener(new OnMapClickListener() {
 
                @Override
                public void onMapClick(LatLng point) {
 
                   // ** new multiple alert
                	
                	// Incrementing location count
                    locationCount++;
                    // **
                    
                    // Drawing marker on the map
                    drawMarker(point);
 
                    // Drawing circle on the map
                    drawCircle(point);
 
                    // This intent will call the activity ProximityActivity com.example.proximitymapv2.activity.proximity
                    // Intent proximityIntent = new Intent("in.wptrafficanalyzer.activity.proximity");
                    Intent proximityIntent = new Intent("com.sck.maininterface.navio.activity.proximity");
 
                    // ** new multiple alert
                    // Passing latitude to the PendingActivity
                    proximityIntent.putExtra("lat",point.latitude);
 
                    // Passing longitude to the PendingActivity
                    proximityIntent.putExtra("lng", point.longitude);
                    //**
                    
                    // Creating a pending intent which will be invoked by LocationManager when the specified region is
                    // entered or exited
                    /** A pending intent is a token that you give to another application (e.g., notification manager, alarm manager or other 3rd party applications), 
                     * which allows this other application to use the permissions of your application to execute a predefined piece of code.*/
                    pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
 
                    // Setting proximity alert
                    // The pending intent will be invoked when the device enters or exits the region 20 meters
                    // away from the marked point
                    // The -1 indicates that, the monitor will not be expired
                    locationManager.addProximityAlert(point.latitude, point.longitude, 10, -1, pendingIntent);
 
                    /** Opening the editor object to write data to sharedPreferences */
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
 
                    // ** new multiple alert
                    // Storing the latitude for the i-th location
                    editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));
 
                    // Storing the longitude for the i-th location
                    editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));
 
                    // Storing the count of locations or marker count
                    editor.putInt("locationCount", locationCount);
 
                    /** Storing the zoom level to the shared preferences */
                    editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
                    
                    // clear dialog 
                    rem.setText("");
                    des.setText("");
                    contact.setText("");
                    etContctName.setText("");

                    // setup a dialog window
					alertDialogBuilder
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											//get user input and set it to result
											//editTextMainScreen.setText(input.getText());
											reminder = rem.getText().toString();
											reminderDescription = des.getText().toString();
											contactNo = contact.getText().toString();
											contactName = etContctName.getText().toString();
											
											editor.putString("Remindar", reminder);
											
											editor.putString("RemDes", reminderDescription);
											
											editor.putString("ContatcNo", contactNo);
											
											editor.putString("Contact_Name", contactName);
						 
						                    //** Saving the values stored in the shared preferences *//*
						                    editor.commit();
						 
						                    Toast.makeText(getBaseContext(), "Proximity Alert is added", Toast.LENGTH_SHORT).show();
						                    
						                    //dialog.cancel();
						                    dialog.dismiss();
						                    
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,	int id) {
											dialog.cancel();
										}
									});

					// avoid illeglestate exception
					if (promptView != null) {
		            ViewGroup parent = (ViewGroup) promptView.getParent();
		            if (parent != null)

		                parent.removeView(promptView);
					}
					
					// create an alert dialog
					AlertDialog alertD = alertDialogBuilder.create();

					alertD.show();
					
                }
            });
 
            googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    //Intent proximityIntent = new Intent("in.wptrafficanalyzer.activity.proximity");
                	Intent proximityIntent = new Intent("com.example.proximitymapv2.activity.proximity");
 
                    pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
 
                    // Removing the proximity alert
                    locationManager.removeProximityAlert(pendingIntent);
 
                    // Removing the marker and circle from the Google Map
                    googleMap.clear();
 
                    // Opening the editor object to delete data from sharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
 
                    // Clearing the editor
                    editor.clear();
 
                    // Committing the changes
                    editor.commit();
 
                    Toast.makeText(getBaseContext(), "Remindar is Removed", Toast.LENGTH_LONG).show();
                }
            });
        }
        //**
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == PICK_CONTACT_REQUEST)
		{
			if(resultCode == RESULT_OK)
			{
				uriContact = data.getData();
				
				retrieveContactNumber();
				retrieveContactName();
			}
		}
		
	}
 
	private void retrieveContactNumber() {
		 
        String contactNumber = null;
 
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
 
        if (cursorID.moveToFirst()) {
 
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
 
        cursorID.close();
 
        //Log.d(TAG, "Contact ID: " + contactID);
 
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
 
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
 
                new String[]{contactID},
                null);
 
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
 
        cursorPhone.close();
 
        //Log.d(TAG, "Contact Phone Number: " + contactNumber);
        contact.setText(contactNumber);
    }
	
	private void retrieveContactName() {
		 
        String contactName = null;
 
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
 
        if (cursor.moveToFirst()) {
 
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
 
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
 
        cursor.close();
 
        
        
        //Log.d(TAG, "Contact Name: " + contactName);
        etContctName.setText(contactName);
 
    }
	
	// ** from proximity
	
	private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
 
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
 
        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
    }
 
    private void drawCircle(LatLng point){
 
        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();
 
        // Specifying the center of the circle
        circleOptions.center(point);
 
        // Radius of the circle
        circleOptions.radius(20);
 
        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);
 
        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);
 
        // Border width of the circle
        circleOptions.strokeWidth(2);
 
        // Adding the circle to the GoogleMap
        googleMap.addCircle(circleOptions);
 
	}
    //**
    
    // we can pass the name of the place and retun palace
    private String getAutoCompleteUrl(String place){
 
        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyA_q5fbGslkhaEaFOUvCC6ssxgi_Q-sb5Q";
 
        // place to be be searched
        String input = "input="+place;
 
        // place type to be searched
        String types = "types=geocode";
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = input+"&"+types+"&"+sensor+"&"+key;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
 
        return url;
    }
 
    private String getPlaceDetailsUrl(String ref){
 
        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyA_q5fbGslkhaEaFOUvCC6ssxgi_Q-sb5Q";
 
        // reference of place
        String reference = "reference="+ref;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = reference+"&"+sensor+"&"+key;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;
 
        return url;
    }
 
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        private int downloadType=0;
 
        // Constructor
        public DownloadTask(int type){
            this.downloadType = type;
        }
 
        /** This class allows to perform background operations and publish results on the UI thread 
         * without having to manipulate threads and/or handlers. */
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        /**Runs on the UI thread after doInBackground(Params...). 
         * The specified result is the value returned by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            switch(downloadType){
            case PLACES:
                // Creating ParserTask for parsing Google Places
                placesParserTask = new ParserTask(PLACES);
 
                // Start parsing google places json data
                // This causes to execute doInBackground() of ParserTask class
                placesParserTask.execute(result);
 
                break;
 
            case PLACES_DETAILS :
                // Creating ParserTask for parsing Google Places
                placeDetailsParserTask = new ParserTask(PLACES_DETAILS);
 
                // Starting Parsing the JSON string
                // This causes to execute doInBackground() of ParserTask class
                placeDetailsParserTask.execute(result);
            }
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        int parserType = 0;
 
        public ParserTask(int type){
            this.parserType = type;
        }
 
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<HashMap<String, String>> list = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                switch(parserType){
                case PLACES :
                    PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                    // Getting the parsed data as a List construct
                    list = placeJsonParser.parse(jObject);
                    break;
                case PLACES_DETAILS :
                    PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                    // Getting the parsed data as a List construct
                    list = placeDetailsJsonParser.parse(jObject);
                }
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return list;
        }
 
        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
 
            switch(parserType){
            case PLACES :
                String[] from = new String[] { "description"};
                int[] to = new int[] { android.R.id.text1 };
 
                // Creating a SimpleAdapter for the AutoCompleteTextView
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
 
                // Setting the adapter
                atvPlaces.setAdapter(adapter);
                break;
            case PLACES_DETAILS :
                HashMap<String, String> hm = result.get(0);
 
                // Getting latitude from the parsed data
                double latitude = Double.parseDouble(hm.get("lat"));
 
                // Getting longitude from the parsed data
                double longitude = Double.parseDouble(hm.get("lng"));
 
                // Getting reference to the SupportMapFragment of the activity_main.xml
                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
                // Getting GoogleMap from SupportMapFragment
                googleMap = fm.getMap();
 
                LatLng point = new LatLng(latitude, longitude);
 
                CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
                CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);
 
                // Showing the user input location in the Google Map
                googleMap.moveCamera(cameraPosition);
                googleMap.animateCamera(cameraZoom);
 
                MarkerOptions options = new MarkerOptions();
                options.position(point);
                options.title("Position");
                options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
 
                // Adding the marker in the Google Map
                googleMap.addMarker(options);
 
                break;
            }
        }
	}


	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

}
