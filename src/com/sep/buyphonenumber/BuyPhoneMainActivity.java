package com.sep.buyphonenumber;


import com.sck.maininterface.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;



public class BuyPhoneMainActivity extends Activity {

    Spinner countryList,phonenumType;
    String countryName;
    
    String countryShortCode;
 
    public final static String EX_COUNTRY = "com.example.buyphonenumber";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.j_activity_buy_phone_main);
        
        Button btnSetCountry = (Button) findViewById(R.id.btnSetCountry);
        countryList = (Spinner) findViewById(R.id.spnCountries);
        phonenumType = (Spinner) findViewById(R.id.spnTypes);
        
        // Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.country_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		countryList.setAdapter(adapter);
		
		
		ArrayAdapter<CharSequence> adapterforNumTypes = ArrayAdapter.createFromResource(this,
				      R.array.numtype_array, android.R.layout.simple_spinner_item);
		adapterforNumTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		phonenumType.setAdapter(adapterforNumTypes);
        
        btnSetCountry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				countryName = countryList.getSelectedItem().toString();
				if(countryName.equals("Canada"))
					countryShortCode = "CA";
				else if(countryName.equals("United Kingdom"))
					countryShortCode = "GB";
				else if(countryName.equals("United States"))
					countryShortCode = "US";
				else if(countryName.equals("United States"))
					countryShortCode = "AU";
				else if(countryName.equals("Sweden"))
					countryShortCode = "SE";
				else
					countryShortCode = "US";
				/*
				 * supported by java 1.7 or higher compilers.(Strings inside switch)
				switch(countryName)
				{
					case "Canada": countryShortCode = "CA";
					break;
					
					case "United Kingdom": countryShortCode = "GB";
					break;
					
					case "United States": countryShortCode = "US";
					break;
					
					case "Australia": countryShortCode = "AU";
					break;
					
					case "Sweden": countryShortCode = "SE";
					break;
					
					default: countryShortCode = "US"; 
				
				}
				*/
				
				
				Intent countryIntent = new Intent(BuyPhoneMainActivity.this,DisplayNumbersActivity.class);
				countryIntent.putExtra(EX_COUNTRY, countryShortCode);
				startActivity(countryIntent);
				finish();
			}
		});
    }

  

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buy_phone_main, menu);
        return true;
    }*/

    
}
