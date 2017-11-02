package com.example.nzhou.lab_3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.hardware.Camera;
import java.util.List;
import android.hardware.Camera.Size;

import java.util.List;

/**
 * Created by Ningjia on 2017/10/9.
 */

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback
{
    private List mSupportedPreviewSizes;
    private Size mPreviewSize;
    private Camera mCamera;

    public CameraPreviewView(Context context, AttributeSet attr)
    {
        super(context, attr);
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null)
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        try {
            if (mCamera != null) mCamera.setPreviewDisplay(holder);
        } catch (Exception exception) { }
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }



    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mCamera != null) mCamera.stopPreview();
    }


    public void setCamera(Camera camera)
    {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        if (sizes == null) return null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        Size optimalSize = null;
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


}
