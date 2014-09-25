package com.sep.global_welcome;

import com.sck.maininterface.R;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class TermsAndConditions extends Activity {
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.terms);
		super.onCreate(savedInstanceState);
		
		
		actionBar=getActionBar();
		actionBar.setTitle("Terms and conditions");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
		actionBar.setDisplayHomeAsUpEnabled(true);  
		
	}

}
