package com.sck.maininterface;

import android.app.ActionBar;
import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Status extends ListActivity {
	
	
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		
			actionBar=getActionBar(); 
	       actionBar.setTitle("Status");
	       actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	       
		
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_2,
				getResources().getStringArray(R.array.status)));
		
	}

}
