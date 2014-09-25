package com.sck.maininterface;

import java.util.ArrayList;

import com.sck.maininterface.R;

import android.R.id;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Adapter extends BaseAdapter {

	
	private Context mContext;
	private ArrayList<Message> mMessages;
	
	public Adapter(Context context, ArrayList<Message> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Message message=(Message) this.getItem(position);
		
		ViewHolder holder;
		
		if (convertView == null)
		{
			holder=new ViewHolder();
			convertView =LayoutInflater.from(mContext).inflate(R.layout.message_row, parent, false);
			holder.message=(TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		}
		else
			
			holder=(ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getMessage());
		
		LayoutParams lp=(LayoutParams) holder.message.getLayoutParams();
		
		if (message.isMine())
		{
			holder.message.setBackgroundResource(R.drawable.send_hdpi);
			lp.gravity=Gravity.RIGHT;
			
		}
		else
		{
			holder.message.setBackgroundResource(R.drawable.recieve_hdpi);
			lp.gravity=Gravity.LEFT;
			
		}
		
		holder.message.setLayoutParams(lp);
		
		
		return convertView;
		
		
		
	}
	
	private static class ViewHolder
	{
		TextView message;
	}

}
