

package com.sck.maininterface.contactslist.ui;

import java.util.ResourceBundle.Control;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;

import com.sck.maininterface.BuildConfig;
import com.sck.maininterface.R;
import com.sck.maininterface.contactslist.util.Utils;

// Here we are defining the Fragement Activity Class to hold ContactListFragment 
public class ContactsListActivity extends FragmentActivity implements
        ContactsListFragment.OnContactsInteractionListener {
	
	ActionBar actionBar;

    // Defines a tag for identifying log entries
    private static final String TAG = "ContactsListActivity";

    private ContactDetailFragment mContactDetailFragment;


    private boolean isSearchResultView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);

        setContentView(R.layout.m_activity_main2);
        
        actionBar=getActionBar(); 
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
        actionBar.setDisplayHomeAsUpEnabled(true);  


        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

            // Fetch query from intent and notify the fragment that it should display search
            // results instead of all contacts.
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            ContactsListFragment mContactsListFragment = (ContactsListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.contact_list);

// If true not display all contacts
            isSearchResultView = true;
            mContactsListFragment.setSearchQuery(searchQuery);

            // Set special title for search results
            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
            setTitle(title);
        }

        
    }

  // By selecting the contact Uri directing to ContactDetailsActivity Class
    static final int PICK_CONTACT_REQUEST=1;
    @Override
    public void onContactSelected(Uri contactUri) {
       
 
     /*   Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.setData(contactUri);
            startActivity(intent);
            */
    	Uri uri = Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode("bob"));
		Cursor c = getContentResolver().query(uri,
		          new String[]{Email.CONTACT_ID, Email.DISPLAY_NAME, Email.DATA},
		          null, null, null);	
		String emailId = null;
		int idx;
		if (c.moveToFirst()) {
			idx = c.getColumnIndex(Email.DATA1);
			emailId = c.getString(idx);
		}
		
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_SUBJECT,
				"");
		email.putExtra(Intent.EXTRA_EMAIL, emailId);
		email.putExtra(Intent.EXTRA_TEXT,
				"");
		email.setData(contactUri);
		startActivity(email);
    }
  // No Contacts Selected
    @Override
    public void onSelectionCleared() {
        
    }

    @Override
    public boolean onSearchRequested() {
 // Dont Allowing another searching if already it searches
        return !isSearchResultView && super.onSearchRequested();
    }
}
