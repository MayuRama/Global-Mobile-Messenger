package com.sck.maininterface.navio;

import com.sck.maininterface.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class NotificationView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.j_notification);
		
		TextView tvRemTitle = (TextView) findViewById(R.id.textView2);
		final TextView tvRemDescription = (TextView) findViewById(R.id.tvDes);
		final TextView tv = (TextView) findViewById(R.id.tv_notification);
		
		ImageButton imgbtnCall = (ImageButton) findViewById(R.id.imageButton1);
		ImageButton imgbtnSms = (ImageButton) findViewById(R.id.imageButton2);
		
		Bundle data = getIntent().getExtras();
        tvRemTitle.setText(data.getString("remtitle"));
        tvRemDescription.setText(data.getString("remdes"));
        tv.setText(data.getString("content"));
        
        imgbtnCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+tv.getText().toString()));
				startActivity(callIntent);
			}
		});
		
        imgbtnSms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setData(Uri.parse("sms:"+tv.getText().toString()));
				smsIntent.putExtra("sms_body", tvRemDescription.getText().toString());
				startActivity(smsIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_view, menu);
		return true;
	}

}
