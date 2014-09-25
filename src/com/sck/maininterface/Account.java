package com.sck.maininterface;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Account extends ListActivity {
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_account);
		
		actionBar=getActionBar();
		actionBar.setTitle("Account");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
		actionBar.setDisplayHomeAsUpEnabled(true); 
		
		
		setListAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Account)));
		
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		String account[]=getResources().getStringArray(R.array.Account);
		
		String con=account[position].replace(" ","");
		
		//String con=settings[position].replace(" ", "");
		try
		{
			String class_name="com.sck.maininterface."+con;
		//	@SuppressWarnings("rawtypes")
			//Class ourClass=Class.forName(class_name);
			//startActivity(new Intent(Account.this, ourClass));
				if (class_name.equals("com.sck.maininterface.ChangeNumber"))
				{
					startActivity(new Intent(Account.this, ChangeNum_1.class));
					
				}
				else
				{
					@SuppressWarnings("rawtypes")
					Class ourClass=Class.forName(class_name);
					startActivity(new Intent(Account.this, ourClass));
				}
			
		}
		catch (Exception e)
		{
			
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			
		}
	}

}
