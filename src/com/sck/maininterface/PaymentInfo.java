package com.sck.maininterface;

import android.R.bool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.Menu;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;
import com.sep.global_welcome.MainActivity;

import org.json.JSONException;

import java.math.BigDecimal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class PaymentInfo extends Activity {

	ActionBar actionBar;
	private static final String TAG = "paymentExample";

	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

	private static final String CONFIG_CLIENT_ID = "AdKzfhDNbETD96Y77Gk2wyDYXK9kzgmUS4ESAamQ5JXb_7HXKwSQRLMepS-T";

	private static final int REQUEST_CODE_PAYMENT = 1;
	// private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

	TextView printPhoneNo, printExpDate;
	RadioGroup rgSelect;
	String price;
	int selectedId, yearExtended;
	private String file2 = "myphoneNo";
	private String file3 = "expdate";
	static JSONObject json_data = null;
	String data;
	String year;
	String monthDate;
	String renewedDate;
	InputStream is = null;
	String line = null;
	String result = null;
	String part2;
	int code;
	boolean paymentSuccess= false;
	//-- Configuring PayPal Account----
	private static PayPalConfiguration config = new PayPalConfiguration()
			.environment(CONFIG_ENVIRONMENT)
			.clientId(CONFIG_CLIENT_ID)
			.merchantName("Global Messenger")
			.merchantPrivacyPolicyUri(
					Uri.parse("http://sepwecom.preview.kyrondesign.com"))
			.merchantUserAgreementUri(
					Uri.parse("http://sepwecom.preview.kyrondesign.com"));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_info);
		actionBar=getActionBar(); 
	    actionBar.setTitle("Payment Info");
	    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80b5e1")));

		rgSelect = (RadioGroup) findViewById(R.id.rgOption);
		rgSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				selectedId = rgSelect.indexOfChild(findViewById(checkedId));
				// Toast.makeText(getBaseContext(),
				// "Method 1 ID = "+String.valueOf(selectedId),
				// Toast.LENGTH_SHORT).show();
			}
		});

		final String phoneNo = read(file2);
		final String expiryDate = read(file3);

		printPhoneNo = (TextView) findViewById(R.id.textPhoneNo);
		printExpDate = (TextView) findViewById(R.id.textExpDate);

		printPhoneNo.setText("+" + phoneNo);
		printExpDate.setText(expiryDate);

		year = expiryDate.substring(0, 4);
		monthDate = expiryDate.substring(4, 10);
		
		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		startService(intent);
		
		new updateExpiryDate().execute();
		
		
	}


	public void onBuyPressed(View pressed) {

		if (selectedId == 0) {
			price = "0.99";
			yearExtended = 1;
			renewedDate = Integer.toString(Integer.parseInt(year) + 1)
					+ monthDate;
		}
		if (selectedId == 1) {
			price = "2.67";
			yearExtended = 3;
			renewedDate = Integer.toString(Integer.parseInt(year) + 3)
					+ monthDate;
		}
		if (selectedId == 2) {
			price = "3.71";
			yearExtended = 5;
			renewedDate = Integer.toString(Integer.parseInt(year) + 5)
					+ monthDate;
		}

		PayPalPayment yearToRenew = getThingToRenew(PayPalPayment.PAYMENT_INTENT_SALE);
		Intent intent = new Intent(PaymentInfo.this, PaymentActivity.class);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, yearToRenew);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);

	}

	private PayPalPayment getThingToRenew(String paymentIntent) {

		return new PayPalPayment(new BigDecimal(price), "USD",
				Integer.toString(yearExtended) + " Year License", paymentIntent);

	}

	private void addAppProvidedNumber(PayPalPayment paypalPayment) {
		final String phoneNo = read(file2);
		ShippingAddress phoneNumber = new ShippingAddress()
				.recipientName(phoneNo);
		paypalPayment.providedShippingAddress(phoneNumber);
	}

	private void enablePhoneNumberRetrieval(PayPalPayment paypalPayment,
			boolean enable) {
		paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {

			if (resultCode == Activity.RESULT_OK) {

				PaymentConfirmation confirm = data
						.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

				if (confirm != null) {

					
					try {
						Log.i(TAG, confirm.toJSONObject().toString(4));
						Log.i(TAG, confirm.getPayment().toJSONObject()
								.toString(4));

					/*	Toast.makeText(
								getApplicationContext(),
								"Payment Confirmation info received from PayPal",
								Toast.LENGTH_LONG).show();*/
						
						
						AlertDialog ad = new AlertDialog.Builder(this).create();  
						ad.setCancelable(false); // This blocks the 'BACK' button  
						ad.setMessage("Payment Confirmation info received from PayPal");  
						ad.setButton("OK", new DialogInterface.OnClickListener() {  
						    @Override  
						    public void onClick(DialogInterface dialog, int which) {  
						        dialog.dismiss();                      
						    }  
						});  
						ad.show();
						//new updateExpiryDate().execute();
						//paymentSuccess=true;
						saveExpDate(renewedDate);

					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ",
								e);
					}
				}
			}

			else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i(TAG, "The user canceled.");
			}

			else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i(TAG,
						"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
			}
		}

		// /

	}

	private void sendAuthorizationToServer(PayPalAuthorization authorization) {

	}

	@Override
	public void onDestroy() {
		// Stop service when done
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}

	/*
	 * private class getExpiryDate extends AsyncTask<Void, String, Void>{
	 * 
	 * @Override protected Void doInBackground(Void... arg0){
	 * 
	 * Bundle extras = getIntent().getExtras(); String phoneNo = read(file2);
	 * 
	 * ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 * nameValuePairs.add(new BasicNameValuePair("pnone",phoneNo )); try{
	 * 
	 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost = new
	 * HttpPost("http://kasunbuddhima.net63.net/phpserver/getuserName.php");
	 * httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	 * HttpResponse response = httpclient.execute(httppost); }
	 * 
	 * } }
	 */
	
	//---------------Reading Mobile No----------------------------------------------

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
	
	//---------------Expiry Date Related--------------------------------------------
	
	@SuppressWarnings("deprecation")
	public void saveExpDate(String s) {
		data = s;
		try {

			FileOutputStream fOut2 = openFileOutput(file3, MODE_WORLD_READABLE);
			fOut2.write(data.getBytes());
			fOut2.close();
			Log.w("file saved", "image name is saved in expDatefile");
			new updateExpiryDate().execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// updating Expiry Date expiry date

	private class updateExpiryDate extends AsyncTask<Void, Integer, Void> {

		private ProgressDialog Dialog = new ProgressDialog(PaymentInfo.this);
		
		 @Override  
	        protected void onPreExecute()  
	        {  
	        	Dialog.setMessage("Uploading Image...");
	            Dialog.show();
	        }
		

		@Override
		protected Void doInBackground(Void... arg0) {

			Bundle extras = getIntent().getExtras();
			if (extras != null) {

				String phoneNo = read(file2);
				String eDate = renewedDate;
				Log.d("pass", renewedDate);
				Log.d("pass", phoneNo);
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("phone", phoneNo));
				nameValuePairs.add(new BasicNameValuePair("expdate", eDate));

				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(
							"http://sepwecom.preview.kyrondesign.com/phpserver/updateExpDate.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httppost);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();

					Log.e("pass 1", "connection success");
					Log.e("pass 1", "Expiry Date :" + eDate);
					Log.e("pass 1", "phone:" + phoneNo);
				} catch (Exception e) {
					Log.e("Fail 1", e.toString());
					
				}
				/*
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					result = sb.toString();
					String[] parts = result.split(">");
					part2 = parts[1];
					Log.e("pass 2", "connection success ");
				} 
				catch (Exception e) {
					Log.e("Fail 2", e.toString());
				}

				try {
					JSONObject json_data = new JSONObject(part2);
					code = (json_data.getInt("code"));

				} catch (Exception e) {
					Log.e("Fail 3", e.toString());
				}*/
				
			}
			return null;
			
		}
		protected void onPostExecute(Void result) {
			Dialog.dismiss();
		}

	}

}
