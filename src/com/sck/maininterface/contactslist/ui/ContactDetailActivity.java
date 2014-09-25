
package com.sck.maininterface.contactslist.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.sck.maininterface.BuildConfig;
import com.sck.maininterface.R;
import com.sck.maininterface.contactslist.HomeActivity;
import com.sck.maininterface.contactslist.util.Utils;

// Fragment Activity Class of ContactDetailsFragment.class
public class ContactDetailActivity extends FragmentActivity {
	
	ActionBar actionBar;
    // Defines a tag for identifying the single fragment for this activity
    private static final String TAG = "ContactDetailActivity";

    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
    	
    	
    	  actionBar=getActionBar(); 
          actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
          actionBar.setDisplayHomeAsUpEnabled(true);
          
        super.onCreate(savedInstanceState);

        // This activity expects to receive an intent that contains the uri of a contact
        if (getIntent() != null) {

            // For OS versions honeycomb and higher use action bar
            if (Utils.hasHoneycomb()) {
                // Enables action bar "up" navigation
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Fetch the data Uri from the intent provided to this activity
            final Uri uri = getIntent().getData();
            
            // ContactDetailFragment with the Uri provided in the intent
            if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                // Passing the URI to ContactDetailFragment Class
                ft.add(android.R.id.content, ContactDetailFragment.newInstance(uri), TAG);
                ft.commit();
            }
        } else {
            // No intent provided, nothing to do so finish()
            finish();
        }
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
