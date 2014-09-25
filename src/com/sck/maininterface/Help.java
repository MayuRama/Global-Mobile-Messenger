package com.sck.maininterface;

import com.sck.maininterface.contactslist.ui.ContactsListActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Help extends ListActivity {
	ActionBar actionBar;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		actionBar=getActionBar();
		actionBar.setTitle("Help");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	    actionBar.setDisplayHomeAsUpEnabled(true); 
		
		
		setListAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Help)));
		
		
		
		
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String settings[]=getResources().getStringArray(R.array.Help);
		String con=settings[position];
		
		try
		{
			String className = "com.sck.maininterface."+con;
			if(className.equals("com.sck.maininterface.FAQ")){
				Intent intent = new Intent(Intent.ACTION_VIEW, 
			             Uri.parse("http://sepwecom.preview.kyrondesign.com/phpserver/customer/faq.php"));
				startActivity(intent);
				
			}else if(className.equals("com.sck.maininterface.Contact Us")){
				Log.d("pass", "entered to else if");
				startActivity(new Intent(Help.this, com.sck.maininterface.ContactUs1.class));
			}
			else{
				Log.d("pass", "entered to else");
			String class_name="com.sck.maininterface."+con;
			@SuppressWarnings("rawtypes")
			Class ourClass=Class.forName(class_name);
			startActivity(new Intent(Help.this, ourClass));
			
			}
			
		}
		catch (Exception e)
		{
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			
		}
	}
	
	
}
