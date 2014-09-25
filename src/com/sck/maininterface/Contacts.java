package com.sck.maininterface;

import com.sck.maininterface.contactslist.ui.ContactsListActivity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Contacts extends ListActivity {

	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		
		actionBar=getActionBar();
		actionBar.setTitle("Contacts");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Contacts)));
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String settings[]=getResources().getStringArray(R.array.Contacts);
		String con=settings[position];
		
		try
		{
			String class_name="com.sck.maininterface."+con;
			//Class ourClass=Class.forName(class_name);
			
			
			//Toast.makeText(this, class_name, Toast.LENGTH_SHORT).show();
			
		//	if (class_name == "com.sck.maininterface.Tell a friend")
			//{
				startActivity(new Intent(Contacts.this, ContactsListActivity.class));
		//	}
			
			
			
			
			
			
		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	

}
