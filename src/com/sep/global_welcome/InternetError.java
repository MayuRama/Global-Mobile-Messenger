package com.sep.global_welcome;

import com.sck.maininterface.R;

import android.R.interpolator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class InternetError extends Activity {
	
	Button InternetErrorButton;
	ImageView NoInternetImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.interneterror);
		super.onCreate(savedInstanceState);
		
		
		InternetErrorButton=(Button) findViewById(R.id.button1);
		NoInternetImageView=(ImageView) findViewById(R.id.imageView1);
		
		//getActionBar().setTitle("Error!");
		
		Animation InternetErrorAnim=AnimationUtils.loadAnimation(this,R.anim.internet_error_anim);
		NoInternetImageView.startAnimation(InternetErrorAnim);
		
		InternetErrorButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
				
			}
		});
	}
	

}
