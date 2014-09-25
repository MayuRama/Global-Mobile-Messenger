package com.sep.global_welcome;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sck.maininterface.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactInforActivity extends Activity {

	AutoCompleteTextView country;
	TextView countryCode;
	Spinner spinner1;
	ImageView ImgFlag;
	Button btncontinue;
	EditText phone;
	TelephonyManager tel;

	ActionBar actionBar;

	// static Socket socket;
	// private static PrintWriter printWriter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);

		actionBar = getActionBar();
		actionBar.setTitle("Verify your phone number");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#80b5e1")));

		tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		final String counCode = tel.getSimCountryIso().toString();
		Log.d("country code", counCode);
		// get the country
		// String country1 = GetCountryZipCode(counCode);
		// Toast.makeText(getApplicationContext(),"coun:"+country1,
		// Toast.LENGTH_LONG).show();

		final String[] arr = getResources()
				.getStringArray(R.array.CountryCodes);
		String[] coun = new String[1000];

		for (int i = 0; i < arr.length; i++) {
			String a = arr[i];
			String[] pair = a.split(",");
			coun[i] = GetCountryZipCode(pair[1].trim());
		}

		// Toast.makeText(getApplicationContext(),coun[5],
		// Toast.LENGTH_LONG).show();

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, coun);
		country = (AutoCompleteTextView) findViewById(R.id.txtCountryName);

		country.setAdapter(adapter1);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// final String[ ]
		// recourseList=this.getResources().getStringArray(R.array.CountryCodes);

		// Write spinner and arrayAdapter for list the String stored in array
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.CountryCodes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter);

		final Context context = this;

		// to get the on selected item of the spinner
		
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				// your code here
				getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				String text = spinner1.getSelectedItem().toString();

				// to hold the two string values of the selected item
				String[] code = text.split(",");
				country = (AutoCompleteTextView) findViewById(R.id.txtCountryName);
				country.setText(GetCountryZipCode(code[1]).trim());

				countryCode = (TextView) findViewById(R.id.txtCounCode);

				countryCode.setText(code[0]);

				ImgFlag = (ImageView) findViewById(R.id.imgViewFlag);
				// String pngName = code[1];
				String pngName = code[1].trim().toLowerCase();

				String uri = "drawable/" + pngName;
				int imageResource = context.getResources().getIdentifier(uri,
						null, context.getPackageName());
				try {
					Drawable image = context.getResources().getDrawable(
							imageResource);
					ImgFlag.setImageDrawable(image);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				// ///////////////////////

				if (counCode != "") {
					for (int i = 0; i < arr.length; i++) {
						String a = arr[i];
						String[] pair = a.split(",");
						String cc = pair[1].toLowerCase();
						// Toast.makeText(getApplicationContext(),counCode,
						// Toast.LENGTH_LONG).show();

						if (cc.equals(counCode.toLowerCase())) {
							countryCode = (TextView) findViewById(R.id.txtCounCode);
							String c = pair[0];
							countryCode.setText(c);
							country.setText(GetCountryZipCode(pair[1]).trim());
							String pngName1 = pair[1].trim().toLowerCase();
							ImgFlag = (ImageView) findViewById(R.id.imgViewFlag);
							String uri1 = "drawable/" + pngName1;
							int imageResource1 = context.getResources()
									.getIdentifier(uri1, null,
											context.getPackageName());
							try {
								Drawable image = context.getResources()
										.getDrawable(imageResource1);
								ImgFlag.setImageDrawable(image);
								//Toast.makeText(getApplicationContext(),
								//"Your Country is detected.",
										//Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								Log.e("error", e.toString());
								Toast.makeText(getApplicationContext(),
										"Country cannot detected.",
										Toast.LENGTH_LONG).show();
							}
						}
					}
				}

				// //////////////////////

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}

		});
		// ///////////////////////////////////////////////////////////////////////////////

		for (int i = 0; i < arr.length; i++) {
			String a = arr[i];
			String[] pair = a.split(",");
			String cc = pair[1].toLowerCase();
			// Toast.makeText(getApplicationContext(),counCode,
			// Toast.LENGTH_LONG).show();
			if (cc.equals(counCode.toLowerCase())) {
				// Toast.makeText(getApplicationContext(),cc,
				// Toast.LENGTH_LONG).show();
				String detectedCountry = GetCountryZipCode(cc);
				country.setText(detectedCountry);
				// countryCode.setText(pair[0]);

				String pngName = pair[1].trim().toLowerCase();
				/*
				 * String uri = "drawable/"+pngName; int imageResource =
				 * context.getResources().getIdentifier(uri, null,
				 * context.getPackageName()); try{ Drawable image =
				 * context.getResources().getDrawable(imageResource);
				 * ImgFlag.setImageDrawable(image); }catch(Exception e){
				 * Toast.makeText(getApplicationContext(),e.getMessage(),
				 * Toast.LENGTH_LONG).show(); }
				 */
			}
		}

		// Continue button id initialization
		btncontinue = (Button) findViewById(R.id.btnContinue);
		// set the default color of the button
		btncontinue.setBackgroundColor(Color.rgb(54, 120, 242));
		// continue btn click event
		btncontinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btncontinue.setBackgroundColor(Color.BLUE);

				countryCode = (TextView) findViewById(R.id.txtCounCode);
				phone = (EditText) findViewById(R.id.txtPhoneNo);

				// String c = (String) countryCode.getText()+ phone.getText();
				CharSequence c = countryCode.getText();
				final String phoneNo = c.toString()+ phone.getText();
				
				
				AlertDialog.Builder builder=new AlertDialog.Builder(ContactInforActivity.this);
				builder.setTitle("Confirm number");
				builder.setMessage("Is this your phone number ?\n\n"+phoneNo);
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Pattern pattern = Pattern.compile("\\d{9}");
						String numbers = phoneNo.substring(Math.max(0, phoneNo.length() - 9));  //extract the last 9 digits
						Log.d("last 9 digits:",numbers);
						// Pattern.compile("^[+]?([0-9]*[\.\s\-\(\)]|[0-9]+){3,24}$");
						Matcher matcher = pattern.matcher(numbers);	//check whether user entered 9 digits number

						if (matcher.matches()) {
							// generate a 5 digit value;
							Random random = new Random();
							int value = random.nextInt(10000);
							String v = Integer.toString(value);
							// Toast.makeText(getApplicationContext(),v,
							// Toast.LENGTH_LONG).show();
							sendEmail(v, phoneNo);

							Intent i = new Intent(getApplicationContext(),
									VerifyActivity.class);
							i.putExtra("verifyCode", v);
							i.putExtra("phone", phoneNo);
							startActivity(i);
							finish();
						} else {
							btncontinue.setBackgroundColor(Color.rgb(54, 120, 242));
							Toast.makeText(
									getApplicationContext(),
									"Invalid Phone Number!!",
									Toast.LENGTH_LONG).show();
						}
						
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
				
				
				AlertDialog alert=builder.create();
				alert.show();
				
				
			}
		});

	}

	// send mail
	protected void sendEmail(String no, String phone) {
		Log.i("Send email", "");

		String[] TO = { "kasun.buddhima@gmail.com" };

		String Verifyno = no;
		String mobile = "+" + phone;
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
		emailIntent.putExtra(Intent.EXTRA_TEXT, Verifyno);

		/*try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			finish();
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(),
					"There is no email client installed.", Toast.LENGTH_SHORT)
					.show();
		}*/

		// /////////////////////////////////////////////////////////////////////////
		// send as a sms message
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(mobile, null, Verifyno, null, null);
			//Toast.makeText(getApplicationContext(), "SMS sent.",
					//Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	// to get the country name
	private String GetCountryZipCode(String ssid) {
		Locale loc = new Locale("", ssid);

		return loc.getDisplayCountry().trim();
	}

	// /////////////////////////////////////////

}
