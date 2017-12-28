package com.example.team.carmeraview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Team丶长相守 on 2017/12/6.
 */

public class CameraView extends LinearLayout implements SurfaceHolder.Callback ,Camera.PictureCallback,Camera.AutoFocusCallback,View.OnClickListener{

    private SurfaceView surfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int screenWidth,screenHeight;
    private PhotoInfo info;
    //CAMERA_FACING_FRONT
    private static final int CameraID = Camera.CameraInfo.CAMERA_FACING_BACK;//默认后置CAMERA_FACING_BACK
    private Camera mCamera = null;//相机类
    private File photoFile;
    private Context context;
    private Button start_camera;
    private HenCoderView coderView;
    private int left,right,top,bottom;
    private int zoom;//焦距
    private final static int DEFOUT_ZOOM = 5;
    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        LayoutInflater.from(context).inflate(R.layout.layout_camera,this);
        surfaceView = findViewById(R.id.surface);
        coderView = findViewById(R.id.coderView);
        start_camera = findViewById(R.id.start_camera);
        start_camera.setOnClickListener(this);
        init();
    }

    private void init(){
        if(checkCameraHardware(context)){
            mCamera = initCamera(CameraID);
        }
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);//设置预览回调
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    //设置回调函数
    public void setPhotoInfo(PhotoInfo info){
        this.info = info;
    }

    //预览开始
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null){
            mCamera = initCamera(CameraID);
        }
        setCameraDistinguis(mCamera,mSurfaceHolder);
    }
    //左边距
    public void setMarginLeft(int left){
        this.left = left;
        coderView.setMarginLeft(left);
    }
    //右边距
    public void setMarginRight(int right){
        this.right = right;
        coderView.setMarginRight(right);

    }
    //上边距--距离顶部高度默认为0
    public void setMarginTop(int top){
        this.top = top;
        coderView.setMarginTop(top);
    }

    //顶部到底部的距离。应该就是高了
    public void setMarginBottom(int bottom){
        this.bottom = bottom;
        coderView.setMarginBottom(bottom);

    }

    //设置焦距大小
    public void setZoom(int zoom){
        this.zoom = zoom;
    }
    //重新预览
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        releaseCamera();
        if (mCamera == null){
            mCamera = initCamera(CameraID);
        }
        setCameraDistinguis(mCamera,mSurfaceHolder);
    }
    //预览结束
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    //设置保存图片路径
    public File getPhotoPath(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File path = new File(file,getTime()+".jpg");
        return path;
    }



    //检测相机硬件是否存在
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    private Camera initCamera(int CarmeId){
        if (mCamera != null) {
            releaseCamera();
        }

        try {
            mCamera = Camera.open(CarmeId);
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            info.cameraError("初始化相机失败");
        }
        return mCamera;

    }
    //设置相机预览分辨率及对焦模式
    private void setCameraDistinguis(Camera camera, SurfaceHolder holder){
        if (camera == null){
            return;
        }
        if (holder == null){
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = camera.getParameters();//获取相机设置对象
        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropPreviewSize(parameters.getSupportedPreviewSizes(), screenHeight);
        parameters.setPreviewSize(previewSize.width,previewSize.height);//设置预览尺寸
        Log.e("预览",previewSize.width+":"+previewSize.height);//1920*1080
        Camera.Size pictureSize  = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(),previewSize.width);
        parameters.setPictureSize(pictureSize.width,pictureSize.height);//设置照片尺寸//1920*1080
        Log.e("尺寸",pictureSize.width+":"+pictureSize.height);
        parameters.set("orientation", "portrait");
        int maxZoom = parameters.getMaxZoom();
        Log.e("maxZoom",maxZoom+"");
        if (zoom!=0&& zoom<maxZoom){
            parameters.setZoom(zoom);
        }
        else {
            parameters.setZoom(DEFOUT_ZOOM);
        }

        //自动对焦
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(pictureSize.height, pictureSize.width);
        surfaceView.setLayoutParams(params);
        camera.cancelAutoFocus();
        camera.setParameters(parameters);
        camera.setDisplayOrientation(CameraUtil.getInstance().setCameraDisplayOrientation(context,CameraID));
        camera.startPreview();
    }


    //得到照片byte[]流
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        photoFile = getPhotoPath(context);
        if (photoFile == null){
            Toast.makeText(context,photoFile.getPath()+"",Toast.LENGTH_LONG);
            return;
        }

        if (!photoFile.exists()){
            try {
                photoFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(photoFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                bitmap =  CameraUtil.getInstance().rotaingImageView(0,90,bitmap);
                info.cameraPath(photoFile.getPath());
                Log.e("==",bitmap.getWidth()+"");
                Log.e("===",bitmap.getHeight()+"");
                bitmap = Bitmap.createBitmap(bitmap,left,top,screenWidth-right-left,bottom-top);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                info.photoBitmap(bitmap);
                //
                if (mCamera == null){
                    mCamera = initCamera(CameraID);
                }
                if (mSurfaceHolder == null){
                    mSurfaceHolder = surfaceView.getHolder();
                }
                setCameraDistinguis(mCamera,mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {
            Toast.makeText(context,"图片存在",Toast.LENGTH_LONG).show();
        }


    }

    //对焦成功之后
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success){
            Log.e("TAG","对焦成功");
            camera.takePicture(null,null,this);
        }
        else {
            camera.takePicture(null,null,this);
            Log.e("TAG","对焦失败");
        }
        // camera.takePicture(null,null,this);
    }
    public void startCamera(){
        mCamera.autoFocus(this);

    }


    public void onResume(){
        if (mCamera == null){
            mCamera = initCamera(CameraID);

        }
        setCameraDistinguis(mCamera,mSurfaceHolder);


    }


    public void onStop(){
        releaseCamera();

    }

    public void onPause(){
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    //释放相机资源
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    private String getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String  date  =  sDateFormat.format(new java.util.Date());
        return date;
    }

    @Override
    public void onClick(View v) {
        startCamera();
    }

    public interface PhotoInfo{
        public void cameraError (String error);
        public void cameraPath(String photoPath);
        public void photoBitmap(Bitmap bitmap);
    }
}
