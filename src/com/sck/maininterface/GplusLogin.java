package com.sck.maininterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.sep.global_welcome.Base64;
import com.sep.global_welcome.MainActivity;

public class GplusLogin extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;
	private static final String TAG = "GplusLogin";
	private static final int PROFILE_PIC_SIZE = 400;
	private GoogleApiClient mGoogleApiClient;
	private String file2 = "myphoneNo";
	
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut, btnRevokeAccess,btnUploadImage;
	private ImageView imgProfilePic;
	private TextView txtName, txtEmail;
	private LinearLayout llProfileLayout;
	Bitmap mIconUpdate=null;
	String URl;
	public final static String APP_PATH_SD_CARD = "/profileImages/";
	public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
	String imageName = "";
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gplus_login);

		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
		btnUploadImage = (Button) findViewById(R.id.btn_upload_image);
		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		txtName = (TextView) findViewById(R.id.txtName);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

		
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnRevokeAccess.setOnClickListener(this);
		btnUploadImage.setOnClickListener(this);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this) // Initializing google plus client 
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	//Solve sign in Errors
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this,RC_SIGN_IN);
			}
			catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
		
			mConnectionResult = result; // stores connection result

			if (mSignInClicked) {

				resolveSignInError(); // method called to resolve sign in Error
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		getProfileInformation(); // call the method to get user's information
		updateViewUI(true);

	}

	// Here we are hiding and showing the UI elements as per the requests
	private void updateViewUI(boolean isSignedIn) {
		if (isSignedIn) {
			btnSignIn.setVisibility(View.GONE);
			btnSignOut.setVisibility(View.VISIBLE);
			btnRevokeAccess.setVisibility(View.VISIBLE);
			btnUploadImage.setVisibility(View.VISIBLE);
			llProfileLayout.setVisibility(View.VISIBLE);
		} else {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignOut.setVisibility(View.GONE);
			btnRevokeAccess.setVisibility(View.GONE);
			btnUploadImage.setVisibility(View.GONE);
			llProfileLayout.setVisibility(View.GONE);
		}
	}

	
	//geting user-name, email address , photo-url  
	
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
	//get the current logged in person
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				
				//Log.e(TAG, "Name: " + personName + ", plusProfile: "
				//		+ personGooglePlusProfile + ", email: " + email
				//		+ ", Image: " + personPhotoUrl);

				txtName.setText(personName);
				txtEmail.setText(email);

				
				//changing the  photoUrl
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				//sending the image URL with the method LOadProfileImage
				new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} 
			else 
			{
				//if person is not exist
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateViewUI(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			
			signInWithGplus();
			break;
		case R.id.btn_sign_out:
			
			signOutFromGplus();
			break;
		case R.id.btn_revoke_access:
			
			revokeGplusAccess();
			break;
		
		case R.id.btn_upload_image:
			new uploadProImgAsync().execute(mIconUpdate);
			
		}
	}


	// method to sign in with g-plus
	private void signInWithGplus() {

		try{
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
		}
		catch(Exception e)
		{
			
			Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
			
		}
	}

	
	//signout from g-plus
	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateViewUI(false);
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.connect();
							updateViewUI(false);
						}

					});
		}
	}

	//background asynctask to load the profile picture from URL
	//private class Load
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				//mIcon11=BitmapFactory;
				mIcon11 = BitmapFactory.decodeStream(in);
			} 
			
			catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			mIconUpdate= mIcon11;
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	//Upload image to server
	private class uploadProImgAsync extends AsyncTask<Bitmap, Integer, String>{

		private ProgressDialog Dialog = new ProgressDialog(GplusLogin.this);
		
		 @Override  
	        protected void onPreExecute()  
	        {  
	        	Dialog.setMessage("Uploading Google+ Image...");
	            Dialog.show();
	        }
		
		@Override
		protected String doInBackground(Bitmap... bit) {
			Bitmap bitmap = bit[0];
			InputStream is;
			BitmapFactory.Options bfo;
			Bitmap bitmapOrg;
			ByteArrayOutputStream bao ;
			 
			bfo = new BitmapFactory.Options();
			bfo.inSampleSize = 2;
			
			bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte [] ba = bao.toByteArray();
			String ba1 = Base64.encodeBytes(ba);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			String phoneNo = read(file2);
			nameValuePairs.add(new BasicNameValuePair("image",ba1));
			nameValuePairs.add(new BasicNameValuePair("cmd",phoneNo+".jpg"));
			Log.v("log_tag", System.currentTimeMillis()+".jpg");
			
			try{
				HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new 
		        //HttpPost("http://www.kasunbuddhima.net63.net/phpserver/Profiles/uploadProImage.php");
		        HttpPost("http://sepwecom.preview.kyrondesign.com/phpserver/Profiles/uploadProImage.php");
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        Log.v("log_tag", "In the try Loop" );
			}catch(Exception e){
				Log.v("log_tag", "Error in http connection "+e.toString());
			}
			
			return "Success";
		}
		
		protected void onPostExecute(String result) {
			Dialog.dismiss();
			String phoneNo = read(file2);
			Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
			//URl = "http://www.kasunbuddhima.net63.net/phpserver/Profiles/"+phoneNo+".jpg";
			startActivity(new Intent(GplusLogin.this,MainActivity.class));
				
		/*	URl = "http://sepwecom.preview.kyrondesign.com/phpserver/Profiles/"+phoneNo+".jpg";
			DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URl);*/
			saveImageToExternalStorage(mIconUpdate);
		}
		
	}
	
	//download image
	private class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
		private ProgressDialog Dialog = new ProgressDialog(GplusLogin.this);
		
		@Override  
        protected void onPreExecute()  
        {  
        	Dialog.setMessage("Loading data...");
            Dialog.show();
        }
		@Override
		protected Bitmap doInBackground(String... uri) {
			String url = uri[0];
			Bitmap bitmap = null;
			try{
                bitmap = downloadUrl(url);
                Log.d("bitmap downloaded",bitmap.toString());
                saveImageToExternalStorage(bitmap);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return bitmap;
            
			//return null;
		}
		@Override
        protected void onPostExecute(Bitmap result) {
			Dialog.dismiss();
            ImageView iView = (ImageView) findViewById(R.id.imageView1);
            iView.setImageBitmap(result);
        }
		
	}
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
	
	public boolean saveImageToExternalStorage(Bitmap image) {
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
			dir.mkdirs();
		}

		OutputStream fOut = null;
		File file = new File(fullPath, imageName);
		file.createNewFile();
		fOut = new FileOutputStream(file);
		Log.d("pass", file+"file is directory and i name"+imageName);

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
	
	
	
	// reding mobile no
	public String read(String file) {
		try {
			FileInputStream fin = openFileInput(file);
			int c;
			String temp = "";

			while ((c = fin.read()) != -1) {
				temp = temp + Character.toString((char) c);
			}
			// et.setText(temp);

			return temp;
		} catch (Exception e) {
			Log.e("Read Error", e.toString());
		}

		return "";

	}
	


}
