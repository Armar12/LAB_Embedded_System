package ece.course.lab_3;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private CameraPreviewView mCameraPreviewView;
    private Camera mCamera;
    private int mCameraId;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No Camara On The Phone Leaving...",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        int numberOfCameras = Camera.getNumberOfCameras();
        boolean hvBackCamera = false;
        android.hardware.Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = i;
                hvBackCamera = true;
                break;
            }
        }
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        if (!hvBackCamera) {
            Toast.makeText(this, "The Apps only support the back Camara, Leaving...",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        mCameraPreviewView = (CameraPreviewView) findViewById(R.id.mCameraPreviewView);
    }

    protected synchronized void onResume()
    {
        super.onResume();
        mWakeLock.acquire();
        mCamera = Camera.open(mCameraId);
        mCameraPreviewView.setCamera(mCamera);
    }


    protected synchronized void onPause()
    {
        mWakeLock.release();
        if (mCamera != null) {
            mCameraPreviewView.setCamera(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuTakePicture :
                return true;
            case R.id.menuRecordVideo :
                return true;
            case R.id.menuStopRecord :
                return true;
        }
        return false;
    }


}
