package com.sep.global_welcome;

import com.sck.maininterface.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SimCardError extends Activity {
	
	Button OkButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.simcarderror);
		super.onCreate(savedInstanceState);
		
		
		OkButton=(Button) findViewById(R.id.button1);
		
		OkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				java.lang.System.exit(0);
				
			}
		});
		
	}

}
