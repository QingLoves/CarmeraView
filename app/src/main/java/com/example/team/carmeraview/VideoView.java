package com.example.team.carmeraview;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.team.carmeraview.util.CameraUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Team丶长相守 on 2017/12/14.
 */

public class VideoView extends LinearLayout implements SurfaceHolder.Callback,MediaRecorder.OnErrorListener,Camera.PictureCallback,Camera.AutoFocusCallback {

    private SurfaceView surfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int screenWidth,screenHeight;
    private Context context;
    private int zoom;//焦距
    private final static int DEFOUT_ZOOM = 5;
    private static final int CameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;//默认后置CAMERA_FACING_FRONT
    private Camera mCamera = null;//相机类
    private MediaRecorder mMediaRecorder;
    private File VideoFile;
    public VideoView(Context context) {
        this(context,null);
    }

    public VideoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_video,this);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        surfaceView = findViewById(R.id.video_surface);
        mSurfaceHolder = surfaceView.getHolder();
        if (checkCameraHardware(context)){
            //初始化相机
            mCamera = initCamera(CameraID);
            mSurfaceHolder = surfaceView.getHolder();
            mSurfaceHolder.addCallback(this);//设置预览回调
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mMediaRecorder = new MediaRecorder();


        }
        else {
            //相机硬件不存在
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
            //info.cameraError("初始化相机失败");
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
      /*  Camera.Size previewSize = CameraUtil.getInstance().getPropVideoSize(parameters.getSupportedVideoSizes(), screenHeight);
        parameters.setPreviewSize(previewSize.width,previewSize.height);//设置预览尺寸
        Log.e("预览",previewSize.width+":"+previewSize.height);//1920*1080*/
       // Camera.Size pictureSize  = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(),previewSize.width);
       /* parameters.setPictureSize(pictureSize.width,pictureSize.height);//设置照片尺寸//1920*1080
        Log.e("尺寸",pictureSize.width+":"+pictureSize.height);*/
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
       /* FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(pictureSize.height, pictureSize.width);
        surfaceView.setLayoutParams(params);
        camera.cancelAutoFocus();
        camera.setParameters(parameters);
        camera.setDisplayOrientation(CameraUtil.getInstance().setCameraDisplayOrientation(context,CameraID));
        camera.startPreview();
        mCamera.unlock();
        initMediaRecorder(camera,pictureSize,holder);*/
    }

    //初始化视频参数
    private void initMediaRecorder(Camera camera, Camera.Size size,SurfaceHolder holder){
        if (camera == null){
            camera = initCamera(CameraID);
        }
        mMediaRecorder.reset();
        VideoFile =  getPhotoPath(context);
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(holder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
       // mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
       // mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
       // mMediaRecorder.setVideoSize(size.width,size.height);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
        mMediaRecorder.setVideoFrameRate(60);
        //mMediaRecorder.setVideoEncodingBitRate(16 * 100000);// 设置帧频率，然后就清晰了
        //设置视频输出的方向 很多设备在播放的时候需要设个参数 这算是一个文件属性
        mMediaRecorder.setOrientationHint(CameraUtil.getInstance().getRecorderRotation(CameraID));
        mMediaRecorder.setOutputFile(VideoFile.getPath());
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //设置焦距大小
    public void setZoom(int zoom){
        this.zoom = zoom;
    }
    //开始拍照
    public void startPlay(){
        //releaseCamera();
        if (mCamera != null){
           // mCamera.autoFocus(null,null,this);
        }
        mMediaRecorder.start();
    }

    //结束拍照
    public void stopPlay(){
        mMediaRecorder.stop();
        releaseCamera();
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

    //开始预览
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCameraDistinguis(mCamera,mSurfaceHolder);

    }

    //重新预览
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*releaseCamera();
        mCamera = initCamera(CameraID);
        setCameraDistinguis(mCamera,mSurfaceHolder);*/
    }

    //结束预览
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
    //录制视频出错
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }
    //设置保存图片路径
    public File getPhotoPath(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File path = new File(file,getTime()+".mp4");
        return path;
    }

    //获取系统时间
    private String getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        String  date  =  sDateFormat.format(new java.util.Date());
        return date;
    }


    //对焦成功
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success){
            camera.takePicture(null,null,this);
        }

    }

    //获取图片
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }




}



