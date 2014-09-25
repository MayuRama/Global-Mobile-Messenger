package com.sck.maininterface.contactslist;

import com.sck.maininterface.R;
import com.sck.maininterface.contactslist.ui.ContactsListActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
		case R.id.action_search:
			Toast.makeText(getBaseContext(), "Clicked on Search icon", Toast.LENGTH_LONG).show();
			return true;
		
		case R.id.action_contacts:
			Intent in = new Intent(HomeActivity.this,ContactsListActivity.class);
			startActivity(in);
			return true;
					
		}
		return super.onOptionsItemSelected(item);
	}

}
