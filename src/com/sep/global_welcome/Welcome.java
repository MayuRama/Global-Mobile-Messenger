package com.sep.global_welcome;

import java.io.File;
import java.io.FileInputStream;

import com.sck.maininterface.R;
import com.sck.maininterface.Main;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Welcome extends Activity {

	ImageView LogoImageView;
	Button AgreeButton;
	TextView TermsTextView;
	
	private String file = "mydata";
    private String file1 = "myphoneNo";
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TelephonyManager Tele=(TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
		int SimState=Tele.getSimState();
		
		if (SimState != TelephonyManager.SIM_STATE_ABSENT)
		{
			Log.d("SIM","Sim is available");
		}
		else
		{
			startActivity(new Intent(this,SimCardError.class));
		}
		
		Log.i("pass", "before read() method call");
		String code = read(file);
		String pCode = read(file1);
		//INTEGRATED
		Log.i("pass", "after read() method called");
		if (!code.equals("") && !pCode.equals(""))
		{
			Log.i("pass", "after read() method called");
			  startActivity(new Intent(this, Main.class));
			  finish();
		}
		else
		{
			Log.i("pass", "entered else block");
			
			/*if (!isNetworkAvailable())
			{
				startActivity(new Intent(this,InternetError.class));
			}*/
			
			
		setContentView(R.layout.welcome);
		
		LogoImageView=(ImageView) findViewById(R.id.imageView1);
		AgreeButton=(Button) findViewById(R.id.button1);
		TermsTextView=(TextView) findViewById(R.id.textView3);
		
		Animation WelcomeAnim =AnimationUtils.loadAnimation(this,R.anim.welcome_anim);
		LogoImageView.startAnimation(WelcomeAnim);
		
		
		AgreeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start Kasun's main page (Sign up)
				startActivity(new Intent(Welcome.this,ContactInforActivity.class));
				finish();
				
			}
		});
		
		TermsTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Welcome.this,TermsAndConditions.class));
				
			}
		});
		
		
		
		
	}


	
}

/*public boolean isNetworkAvailable()
{
	ConnectivityManager ConnMan=(ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
	NetworkInfo NetInfo=ConnMan.getActiveNetworkInfo();
	return NetInfo != null && NetInfo.isConnected();
}*/

public String read(String file)
{
    try{
    	Log.i("pass", "entered try block");
       FileInputStream fin = openFileInput(file);
       Log.i("pass", "file open");
       int c;
       String temp="";
       
       while( (c = fin.read()) != -1){
          temp = temp + Character.toString((char)c);
       }
       //et.setText(temp);
      
       return temp;
    }catch(Exception e){
  	  	Log.e("Read Error", e.toString());
    }
    
    return "";

}
}
