package com.sck.maininterface;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.sck.maininterface.R;
import com.sck.twilio.JSONParser;
import com.sck.twilio.SmsSender;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sck.twilio.SmsSender.*;
import com.sep.global_welcome.ContactInforActivity;
import com.sep.global_welcome.VerifyActivity;

/**
 * MessageActivity is a main Activity to show a ListView containing Message
 * items
 * 
 * 
 * 
 */
public class MessageActivity extends ListActivity {
	/** Called when the activity is first created. */

	ArrayList<Message> messages;
	Adapter adapter;
	EditText text;
	static Random rand = new Random();
	static String sender = Main.CONTACT_NAME;
	ImageView send_button;
	ActionBar actionBar;
	String subTitle;
	String messageDBStatus;
	public static String MESSSAGE_CONTENT = "";
	public static String DateTime="";
	public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "wallpapers";

	// /////////////////////////////////////////////////////////////////////////////////

	public ProgressDialog pDialog;

	private static String Senderno="";

	JSONParser jsonParser = new JSONParser();
	// EditText fromNumber; //Twilio no
	// EditText toNumber; //Recievers no
	// EditText smsBody;
	String fromNumber = "+12086470713";
	private Context context;
	// url to create new SMS
	// private static String url_create_sms =
	// "http://10.0.2.2/twiliosmssend/create_sms.php";
	// working url
	// private static String url_send_sms =
	// "http://10.0.2.2/twiliosmssend/sendnotifications.php";
	//private static String url_send_sms = "http://sepwecom.preview.t10.info/twiliosmssend/sendnotifications.php";
	private static String url_send_sms = "http://sepwecom.preview.kyrondesign.com/twiliosmssend/sendnotifications.php";
	
	// private static String url_send_sms =
	// "http://kasunbuddhima.net63.net/phpserver/twiliosmssend/sendnotifications.php";

