package com.sck.maininterface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Introduction extends Activity {
	CheckBox checkBox;
	Button button;
	private SharedPreferences preferences;
	
	static int RESPOND=0;
	static int STORED_RESPOND=0;
	public static final String KEY="key";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_introduction);
		
		preferences=getPreferences(MODE_PRIVATE);
		checkBox=(CheckBox) findViewById(R.id.checkBox1);
		button=(Button) findViewById(R.id.button1);
		
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkBox.isChecked())
				{
					RESPOND=1;
				}
				
				finish();
				//Toast.makeText(Introduction.this,"RESPOND ="+RESPOND , 5000).show();
			}
			
		});
		
		
	}
	
	public void setPreferences(View v ) {
	
		SharedPreferences.Editor editor=preferences.edit();
		editor.putInt(KEY, RESPOND);
		editor.commit();
		
	}
	
	public void  getPreferences(View v) {
		
		STORED_RESPOND=preferences.getInt(KEY, 0);
		
		
	}

}
