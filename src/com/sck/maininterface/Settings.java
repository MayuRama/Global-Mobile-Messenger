package com.sck.maininterface;


import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends ListActivity {
	
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		actionBar=getActionBar(); 
	    actionBar.setTitle("Settings");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
	    actionBar.setDisplayHomeAsUpEnabled(true);   
	    
	    
	   // ListView listView=getListView();
	    //listView= (ListView) findViewById(R.id.listView1);
	    
	       
	   setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1,R.id.settings_item_textView1,
	    		   getResources().getStringArray(R.array.sttings)));
	   
	   
	  /* listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			if (id == 0)
			{
				startActivity(new Intent(Settings.this, Help.class));
			}
		}
	});*/
	   
   
	       
	}
	
	private class MyAdapter extends ArrayAdapter<String>
	{

		public MyAdapter(Context context, int resource, int textViewResourceId,
				String[] strings) {
			super(context, resource, textViewResourceId, strings);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {


			LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.settings_item, parent, false);
			String[] items = getResources().getStringArray(R.array.sttings);
			
			ImageView imageView=(ImageView) row.findViewById(R.id.settings_item_imageView1);
			TextView tv=(TextView) row.findViewById(R.id.settings_item_textView1);
			
			tv.setText(items[position]);
			
			if (items[position].equals("Help"))
			{
				imageView.setImageResource(R.drawable.ic_action_help);
			}
			else if (items[position].equals("Profile"))
			{
				imageView.setImageResource(R.drawable.ic_action_person);
			}
			else if (items[position].equals("Account"))
			{
				imageView.setImageResource(R.drawable.ic_action_accounts);
			}
			else if (items[position].equals("Chat Settings"))
			{
				imageView.setImageResource(R.drawable.ic_action_chat_b);
			}
			else if (items[position].equals("Notifications"))
			{
				imageView.setImageResource(R.drawable.ic_action_volume_on);
			}
			else if (items[position].equals("Contacts"))
			{
				imageView.setImageResource(R.drawable.ic_action_view_as_list);
			}
			
			
			
			return row;
		}
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String settings[]=getResources().getStringArray(R.array.sttings);
		String con=settings[position].replace(" ", "");
		
		
		
		try
		{
			String class_name="com.sck.maininterface."+con;
			
			
			if (class_name.equals("com.sck.maininterface.Profile"))
			{
				//call Kasun's activity
				startActivity(new Intent(Settings.this, com.sep.global_welcome.MainActivity.class));
				
			}
			else
			{
				@SuppressWarnings("rawtypes")
				Class ourClass=Class.forName(class_name);
				startActivity(new Intent(Settings.this, ourClass));
			}
			
			
		}
		catch (Exception e)
		{
			//startActivity(new Intent(Settings.this, com.sep.global_welcome.MainActivity.class));
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			
		}
	}
	

}
