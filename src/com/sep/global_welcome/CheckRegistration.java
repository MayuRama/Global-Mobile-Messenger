package com.sep.global_welcome;

import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CheckRegistration extends Activity {
	   private String file = "mydata";
	   private String file1 = "myphoneNo";
	  // File file1 = new File(file);
	   
	   
	   protected void onCreate(Bundle savedInstanceState){
		   super.onCreate(savedInstanceState);
		   
		   String code = read(file);
		   String pCode = read(file1);
		   if(code.equals("") && pCode.equals("")){
			   //Goes to welcome page
			   Intent i2 = new Intent(getApplicationContext(),ContactInforActivity.class);
			   startActivity(i2);
			   finish();
		   }else{
			   Intent i1 = new Intent(getApplicationContext(),MainActivity.class);
			   //Goes to inbox
			   i1.putExtra("phone", pCode);
			   startActivity(i1);
			   finish();
		   }
	   }
	
	public String read(String file){
	      try{
	         FileInputStream fin = openFileInput(file);
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