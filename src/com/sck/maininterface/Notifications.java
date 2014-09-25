package com.sck.maininterface;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class Notifications extends Activity {
	ActionBar actionBar;
	RingtoneManager mRingtoneManager;
	final Builder builderVib=null;
	CheckBox chkBoxNot;
	String chosenRingtone;

	// Notification chosenRingtone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		actionBar=getActionBar();
		actionBar.setTitle("Notifications");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
		actionBar.setDisplayHomeAsUpEnabled(true); 
		

		
		mRingtoneManager = new RingtoneManager(this);
		Cursor mcursor;
		mcursor = mRingtoneManager.getCursor();

		chkBoxNot = (CheckBox) findViewById(R.id.chkNotify);
		chkBoxNot.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					audioManager.setStreamMute(
							AudioManager.STREAM_NOTIFICATION, false);

					Toast.makeText(getBaseContext(), "Hi it's Checked",
							Toast.LENGTH_LONG).show();
				} else {
					AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					audioManager.setStreamMute(
							AudioManager.STREAM_NOTIFICATION, true);

				}
			}
		});

	}

	public void onClick1(View view) {
		/* *************************
		 * MediaPlayer mp = MediaPlayer.create(this,
		 * Uri.parse(PreferenceManager.
		 * getDefaultSharedPreferences(c).getString("sound",
		 * "content://settings/system/notification_sound"));
		 * mp.setLooping(true); mp.start();******************
		 */

		// startActivity(new Intent(this,ChatSettings.class));

		Toast.makeText(getBaseContext(), "Hi it's Checked", Toast.LENGTH_LONG)
				.show();

		/* ********
		 * RingtoneManager ringtoneMgr = new RingtoneManager(this);
		 * ringtoneMgr.setType(RingtoneManager.TYPE_ALARM); Cursor alarmsCursor
		 * = ringtoneMgr.getCursor(); int alarmsCount = alarmsCursor.getCount();
		 * if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) { return null; }
		 * Uri[] alarms = new Uri[alarmsCount];
		 * while(!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) { int
		 * currentPosition = alarmsCursor.getPosition(); alarms[currentPosition]
		 * = ringtoneMgr.getRingtoneUri(currentPosition); }
		 * alarmsCursor.close(); return alarms;*****************************
		 */

		/* ***********************************
		 * Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		 * startActivity(intent);**********************
		 */

		/* ***********************
		 * Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		 * intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
		 * RingtoneManager.TYPE_RINGTONE);
		 * intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
		 * intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
		 * RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		 * startActivityForResult(intent, 5);*****************
		 */

		final Intent ringtone = new Intent(
				RingtoneManager.ACTION_RINGTONE_PICKER);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
				RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		startActivityForResult(ringtone, RingtoneManager.TYPE_NOTIFICATION);

	}
	
	public void onClick21(View view){
		
		final Intent ringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		startActivityForResult(ringtone, RingtoneManager.TYPE_NOTIFICATION);
		
	}
	

    public void onClick2(View v)
    {
    	NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);
    	final Notification note = mBuilder.build();
    	final CharSequence[] items={"Off","Default","Short","Long"};
    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
    	builder.setTitle("Vibrate");
    	builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	
     	builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			//	LinearLayout ll=(LinearLayout)findViewById(R.id.linear);
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				
				if("Off".equals(items[which]))
				{
				//	Toast.makeText(getApplication(), "U selected red", Toast.LENGTH_LONG).show();
					try{
					v.vibrate(0);
					}
					catch (Exception e)
					{
						Log.w("Error","Error");
					}
				}
				else if("Default".equals(items[which]))
				{
					/*Toast.makeText(getApplication(), "U selected red", Toast.LENGTH_LONG).show();
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(1000);
					*/
					try{
						builderVib.setVibrate(new long[] {1000});
					}	
					catch(Exception e)
					{
						
						Log.w("Vib Err","Vib Err");
					}
					//ll.setBackgroundColor(Color.GREEN);
					}
				else if("Short".equals(items[which]))
				{
					//Toast.makeText(getApplication(), "U selected red", Toast.LENGTH_LONG).show();
			
					v.vibrate(500);
					//ll.setBackgroundColor(Color.BLUE);
					}
				else if("Long".equals(items[which]))
				{
					//Toast.makeText(getApplication(), "U selected red", Toast.LENGTH_LONG).show();
					//v.vibrate(3000);
					//ll.setBackgroundColor(Color.BLUE);
					v.vibrate(2000);
					/*SharedPreferences preferences = this.getSharedPreferences("VIBRATE",
				            0);
					boolean vibrate = preferences.getBoolean("vibrate", true);
					if (vibrate) {
				        noti.defaults |= Notification.DEFAULT_VIBRATE;
				    }*/
					try{
						note.defaults |= Notification.DEFAULT_VIBRATE;
						
					}
					catch (Exception e)
					{
						
						Log.w("Err","Err");
					}
				}
			}
		});
    	builder.show();
    
    }
    
    public void onClick3(View v)
    {
    	NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);
    	final Notification note = mBuilder.build();
    	final CharSequence[] items={"None","White","Red","Yellow","Green","Cyan","Blue","Purple"};
    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
    	builder.setTitle("Light");
    	builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	
     	builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			
				NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
			    Notification notif = new Notification();
			    
				if("None".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				    
				}
				else if("White".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Red".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Yellow".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Green".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Cyan".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Blue".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
				else if("Purple".equals(items[which]))
				{
				    notif.ledARGB = 0xFFff0000;
				    notif.flags = Notification.FLAG_SHOW_LIGHTS;
				    notif.ledOnMS = 100; 
				    notif.ledOffMS = 100; 
				    nm.notify(0, notif);
				
				}
			}
		});
    	builder.show();
    
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == RingtoneManager.TYPE_NOTIFICATION) {
			Uri uri = intent
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			try {
				RingtoneManager.setActualDefaultRingtoneUri(this, requestCode,
						uri);
				// builder.setSound(uri);

				Toast.makeText(getApplicationContext(), uri.toString(),
						Toast.LENGTH_LONG).show();

				Toast.makeText(
						getApplicationContext(),
						(RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
								.toString(), Toast.LENGTH_LONG).show();
				// this.chosenRingtone= uri.toString();
				// RingtoneManager.setActualDefaultRingtoneUri(this,
				// RingtoneManager.TYPE_NOTIFICATION, uri);
			} catch (Exception localException) {
				Log.w("Error", "Error");

			}

		} else {
			Toast.makeText(getApplicationContext(), "Not Selected",
					Toast.LENGTH_LONG).show();

		}
	}
}
