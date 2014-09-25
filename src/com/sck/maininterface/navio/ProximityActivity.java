package com.sck.maininterface.navio;

import com.sck.maininterface.R;

import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.widget.Toast;

public class ProximityActivity extends Activity {

	String notificationTitle;
    String notificationContent;
    String tickerMessage;
    
    SharedPreferences sharedPreferences;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		boolean proximity_entering = getIntent().getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		
		// ** new for multiple
		double lat = getIntent().getDoubleExtra("lat", 0);
		 
        double lng = getIntent().getDoubleExtra("lng", 0);
        
        String strLocation = Double.toString(lat)+","+Double.toString(lng);
        
        //**
        
		sharedPreferences = getSharedPreferences("location", 0);
		 
        // Getting stored data if exists else return 0
        String rem = sharedPreferences.getString("Remindar", "0");
        String des = sharedPreferences.getString("RemDes", "0");
        String con = sharedPreferences.getString("ContatcNo", "0");
        String conName = sharedPreferences.getString("Contact_Name", "0");
        
        if(proximity_entering){
            Toast.makeText(getBaseContext(),"You are Entering to the Region"  ,Toast.LENGTH_LONG).show();
            notificationTitle= rem;
            notificationContent= des;
            tickerMessage = rem;
        }else{
            Toast.makeText(getBaseContext(),"You are Exiting from the Region"  ,Toast.LENGTH_LONG).show();
            notificationTitle="Exited from the Region";
            notificationContent="Hey, Did you Call/SMS";
            tickerMessage = "Exited from the Region";
        }
 
        Intent notificationIntent = new Intent(getApplicationContext(),NotificationView.class);
        
        /** Adding content to the notificationIntent, which will be displayed on
         * viewing the notification
         */
        
        notificationIntent.putExtra("content", con );
        notificationIntent.putExtra("contentName", conName );
        notificationIntent.putExtra("remtitle", rem );
        notificationIntent.putExtra("remdes", des );
 
        /** This is needed to make this intent different from its previous intents */
        notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
 
        /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK */
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
 
        /** Getting the System service NotificationManager */
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
 
        /** Configuring notification builder to create a notification */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setWhen(System.currentTimeMillis())
                .setContentText(notificationContent)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.mapimg)
                .setAutoCancel(true)
                .setTicker(tickerMessage)
                .setContentIntent(pendingIntent);
 
        /** Creating a notification from the notification builder */
        Notification notification = notificationBuilder.build();
 
        /** Sending the notification to system.
        * The first argument ensures that each notification is having a unique id
        * If two notifications share same notification id, then the last notification replaces the first notification
        * */
        nManager.notify((int)System.currentTimeMillis(), notification);
 
        /** Finishes the execution of this activity */
        finish();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.proximity, menu);
		return true;
	}

}
