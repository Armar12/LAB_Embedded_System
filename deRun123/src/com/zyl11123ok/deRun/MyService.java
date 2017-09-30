package com.zyl11123ok.deRun;

import com.zyl11123ok.deRun.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

	private static final String TAG = "zyl.derun.MyService";
    private SharedPreferences mSettings;
    private Settings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepSensor mStepSensor;
    private StepNotifier mStepNotifier;
    private DistanceNotifier mDistanceNotifier;
    private NotificationManager mNM;
    private int mSteps;
    private float mDistance;
    public class StepBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        
       mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);
        mState = getSharedPreferences("state", 0);

        mUtils = Utils.getInstance();
        mUtils.setService(this);
        
        mStepSensor=new StepSensor();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();
        
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        
        mStepNotifier=new StepNotifier(mPedometerSettings, mUtils);
        mStepNotifier.setSteps(mSteps = mState.getInt("steps", 0));
        mStepNotifier.addListener(mStepListener);
        mStepSensor.addStepListener(mStepNotifier);
        
        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings, mUtils);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mStepSensor.addStepListener(mDistanceNotifier);
        
        Toast.makeText(this, "开始计步", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");
    
        unregisterReceiver(mReceiver);
        unregisterDetector();
        
        mStateEditor = mState.edit();
        mStateEditor.putInt("steps", mSteps);
        mStateEditor.putFloat("distance", mDistance);
     
        mStateEditor.commit();
        mNM.cancel(R.string.app_name);
        super.onDestroy();
        
        // Stop detecting
        mSensorManager.unregisterListener(mStepSensor);

        // Tell the user we stopped.
        Toast.makeText(this,"停止计步", Toast.LENGTH_SHORT).show();
    }
    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepSensor,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepSensor);
    }


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		  Log.i(TAG, "[SERVICE] onBind");
		  return mBinder;
	}
	public IBinder onReBind(Intent intent){
		Log.i(TAG, "[SERVICE] onReBind");
		return mBinder;
		
	}
	private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);
        public void distanceChanged(float value);
        
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepSensor != null) { 
            mStepSensor.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }
        
        if (mStepNotifier    != null) mStepNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
    }
    public void resetValues() {
        mStepNotifier.setSteps(0);
        mDistanceNotifier.setDistance(0);
  
    }
    private StepNotifier.Listener mStepListener = new StepNotifier.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
            }
        }
    };
    private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_launcher, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, View.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text,
               "计步器正在计步...", contentIntent);

        mNM.notify(R.string.app_name, notification);
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                MyService.this.unregisterDetector();
                MyService.this.registerDetector();

           }
        }
    };

}
