package ece.course.android.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class NetworkConnectionServer extends Thread {
	public static final int MSG_HV_DATA         = 0;
	public static final int MSG_CONNECTED       = 2;
	public static final int MSG_CONNECTION_LOST = 3;
	
	private static final String TAG_DEBUG = "NetworkConnectionServer";

	private static final int BUFFER_SIZE  = 1024;

	private final Handler mHandler;
	
	private final int mTargetPort;
	
	private ServerSocket mServerSocket;
	private Socket       mClientSocket;
	private InputStream  mInputStream;
	private OutputStream mOutputStream;
	
	private boolean isConnected = false;
	
	public NetworkConnectionServer(int targetPort, Handler handler) {
		mTargetPort = targetPort;
		mHandler = handler;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				isConnected = false;

				mServerSocket = new ServerSocket(mTargetPort);
				mClientSocket = mServerSocket.accept();
				mInputStream  = mClientSocket.getInputStream();
				mOutputStream = mClientSocket.getOutputStream();
				
				isConnected = true;
				mHandler.obtainMessage(MSG_CONNECTED).sendToTarget();
				
				try {	
					int length = -1;
					byte[] data = new byte[BUFFER_SIZE];
					while (true) {
						length = mInputStream.read(data);
						if (length != -1) {
							mHandler.obtainMessage(MSG_HV_DATA, length, 0, data).sendToTarget();
						}
					}
				} catch(IOException ioException) {
					Log.e(TAG_DEBUG, "run()", ioException);
				}
				
				mHandler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();

				if (mClientSocket != null) {
					try {
						if (mClientSocket.isConnected()) {
							mClientSocket.close();
						}
					} catch(IOException ioException) {
						Log.e(TAG_DEBUG, "run()", ioException);
					}
				}
				
				if (mServerSocket != null) {
					mServerSocket.close();
					mServerSocket = null;
				}
			} catch(IOException ioException) {
				Log.e(TAG_DEBUG, "run()", ioException);
			}
		}
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
}
