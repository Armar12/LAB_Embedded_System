package ece.course.android.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class NetworkConnectionClient extends Thread {
	public static final int MSG_HV_DATA         = 0;
	public static final int MSG_CONNECTING      = 1;
	public static final int MSG_CONNECTED       = 2;
	public static final int MSG_CONNECTION_LOST = 3;
	
	private static final String TAG_DEBUG = "NetworkConnectionClient";

	private static final int BUFFER_SIZE  = 1024;
	private static final int TIMEOUT_TIME = 500;
	
	private final Handler mHandler;

	private final String mTargetHost;
	private final int mTargetPort;

	private Socket       mSocket;
	private InputStream  mInputStream;
	private OutputStream mOutputStream;
	
	private boolean isConnected = false;
	private boolean isRunning = false;
	
	public NetworkConnectionClient(String targetHost, int targetPort, Handler handler) {
		mTargetHost = targetHost;
		mTargetPort = targetPort;
		mHandler = handler;
	}
	
	@Override
	public void run() {
		isRunning = true;
		try {
			mHandler.obtainMessage(MSG_CONNECTING).sendToTarget();
					
			mSocket = new Socket();
			mSocket.bind(null);
			mSocket.connect((new InetSocketAddress(mTargetHost, mTargetPort)), TIMEOUT_TIME);
			mInputStream  = mSocket.getInputStream();
			mOutputStream = mSocket.getOutputStream();
			isConnected = true;

			mHandler.obtainMessage(MSG_CONNECTED).sendToTarget();
			try {
				int length = -1;
				byte[] data = new byte[BUFFER_SIZE];
				while (isRunning) {
					length = mInputStream.read(data);
					if (length != -1) {
						mHandler.obtainMessage(MSG_HV_DATA, length, 0, data).sendToTarget();
					}
				}
			} catch(IOException ioException) {
				Log.e(TAG_DEBUG, "run()", ioException);
			}
				
			if (mSocket != null) {
				if (mSocket.isConnected()) {
					mSocket.close();
				}
			}
		} catch (IOException ioException) {
			Log.e(TAG_DEBUG, "run()", ioException);
		}
		isConnected = false;
		mHandler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
	}
	
	public void sendData(byte[] data) {
		if (!isConnected) {
			return;
		}
		
		try {
			mOutputStream.write(data);
		} catch (IOException ioException) {
			Log.e(TAG_DEBUG, "sendData()", ioException);
		}
	}
	
	public void stopRun() {
		isRunning = false;
	}
}
