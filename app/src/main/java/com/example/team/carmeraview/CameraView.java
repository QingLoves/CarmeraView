package com.example.team.carmeraview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.team.carmeraview.util.CameraUtil;
import com.example.team.carmeraview.util.DisplayUtil;
import com.example.team.carmeraview.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Team丶长相守 on 2017/12/6.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraView.class.getName();
    private CameraUtil cameraUtil;
    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private int DEFAULT_PHOTO_WIDTH ;
    private int DEFAULT_PHOTO_HEIGHT ;
    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        DEFAULT_PHOTO_WIDTH = DisplayUtil.getScreenHeight(context);;
        DEFAULT_PHOTO_HEIGHT = DisplayUtil.getScreenWidth(context);
        LogUtil.e(TAG,DEFAULT_PHOTO_WIDTH+":"+DEFAULT_PHOTO_HEIGHT);
        cameraUtil = CameraUtil.getInstance();
        initCamera();
    }


    private void initCamera(){
        if ( cameraUtil.checkCameraHardware(context)){
            mCamera = cameraUtil.openCamera();
        }

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }


    //开始预览
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            setCameraDistinguis();
            mCamera.startPreview();
        } catch (IOException e) {
            LogUtil.e(TAG,"开始预览失败");
            e.printStackTrace();
        }

    }

    //重新预览
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(TAG,"重新预览失败");
        }
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //停止预览
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraUtil.releaseCamera(mCamera);
    }


    /**
     * 设置相机预览分辨率及对焦模式
     *
     * */
    private void setCameraDistinguis(){

        LogUtil.i(TAG, "setCameraParams  width=" + DEFAULT_PHOTO_WIDTH + "  height=" + DEFAULT_PHOTO_HEIGHT);
        Camera.Parameters parameters = mCamera.getParameters();
        /*************************** 获取图片支持的PictureSize列表********************/
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            LogUtil.i(TAG, "图片支持的分辨率：" + " size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size picSize =cameraUtil.getBestSupportedSize(pictureSizeList, ((float) DEFAULT_PHOTO_HEIGHT) / DEFAULT_PHOTO_WIDTH);//从列表中选取合适的分辨率
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }

        LogUtil.e(TAG, "图片分辨率：" + "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        parameters.setPictureSize(picSize.width, picSize.height);


        /*************************** 获取预览分辨率支持的PreviewSize列表********************/
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            LogUtil.i(TAG, "预览的分辨率：" + " size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize =cameraUtil.getBestSupportedSize(previewSizeList, ((float) DEFAULT_PHOTO_HEIGHT) / DEFAULT_PHOTO_WIDTH);
        if (null != preSize) {
            LogUtil.e(TAG, "预览分辨率：" + "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//手动区域自动对焦
        //图片质量
        parameters.setJpegQuality(100); // 设置照片质量
        parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP); // 预览格式
        parameters.setPictureFormat(PixelFormat.JPEG); // 相片格式为JPEG，默认为NV21

        // 关闪光灯
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        // 横竖屏镜头自动调整
       cameraUtil.setCameraDisplayOrientation(context,0);
        mCamera.setDisplayOrientation(cameraUtil.setCameraDisplayOrientation(context,0));

    }

    /**
     * 拍照回调
     * */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    /**
     * 相机拍摄
     * */
    public void capturePicture(){
        mCamera.takePicture(null,null,mPictureCallback);
    }




}
