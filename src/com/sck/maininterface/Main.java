package com.sck.maininterface;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;



import com.sck.maininterface.navio.MainActivity;
import com.sck.maininterface.InboxAdapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main extends ListActivity {

	ActionBar actionBar;
	public static String CONTACT_NAME="contact_name";
	public static String CONTACT_NUMBER="contact_number";
	public static int INBOX_COUNT=0;
	private Context context;
	public final static String APP_PATH_SD_CARD = "/profileImages/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "wallpapers";
    InputStream is=null;
    StringBuilder sb; 
    static JSONObject json_data = null;
    static String result="";
    String line=null;
    String part2;
    String status;
    static int count = 0;
    
    // sms reminder iteration 5 by kanushka
    final CharSequence[] choiceList = {"Put Reminder", "Send by Network", "Cancle"};
    final CharSequence[] choiceList2 =  {"Put Draft Reminder", "View Draft Reminders", "Cancle"};
    
	@SuppressWarnings("rawtypes")
	private ArrayAdapter inboxMessageArrayAdapter;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       ////////////////////
      //to set the background chat wall paper
		 RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.main_bk);
		 try{
		 Bitmap bit = getThumbnail("wallpaper.jpg");
		 Resources res = getResources(); //resource handle
		 @SuppressWarnings("deprecation")
		BitmapDrawable draw = new BitmapDrawable(bit);
		 Log.e("pass 1", "layout back");
		 if(Build.VERSION.SDK_INT >= 16)
			 rLayout.setBackground(draw);
			else
				rLayout.setBackgroundDrawable(draw);
		 Log.e("pass 2", "layout back ");
		 }catch(Exception e){
			 Log.e("no selected","no image is set as background by usesr");
		 }
       ////////////////////
		 
		 
       actionBar=getActionBar(); 
       actionBar.setTitle("Chats");
       actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));
       actionBar.setIcon(R.drawable.dcd);
       
      // Toast.makeText(Main.this,"RESPOND ="+Introduction.RESPOND + "\n STORED_RESPOND ="+ Introduction.STORED_RESPOND  , 5000).show();
       
       
       inboxMessageArrayAdapter=new InboxAdapter(this, new String[INBOX_COUNT]);
       setListAdapter(inboxMessageArrayAdapter); 
       
       if (Introduction.RESPOND == 0)
       {
    	   startActivity(new Intent(Main.this, Introduction.class));
    	  
       }
       CheckConnection cc = new CheckConnection(getApplicationContext());
       Boolean isInternetPresent = cc.isConnectingToInternet();
       
      
       if(isInternetPresent){
    	   new BackgroundImageStatusAsync().execute();
       }else{
    	   showAlertDialog(Main.this, "No Internet Connection", "You don't have internet connection.", false);
       }
    
        
    }
	
	
	private class BackgroundImageStatusAsync extends AsyncTask<String, Integer, String> {
		private ProgressDialog Dialog = new ProgressDialog(Main.this);
		protected void onPreExecute()  
	    {  
			Dialog.setMessage("Loading data...");
            Dialog.show();
	    }
		
		@Override
		protected String doInBackground(String... arg0) {
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/checkImageStates.php");
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
		        Log.e("pass 1", "connection success ");
			}catch(Exception e){
				Log.e("fail 1", e.toString());
			}
			//////////////////////////
			try{
	    		 
	          	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	          	sb = new StringBuilder();
	            //sb.append(reader.readLine() + "\n"); 	
	             while ((line = reader.readLine()) != null)
	             {	 
	        	    sb.append(line + "\n");
	             }
	             is.close();	
	            // result = sb.toString();
	             result = sb.toString().substring(0, sb.toString().length()-1);
	             Log.e("pass 2 result", result);
	             Log.e("pass 2", "connection success ");
	             //String[] parts = result.split(">");
	             //part2 = parts[1];
	             //Log.e("pass 2", part2);
			}catch(Exception e){
				Log.e("fail 2", e.toString());
	        }
	         
			////////////////////////////
			try{
	    		
	         	 json_data = new JSONObject(result);
	         	
	         	Log.e("pass 3", "Entered to try block");
	         	status = (json_data.getString("status"));
	         	Log.e("pass 3", "status: " + status);
			}catch(Exception e){
				Log.e("fail 3", e.toString());
			}
			
			return status;
		}
		
		@SuppressWarnings("deprecation")
		@Override  
        protected void onPostExecute(String result)  
        { 
			 Dialog.dismiss();
			 Log.e("onPostExecute", "status: " + result);
			 if((result.equals("1") && (count==0))){
				 showAlertDialogforWallpaper(Main.this, "New wallpaper Add from Global", "Do you want to Download?", true);
				 count++;
				 
			 }
        }
		
		
		
	}
	
	//display if downlodable wallpaper is available		
	@SuppressWarnings("deprecation")
	public void showAlertDialogforWallpaper(Context context, String title, String message, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
         
        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	new DownloadTask().execute();
            }
        });
       
        alertDialog.setButton2("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
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
            	Intent ii = new Intent(getApplicationContext(),Settings.class);
    	        Log.d("intent", "redirect to msg activity");
    	        startActivity(ii);
    	        finish();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
			
			
	
	
	
	private class DownloadTask extends AsyncTask<Void, Integer, Bitmap> {
		private ProgressDialog Dialog = new ProgressDialog(Main.this);
		
		@Override  
        protected void onPreExecute()  
        {  
        	Dialog.setMessage("Downloaing wallpaper...");
            Dialog.show();
        }
		
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			String url = "http://sepwecom.preview.kyrondesign.com/phpserver/wallpaper/wallpaper.jpg";
			Bitmap bitmap = null;
			try{
				bitmap = downloadUrl(url);
				saveImageToExternalStorage(bitmap);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
				//bitmap = null;
			}
			return bitmap;
		}
		@Override
        protected void onPostExecute(Bitmap result) {
			Dialog.dismiss();
			try{
				
				if(result == null){
					Intent iii = new Intent(getApplicationContext(),MessageActivity.class);
			        Log.d("intent with extras", "redirect to msg activity");
			        iii.putExtra("msg", "No downloadable wallpapers set");
			        startActivity(iii);
			        finish();
				}else{
					Intent ii = new Intent(getApplicationContext(),MessageActivity.class);
			        Log.d("intent", "redirect to msg activity");
			        startActivity(ii);
			        finish();
				}
			}catch(Exception e){
				Log.e("error",e.toString());
			}
   
        }
		
	}
	
	public boolean saveImageToExternalStorage(Bitmap image) {
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
				//dir.getParentFile().mkdirs();
				//dir.createNewFile();
			}
		//dir.createNewFile();
		OutputStream fOut = null;
		//imageName = read(file);
		File file = new File(fullPath, "wallpaper.jpg");
		Log.d("pass", file+"file is directory"+"wallpaper.jpg");
		file.createNewFile();
		
		//.getParentFile().createNewFile();
		Log.d("pass", "file createtd");
		fOut = new FileOutputStream(file);
		
		// 100 means no compression, the lower you go, the stronger the compression
		image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();

		MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

		return true;

		} catch (Exception e) {
			Log.e("saveToExternalStorage()", e.getMessage());
			return false;
		}
	}
	
	public boolean isdReadable() {
		
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
	
	//download userImage uri
			@SuppressWarnings("unused")
			private Bitmap downloadUrl(String strUrl) throws IOException{
		        Bitmap bitmap=null;
		        InputStream iStream = null;
		        try{
		            URL url = new URL(strUrl);
		            // Creating an http connection to communcate with url 
		            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		            urlConnection.connect();
		            //Reading data from url
		            iStream = urlConnection.getInputStream();
		            //Creating a bitmap from the stream returned from the url
		            bitmap = BitmapFactory.decodeStream(iStream);
		        }catch(Exception e){
		            Log.d("Exception while downloading url", e.toString());
		        }finally{
		            iStream.close();
		        }
		        
		        return bitmap;
		    }
	

	


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       
    	getMenuInflater().inflate(R.menu.main_ativity_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_search:
			//TODO
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
			break;
		case R.id.action_new_chat:
		startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI), 1);
		INBOX_COUNT++;
		//Toast.makeText(Main.this, CONTACT_NAME +"," + CONTACT_NUMBER, Toast.LENGTH_LONG).show();
		
		break;
		case R.id.action_start_navio:
			startActivity(new Intent(Main.this, MainActivity.class));
		break;
		case R.id.action_contacts:
		
		break;
		case R.id.action_new_group:
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
		break;
		case R.id.action_settings:
			startActivity(new Intent(Main.this, Settings.class));
		break;
		case R.id.action_status:
			//startActivity(new Intent(Main.this, Status.class));
			Toast.makeText(this, "This feature is not available in the Alpha Release", Toast.LENGTH_LONG).show();
		break;
		case R.id.action_start_chat:
		startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI), 1);
		INBOX_COUNT++;
		
		
		break;
		
		case R.id.action_draft_reminder:
			/*// SMS reminder iteration 5 by kanushka
        	AlertDialog.Builder makeChoice2 = new AlertDialog.Builder(Main.this);
			makeChoice2.setTitle("What Do You Want?");
			// Setting Icon to Dialog
			//makeChoice2.setIcon(R.drawable.menupac);
			
			makeChoice2.setItems(choiceList2, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
					if(choiceList2[arg1].equals("Put Draft Reminder"))
					{
						Intent draftSave = new Intent(Main.this,SaveDraftsActivity.class);
						startActivity(draftSave);
					}
					else if(choiceList2[arg1].equals("View Draft Reminders"))
					{
						
						Intent draftRem = new Intent(Main.this,DraftRemindersListActivity.class);
						startActivity(draftRem);
						
					}
					else if(choiceList2[arg1].equals("Cancle"))
					{
						arg0.dismiss();
					
					}
					
				}
			});
			makeChoice2.show();*/
		break;
		
		default:
			return super.onOptionsItemSelected(item);
			
		}
		return false;
    	
    }
    @SuppressWarnings("deprecation")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch (requestCode)
    	{
    	case 1:
    		if (resultCode == Activity.RESULT_OK)
    		{
    			Uri contactData=data.getData();
    			@SuppressWarnings("deprecation")
				Cursor c=managedQuery(contactData, null, null, null, null);
    			
    			if (c.moveToFirst())
    			{
    				CONTACT_NAME=c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    				String CONTACT_ID=c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
    				Cursor phones=getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "="+CONTACT_ID, null, null);
    				
    				while (phones.moveToNext())
    				{
    					//CONTACT_NUMBER=phones.getString(phones.getColumnIndex(Phone.NUMBER));
    					//startActivity(new Intent(Main.this, MessageActivity.class));
    					
    					 Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    				                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
    				 
    				                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
    				                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
    				                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
    				 
    				                new String[]{CONTACT_ID},
    				                null);
    				 
    				        if (cursorPhone.moveToFirst()) {
    				        	CONTACT_NUMBER = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    				        }
    				 
    				        cursorPhone.close();
    				 
    				        startActivity(new Intent(Main.this, MessageActivity.class));
    				        
    					
    				}
    				
    				
    			}
    			
    		}
    	
    	
    	break;
    	
    	}
    	
    	
    }
    
    
    
}