	// private static String url_create_product =
	// "http://aspenk.netne.net/twiliosmssend/create_sms.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	public static SharedPreferences pref2;
	public static Editor editor2;
	String ppp;
	// //////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
	// /////////////////////////////////////////////////////////////////////////////////////
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_window);
		
		
		 if (Main.CONTACT_NUMBER.startsWith("07"))
	        {
	        	String trim=Main.CONTACT_NUMBER.substring(2);
	        	Senderno="+947"+trim;
	        	//Toast.makeText(MessageActivity.this, Senderno, Toast.LENGTH_LONG).show();
	        }
	        else
	        {
	        	Toast.makeText(MessageActivity.this, "Cannot send meesages to that particular number", Toast.LENGTH_LONG).show();
	        	finish();
	        	startActivity(new Intent(MessageActivity.this, Main.class));
	        }
		 /*
		pref2 = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
		ppp = pref2.getString("picpath", "");
		
		if(!(ppp.equals("")))
		{
			Resources res = getResources();
			Drawable draw = new BitmapDrawable(res, pref2.getString("picpath", null));
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.bk);
			
			if(Build.VERSION.SDK_INT >= 16)
				rl.setBackground(draw);
			else
				rl.setBackgroundDrawable(draw);
		}
		*/
		 Bundle extras = getIntent().getExtras();
		 if(extras!=null){
			 String txt = extras.getString("msg");
			 Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
		 }
		
		 
			 //to set the background chat wall paper
			 RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.bk);
			 try{
			 Bitmap bit = getThumbnail("wallpaper.jpg");
			 Resources res = getResources(); //resource handle
			 BitmapDrawable draw = new BitmapDrawable(bit);
			 int pic = R.drawable.android_logo;
			 Log.e("pass 1", "layout back");
			 if(Build.VERSION.SDK_INT >= 16)
				 rLayout.setBackground(draw);
				else
					rLayout.setBackgroundDrawable(draw);
			 Log.e("pass 2", "layout back ");
			}catch(Exception e){
				 Log.e("no selected","no image is set as background by usesr");
			 }		 

	
			
		actionBar = getActionBar();
		actionBar.setTitle(Main.CONTACT_NAME);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#80b5e1")));
		actionBar.setDisplayHomeAsUpEnabled(true);

		send_button = (ImageView) findViewById(R.id.imageView1);

		send_button.setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				String newMessage = text.getText().toString().trim();
				MESSSAGE_CONTENT = newMessage;
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date=new Date();
				DateTime=dateFormat.format(date);
				
				CheckConnection cc = new CheckConnection(getApplicationContext());
            	Boolean isInternetPresent = cc.isConnectingToInternet();
            	if(isInternetPresent){
					new CreateNewSMS().execute();
            	}else{
            		showAlertDialog(MessageActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
            	}
				if (newMessage.length() > 0) {
					text.setText("");
					addNewMessage(new Message(newMessage, true));
					messageDBStatus = "Sent";
					// update the DB
				}

			}
		});

		text = (EditText) this.findViewById(R.id.text);

		// sender = Utility.sender[rand.nextInt( Utility.sender.length-1)];
		this.setTitle(Main.CONTACT_NAME);
		messages = new ArrayList<Message>();

		/*
		 * messages.add(new Message("Hello", false)); messages.add(new
		 * Message("Hi!", true)); messages.add(new Message("Wassup??", false));
		 * messages.add(new Message("nothing much, working on speech bubbles.",
		 * true)); messages.add(new Message("you say!", true)); messages.add(new
		 * Message("oh thats great. how are you showing them", false));
		 */

		adapter = new Adapter(this, messages);
		setListAdapter(adapter);
		// addNewMessage(new
		// Message("mmm, well, using 9 patches png to show them.", true));
	}

	private class SendMessage extends AsyncTask<Void, String, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(2000); // simulate a network call
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.publishProgress(String.format("%s started writing",
					Main.CONTACT_NAME));
			// ActionBar bar=getActionBar();

			// bar.setSubtitle("typing...");
			try {
				Thread.sleep(2000); // simulate a network call
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.publishProgress(String.format("%s has entered text",
					Main.CONTACT_NAME));
			try {
				Thread.sleep(3000);// simulate a network call
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return Utility.messages[rand.nextInt(Utility.messages.length - 1)];

		}

		@Override
		public void onProgressUpdate(String... v) {

			if (messages.get(messages.size() - 1).isStatusMessage)// check
																	// whether
																	// we have
																	// already
																	// added a
																	// status
																	// message
			{
				messages.get(messages.size() - 1).setMessage(v[0]); // update
																	// the
																	// status
																	// for that
				adapter.notifyDataSetChanged();
				getListView().setSelection(messages.size() - 1);
			} else {
				addNewMessage(new Message(true, v[0])); // add new message, if
														// there is no existing
														// status message
			}
		}

		@Override
		protected void onPostExecute(String text) {
			if (messages.get(messages.size() - 1).isStatusMessage)// check if
																	// there is
																	// any
																	// status
																	// message,
																	// now
																	// remove
																	// it.
			{
				messages.remove(messages.size() - 1);
			}

			addNewMessage(new Message(text, false)); // add the orignal message
														// from server.
		}

	}

	void addNewMessage(Message m) {
		messages.add(m);
		adapter.notifyDataSetChanged();
		getListView().setSelection(messages.size() - 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.chat_window_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_attach:
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			//startActivity(new Intent(MessageActivity.this, Attach.class));
			break;

		case R.id.action_view_contact:
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			//startActivityForResult(new Intent(Intent.ACTION_PICK,
					//android.provider.ContactsContract.Contacts.CONTENT_URI), 1);
			break;

		case R.id.action_media:
			//TODO
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			break;

		case R.id.action_search:
			// TODO
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			break;

		case R.id.action_call:
			// TODO
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			break;

		case R.id.action_wallpaper:
		startActivity(new Intent(MessageActivity.this, Wallpaper.class));
		
			break;

		case R.id.action_more:
			// TODO
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// Kanushka's code

	public class CreateNewSMS extends AsyncTask<String, String, String> {

		// Before starting background thread Show Progress Dialog
		// onPreExecute - Runs on the UI thread after
		// publishProgress(Progress...) is invoked.
		// publishProgress(Progress...) - This method can be invoked from
		// doInBackground(Params...) to publish updates on the
		// UI thread while the background computation is still running.
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MessageActivity.this);
			pDialog.setMessage("Sending SMS..");
			pDialog.setIndeterminate(false); // Change the indeterminate mode
												// for this progress bar.
			// A progress bar can also be made indeterminate. In indeterminate
			// mode, the progress bar shows a cyclic animation
			// without an indication of progress. This mode is used by
			// applications when the length of the task is unknown.
			// The indeterminate progress bar can be either a spinning wheel or
			// a horizontal bar.
			pDialog.setCancelable(true);
			// pDialog.show();
		}

		// Creating SMS
		// Override this method to perform a computation on a background thread.
		// The specified parameters are
		// the parameters passed to execute(Params...) by the caller of this
		// task. This method can call publishProgress(Progress...)
		// to publish updates on the UI thread.
		protected String doInBackground(String... args) {
			// String fromphoneno = fromNumber.getText().toString();
			
			// String tophoneno = toNumber.getText().toString();
			// String smsbody = smsBody.getText().toString();
			// Building Parameters
			//Senderno
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("fromphoneno", fromNumber));
			params.add(new BasicNameValuePair("tophoneno", Senderno));
			params.add(new BasicNameValuePair("smsbody",
					MessageActivity.MESSSAGE_CONTENT));
			//Toast.makeText(MessageActivity.this, fromNumber +","+
			//Main.CONTACT_NUMBER+","+ MessageActivity.MESSSAGE_CONTENT ,
			//Toast.LENGTH_LONG).show();
			// getting JSON Object
			// Note that create sms url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_send_sms, "POST",
					params);
			Log.d("Create Response", json.toString());
			
			int success;
			 
			try {
				success = json.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
			
				e.printStackTrace();
			}
			
			
			// check log cat fro response
			

			// getInt - Returns the value mapped by parameter if it exists and
			// is an int or can be coerced to an int.
			// TAG_SUCCESS is JSON Node names created above
			

				/*
				 * if (success == 1) { MessageActivity.this.runOnUiThread(new
				 * Runnable() { public void run() { //
				 * Toast.makeText(MessageActivity.this, "SMS is Sent",
				 * Toast.LENGTH_SHORT).show(); } }); } else { // failed to
				 * create sms MessageActivity.this.runOnUiThread(new Runnable()
				 * { public void run() { //Toast.makeText(MessageActivity.this,
				 * "SMS is NOT Sent", Toast.LENGTH_SHORT).show(); } }); }
				 */

			

			return null;
		}

		// After completing background task Dismiss the progress dialog

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
	
	public Bitmap getThumbnail(String filename) {
		
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
		Bitmap thumbnail = null;
		
		// Look for the file on the external storage
		try {
		if (isSdReadable() == true) {
		thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
		}
		} catch (Exception e) {
		Log.e("getThumbnail() on external storage", e.getMessage());
		}
		
		// If no file on external storage, look in internal storage
		if (thumbnail == null) {
		try {
		File filePath = context.getFileStreamPath(filename);
		FileInputStream fi = new FileInputStream(filePath);
		thumbnail = BitmapFactory.decodeStream(fi);
		} catch (Exception ex) {
		Log.e("getThumbnail() on internal storage", ex.getMessage());
		}
		}
		
		return thumbnail;
	}

	public boolean isSdReadable() {
	
		try{
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();
	
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = true;
			Log.i("isSdReadable", "External storage card is readable.");
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				Log.i("isSdReadable", "External storage card is readable.");
				mExternalStorageAvailable = true;
			} else {
				// Something else is wrong. It may be one of many other
				// states, but all we need to know is we can neither read nor write
				mExternalStorageAvailable = false;
		}
	
			return mExternalStorageAvailable;
		}catch(Exception e){
			Log.e("error in checking", e.toString());
		}
		return false;
	}
	
	//display Internet not available alert dialog			
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
         
        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
    	        Log.d("intent", "redirect to msg activity");
    	        startActivity(ii);
    	        finish();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }

}