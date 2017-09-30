package com.zyl11123ok.deRun;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.zyl11123ok.deRun.R;
import com.zyl.deRun.DataBase.DataBase;

public class View1 extends ActionBarActivity{
	private ListView list;
	private Button btnBack,btnForward;
	private int mYear,mMonth,mDay;
	private TextView tvTitle;
	String[] from;
	String today;
	int[] to;
	int _id;
	SimpleCursorAdapter mAdapter;
	DataBase db;
	Cursor cur;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view1);
		db=new DataBase(this);
		db.CreatTable_amount();
		
		list=(ListView)findViewById(R.id.listView1);
		Calendar c = Calendar. getInstance(TimeZone.getTimeZone("GMT+08:00"));
		mYear = c.get(Calendar.YEAR);
	    mMonth = c.get(Calendar.MONTH)+1;
	    mDay=c.get(Calendar.DAY_OF_MONTH);
	    if(mMonth<10)
		{today = mYear + "-" + "0"+mMonth;}
	    else
	    {today=mYear+"-"+mMonth;}
	    cur=db.getAmount(today);
		
		from=new String[]{"date","Distance"};
		to=new int[]{R.id.textView1,R.id.textView2};
		cur=db.getAmount(today);
		mAdapter = new SimpleCursorAdapter(this, R.layout.items, cur,from, to);
		list.setAdapter(mAdapter);
		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView
					<?>  Vparent, android.view.View view,
					int position, long id) {
				// TODO Auto-generated method stub
				_id=(int)id;
				new AlertDialog.Builder(View1.this).setTitle("提示").setMessage(
						"确定删除该明细?").setIcon(R.drawable.ic_launcher).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
		                        //Log.v("",""+_id);
								 db.delAmount(_id);
								 mAdapter.changeCursor(cur);
							 	 ((SimpleCursorAdapter) mAdapter).notifyDataSetChanged();
								finish();
								 System.out.println("ok");
								 list.setAdapter(mAdapter);
							
							}
						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).show();

				return false;
			}
		});
		tvTitle=(TextView)findViewById(R.id.tvTitle);
		tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+mYear+"-"+(mMonth+1)+"-"+"01");
		btnBack=(Button)findViewById(R.id.btnBack);
		btnForward=(Button)findViewById(R.id.btnForward);
		ButtonListener buttonlistener =new ButtonListener();
		btnBack.setOnClickListener(buttonlistener);
		btnForward.setOnClickListener(buttonlistener);
	}
	class ButtonListener implements OnClickListener
    {
    	public void  onClick(android.view.View v)
    	{
    		switch(v.getId())
    		{
    			case R.id.btnBack:leftView();
    			break;
    			case R.id.btnForward:rightView();
    			break;
    	
    		}
    	}

		private void rightView() {
			Calendar c = Calendar. getInstance(TimeZone.getTimeZone("GMT+08:00"));
			mMonth ++;
			tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+mYear+"-"+(mMonth+1)+"-"+"01");
			if(mMonth==12)
			{
				tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+(mYear+1)+"-"+1+"-"+"01");
			}
			else if(mMonth>12)
	    	{
	    		mMonth=1;
	    		mYear=mYear+1;
	      		tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+mYear+"-"+(mMonth+1)+"-"+"01");
	        	
	    	}
			  if(mMonth<10)
	    		{today = mYear + "-" + "0"+mMonth;}
	    	    else
	    	    {today=mYear+"-"+mMonth;}
			 cur=db.getAmount(today);
			mAdapter.changeCursor(cur);
			((SimpleCursorAdapter) mAdapter).notifyDataSetChanged();
		
		}

		private void leftView() {
			mMonth=mMonth-1;
			if(mMonth<1)
	    	{
	    		mMonth=12;
	    		mYear=mYear-1;
	    		tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+(mYear+1)+"-"+"1"+"-"+"01");
	    	}
			else if(mMonth>=12)
	    	{
	    		mMonth=1;
	    		mYear=mYear+1;
	    		tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+(mYear+1)+"-"+"1"+"-"+"01");
	    	}
	    	else
	     	tvTitle.setText(mYear+"-"+mMonth+"-"+"01"+"---"+mYear+"-"+(mMonth+1)+"-"+"01");
	        if(mMonth<10)
	    		{today = mYear + "-" + "0"+mMonth;}
	    	    else
	    	    {today=mYear+"-"+mMonth;}
	        cur=db.getAmount(today);
	    	mAdapter.changeCursor(cur);
			((SimpleCursorAdapter) mAdapter).notifyDataSetChanged();
		}
    }
}
