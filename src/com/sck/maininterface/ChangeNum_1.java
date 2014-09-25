package com.sck.maininterface;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ChangeNum_1 extends Activity{

	ActionBar actionBar;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_num1);
		
		actionBar=getActionBar(); 
		actionBar.setTitle("Change Number");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));   
		
	}
	

	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	       
	    	getMenuInflater().inflate(R.menu.signup_end_acions, menu);
	    	return super.onCreateOptionsMenu(menu);
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
			case R.id.action_next:
			startActivity(new Intent(ChangeNum_1.this, ChangeNum_2.class));
			finish();
				break;
			
			default:
				return super.onOptionsItemSelected(item);
				
			}
			return false;
	    	
	    }

}
