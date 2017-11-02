package course.elec4010b.lab_4_4;

import ece.course.android.module.NetworkConnectionClient;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_CONNECT = 0;
	
	private static final int APPS_PORT       = 1234;

	private static final byte MSG_PICTURE_CAPTURE       = (byte)'P';
	private static final byte MSG_VIDEO_RECORDING_START = (byte)'V';
	private static final byte MSG_VIDEO_RECORDING_STOP  = (byte)'S';
	
	private PowerManager mPowerManager;
	private WakeLock     mWakeLock;
	private WifiManager  mWifiManager;
	
	private TextView tvConnectionState;
	private Button   btnConnect;
	private Button   btnPictureCapture;
	private Button   btnVideoRecordingStart;
	private Button   btnVideoRecordingStop;
	
	private NetworkConnectionClient mClient;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
        
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        
        if (mWifiManager == null) {
        	Toast.makeText(this, "There is no wifi Module in the phone, leave...", Toast.LENGTH_LONG).show();
        	finish();
        }
        
        tvConnectionState      = (TextView) findViewById(R.id.tvConnectionState);
        btnConnect             = (Button) findViewById(R.id.btnConnect);
        btnPictureCapture      = (Button) findViewById(R.id.btnPictureCapture);
        btnVideoRecordingStart = (Button) findViewById(R.id.btnVideoRecordingStart);
        btnVideoRecordingStop  = (Button) findViewById(R.id.btnVideoRecordingStop);
        
        btnConnect.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(MainActivity.this, NetworkConnectionActivity.class);
        		startActivityForResult(intent, REQUEST_CONNECT);
        	}
        });
        
        btnPictureCapture.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		if (mClient != null) {
        			byte[] data = { MSG_PICTURE_CAPTURE };
        			mClient.sendData(data);
        		}
        	}
        });
        
        btnVideoRecordingStart.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		if (mClient != null) {
        			byte[] data =  { MSG_VIDEO_RECORDING_START };
        			mClient.sendData(data);
        		}
        	}
        });
        
        btnVideoRecordingStop.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		if (mClient != null) {
        			byte[] data =  { MSG_VIDEO_RECORDING_STOP };
        			mClient.sendData(data);
        		}
        	}
        });
        
        btnConnect.setEnabled(true);
        btnPictureCapture.setEnabled(false);
        btnVideoRecordingStart.setEnabled(false);
        btnVideoRecordingStop.setEnabled(false);
    }
    
    @Override
    public synchronized void onResume() {
    	super.onResume();
        mWakeLock.acquire();
    }
    
    @Override
    public synchronized void onPause() {
        mWakeLock.release();
        if (mClient != null) {
        	mClient.stopRun();
        }
    	super.onPause();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case REQUEST_CONNECT :
    		if (resultCode == RESULT_OK) {
    			String host = data.getStringExtra(NetworkConnectionActivity.TAG_HOST);
    			mClient = new NetworkConnectionClient(host, APPS_PORT, new Handler() {
    				@Override
    				public void handleMessage(Message msg) {
    					switch(msg.what) {
    					case NetworkConnectionClient.MSG_HV_DATA :
    						byte[] data = (byte[]) msg.obj;
    						int length  = msg.arg1;
    						
    						for (int i = 0; i < length; i++) {
    							switch(data[i]) {
    							case MSG_PICTURE_CAPTURE :
    								Toast.makeText(MainActivity.this, R.string.msgPictureCapture, Toast.LENGTH_SHORT).show();
    								break;
    							case MSG_VIDEO_RECORDING_START :
    								Toast.makeText(MainActivity.this, R.string.msgVideoRecordingStart, Toast.LENGTH_SHORT).show();
    	    		    			
    								btnConnect.setEnabled(false);
    	    		    	        btnPictureCapture.setEnabled(false);
    	    		    	        btnVideoRecordingStart.setEnabled(false);
    	    		    	        btnVideoRecordingStop.setEnabled(true);
    								
    	    		    	        break;
    							case MSG_VIDEO_RECORDING_STOP :
    								Toast.makeText(MainActivity.this, R.string.msgVideoRecordingStop, Toast.LENGTH_SHORT).show();
    	    		    			
    								btnConnect.setEnabled(false);
    	    		    	        btnPictureCapture.setEnabled(true);
    	    		    	        btnVideoRecordingStart.setEnabled(true);
    	    		    	        btnVideoRecordingStop.setEnabled(false);
    	    		    	        
    								break;
    							}
    						}
    						break;
    					case NetworkConnectionClient.MSG_CONNECTING :
    						tvConnectionState.setText(R.string.stateConnecting);
    		    			btnConnect.setEnabled(false);
    		    	        btnPictureCapture.setEnabled(false);
    		    	        btnVideoRecordingStart.setEnabled(false);
    		    	        btnVideoRecordingStop.setEnabled(false);
    						break;
    					case NetworkConnectionClient.MSG_CONNECTED :
    						tvConnectionState.setText(R.string.stateConnected);
    		    			btnConnect.setEnabled(false);
    		    	        btnPictureCapture.setEnabled(true);
    		    	        btnVideoRecordingStart.setEnabled(true);
    		    	        btnVideoRecordingStop.setEnabled(false);
    						break;
    					case NetworkConnectionClient.MSG_CONNECTION_LOST :
    						tvConnectionState.setText(R.string.stateConnectLost);
    		    			
    						btnConnect.setEnabled(true);
    		    	        btnPictureCapture.setEnabled(false);
    		    	        btnVideoRecordingStart.setEnabled(false);
    		    	        btnVideoRecordingStop.setEnabled(false);
    		    	        
    		    	        mClient = null;
    						break;
    					}
    				}
    			});
    			btnConnect.setEnabled(false);
    	        btnPictureCapture.setEnabled(false);
    	        btnVideoRecordingStart.setEnabled(false);
    	        btnVideoRecordingStop.setEnabled(false);
    			mClient.start();
    		}
    	}    	
    }
}