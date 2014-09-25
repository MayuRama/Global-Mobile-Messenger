package com.sck.maininterface;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class InboxAdapter extends ArrayAdapter{
	
	private LayoutInflater inflater;
	
	
	

	@SuppressWarnings("unchecked")
	public InboxAdapter(Activity activity, String[] items) {
		super(activity,R.layout.row_inbox,items);
		
		inflater=activity.getWindow().getLayoutInflater();
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if  (convertView == null)
		{
			convertView= inflater.inflate(R.layout.row_inbox, parent,false);
		}
		
		TextView nameTextView=(TextView) convertView.findViewById(R.id.NameTextView);
		TextView lastMsgTextView=(TextView) convertView.findViewById(R.id.MessageBodyTextView);
		TextView datetimeTextView=(TextView) convertView.findViewById(R.id.DateTimeTextView);
		
		nameTextView.setText(Main.CONTACT_NAME);
		lastMsgTextView.setText(MessageActivity.MESSSAGE_CONTENT);
		datetimeTextView.setText(MessageActivity.DateTime);
		
		
		
		return convertView;
		
		
		
		
	}

}
