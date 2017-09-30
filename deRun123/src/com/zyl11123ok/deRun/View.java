package com.zyl11123ok.deRun;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zyl11123ok.deRun.R;
import com.zyl.deRun.DataBase.DataBase;

public class View extends ActionBarActivity {
	
	private static final String TAG = "Delightful";
	private Button btnReset,btnCancel,btnSave,btnDate,btnNext,btnAC;
	public DataBase db;
	private TextView tvDate,tvCount,tvMile;
	private Button imageButton;
    private SharedPreferences mSettings;
	private Settings mPedometerSettings;
    private Utils mUtils;
    private DatePickerDialog dialog;
    private int mStepValue;
    private float mDistanceValue;
    private boolean mIsRunning;
    private boolean mQuitting = false; 
    private int mYear;
    private int mMonth;
    private int mDay;
    int acctitemid=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    Log.i(TAG, "[ACTIVITY] onCreate");
	
		  mStepValue = 0;
		  mUtils = Utils.getInstance();
		  setContentView(R.layout.view_plus);
		  btnReset=(Button)findViewById(R.id.BtnReset);
		  btnCancel=(Button)findViewById(R.id.btnCancel);
		  btnSave=(Button)findViewById(R.id.BtnSave);
		  btnDate=(Button)findViewById(R.id.BtnEdit);
		  btnNext=(Button)findViewById(R.id.BtnNext);
		  btnAC=(Button)findViewById(R.id.btnAC);
		  db=new DataBase(this);
		  db.CreatTable_amount();
		  tvDate=(TextView)findViewById(R.id.tvDate);
		  initTime();
		  if(mMonth<10 && mDay<10)
			{
				tvDate.setText(mYear+"-"+"0"+mMonth+"-"+"0"+mDay);
			}
				else if(mMonth<10 && mDay>=10)
				 {
					tvDate.setText(mYear+"-"+"0"+mMonth+"-"+mDay);
				 }
				else if(mMonth>=10 && mDay<10)
				{
					tvDate.setText(mYear+"-"+mMonth+"-"+"0"+mDay);
				}
				else{tvDate.setText(mYear+"-"+mMonth+"-"+mDay);}
		  ButtonListener buttonlistener =new ButtonListener();
			btnDate.setOnClickListener(buttonlistener);
			btnNext.setOnClickListener(buttonlistener);
			btnReset.setOnClickListener(buttonlistener);
			btnCancel.setOnClickListener(buttonlistener);
			btnSave.setOnClickListener(buttonlistener);
			btnAC.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(android.view.View v) {
					AlertDialog.Builder builder=new AlertDialog.Builder(View.this);
					android.view.View view=LayoutInflater.from(View.this).inflate(R.layout.dialogdemo,null);
					builder.setView(view);
					final EditText ed1=(EditText)view.findViewById(R.id.editText1);
					builder.setTitle("Please insert distance");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							String a=ed1.getText().toString().trim();
							tvMile.setText(""+a);
						}
				    
			        });
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					});
					builder.show();
				}
			});
		
	}
	@Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
    }
	@Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);
        
        mIsRunning = mPedometerSettings.isServiceRunning();
         boolean temp=mIsRunning;
       
         if (!mIsRunning && mPedometerSettings.isNewStart()) {
             startStepService();
             bindStepService();
         }
         else if (mIsRunning) {
             bindStepService();
         }
         mPedometerSettings.clearServiceRunning();
       
		tvMile=(TextView)findViewById(R.id.tvMile);
		
		tvCount=(TextView)findViewById(R.id.tvCount);
		imageButton=(Button)findViewById(R.id.imageButton1);
		imageButton.setText("暂停");
		imageButton.setOnClickListener(new OnClickListener() {
		boolean temp=mIsRunning;
			@Override
			public void onClick(android.view.View v) {
				if(temp){
					imageButton.setText("开始");
					stopStepService();
		             unbindStepService();
		             temp=false;
				}
				else{
					imageButton.setText("暂停");
					 startStepService();
		             bindStepService();
		             temp=true;
				}
			}
		});
}
	@Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        
        super.onPause();
        
    }
	@Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        
        super.onDestroy();
    }
    
    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onRestart();
    }
    private MyService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MyService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(View.this,
                    MyService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(View.this, 
                MyService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(View.this,
                  MyService.class));
        }
        mIsRunning = false;
    }
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else{
        	tvCount.setText("0");
        	 SharedPreferences state = getSharedPreferences("state", 0);
             SharedPreferences.Editor stateEditor = state.edit();
        
             if(updateDisplay){
            	 stateEditor.putInt("steps", 0);
            	 stateEditor.commit();
             }
        }
    }
    private void initTime(){
		Calendar c = Calendar. getInstance(TimeZone.getTimeZone("GMT+08:00"));
		mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
       
	}
    private static final int STEPS_MSG = 1;
    private static final int DISTANCE_MSG = 2;
    private MyService.ICallback mCallback = new MyService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
    };
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    tvCount.setText("" + mStepValue);
                    break;
                case DISTANCE_MSG:
                    mDistanceValue= ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        tvMile.setText("0.000");
                    }
                    else {
                        tvMile.setText( ""+mDistanceValue );
                    }
                    break;
            
                default:
                	super.handleMessage(msg);
            }
       }
    };

	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(android.view.View v) {
			if(v.equals(btnNext)){
				Intent i1=new Intent(View.this,View1.class);
					startActivity(i1);
			}
			if(v.equals(btnDate)){
				 dialog=new DatePickerDialog(View.this, new OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
							String text=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
							if((monthOfYear+1)<10 && dayOfMonth<10)
							{
								text=year+"-"+"0"+(monthOfYear+1)+"-"+"0"+dayOfMonth;
							}
								else if((monthOfYear+1)<10 && dayOfMonth>=10)
								 {
									text=year+"-"+"0"+(monthOfYear+1)+"-"+dayOfMonth;
								 }
								else if((monthOfYear+1)>=10 && dayOfMonth<10)
								{
									text=year+"-"+(monthOfYear+1)+"-"+"0"+dayOfMonth;
								}
								else{text=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
								}
							tvDate.setText(text);
						}
				 },mYear,mMonth-1,mDay);
				 dialog.show();
			}
			if(v.equals(btnCancel)){
				AlertDialog.Builder builder=new AlertDialog.Builder(View.this);
				builder.setTitle("请结束暂停状态后再退出");
				builder.setIcon(R.drawable.ic_launcher);
				builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try{ 
							resetValues(true);
							unbindStepService();
							stopStepService();
							mQuitting = true;
							finish();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
			    
		        });
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.show();
			}
			if(v.equals(btnReset)){
				resetValues(true);
			}
			if(v.equals(btnSave)){
				try{
				Log.v("TAG","u put save button");
				db.Amount_save(((TextView)tvDate).getText().toString(), ((TextView)tvMile).getText().toString()+"km");
				Toast.makeText(View.this, "保存成功", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	 @Override  
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 super.onActivityResult(requestCode, resultCode, data);  
		  if (resultCode == 100)  
          {  
              Uri uriData = data.getData();  
              tvDate.setText(uriData.toString());  
          }  
	 }
}
