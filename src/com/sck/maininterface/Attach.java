package com.sck.maininterface;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class Attach extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
		layoutParams.gravity=Gravity.TOP|Gravity.RIGHT;
		setContentView(R.layout.attach);
	}

}
