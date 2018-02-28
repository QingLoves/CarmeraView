package com.example.team.carmeraview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.team.carmeraview.util.CameraUtil;
import com.example.team.carmeraview.util.DisplayUtil;
import com.example.team.carmeraview.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
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
    /**
     * 当前摄像头id
     */
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
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
                setCameraParams(DEFAULT_PHOTO_WIDTH,DEFAULT_PHOTO_HEIGHT);
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
     * 设置分辨率等参数
     *
     * @param width  宽
     * @param height 高
     */
    private void setCameraParams(int width, int height) {
        LogUtil.i(TAG, "setCameraParams  width=" + width + "  height=" + height);

        Camera.Parameters parameters = mCamera.getParameters();


        /*************************** 获取摄像头支持的PictureSize列表********************/
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        sort(pictureSizeList);//排序
        for (Camera.Size size : pictureSizeList) {
            LogUtil.i(TAG, "摄像头支持的分辨率：" + " size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size picSize = getBestSupportedSize(pictureSizeList, ((float) height / width));//从列表中选取合适的分辨率
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }

        LogUtil.e(TAG, "我们选择的摄像头分辨率：" + "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        parameters.setPictureSize(picSize.width, picSize.height);


        /*************************** 获取摄像头支持的PreviewSize列表********************/
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        sort(previewSizeList);
        for (Camera.Size size : previewSizeList) {
            LogUtil.i(TAG, "摄像支持可预览的分辨率：" + " size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getBestSupportedSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            LogUtil.e(TAG, "我们选择的预览分辨率：" + "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        /*************************** 对焦模式的选择 ********************/
        if(0 == Camera.CameraInfo.CAMERA_FACING_BACK){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//手动区域自动对焦
        }
        //图片质量
        parameters.setJpegQuality(100); // 设置照片质量
        parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP); // 预览格式
        parameters.setPictureFormat(PixelFormat.JPEG); // 相片格式为JPEG，默认为NV21

        // 关闪光灯
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        // 横竖屏镜头自动调整
        if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }

        //相机异常监听
        mCamera.setErrorCallback(new Camera.ErrorCallback() {

            @Override
            public void onError(int error, Camera camera) {
                String error_str;
                switch (error) {
                    case Camera.CAMERA_ERROR_SERVER_DIED: // 摄像头已损坏
                        error_str = "摄像头已损坏";
                        break;

                    case Camera.CAMERA_ERROR_UNKNOWN:
                        error_str = "摄像头异常，请检查摄像头权限是否应许";
                        break;

                    default:
                        error_str = "摄像头异常，请检查摄像头权限是否应许";
                        break;
                }
                //ToastUtil.getInstance().toast(error_str);
                Log.i(TAG, error_str);
            }
        });
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
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


    /**
     * 排序：从大到小
     *
     * @param list
     */
    private void sort(List<Camera.Size> list) {
        Collections.sort(list, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                return o2.width - o1.width;
            }
        });
    }

    /**
     * 如包含默认尺寸，则选默认尺寸，如没有，则选最大的尺寸
     * 规则：在相同比例下，1.优先寻找长宽分辨率相同的->2.找长宽有一个相同的分辨率->3.找最大的分辨率
     *
     * @param sizes 尺寸集合
     * @return 返回合适的尺寸
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, float screenRatio) {
        Camera.Size largestSize = null;
        int largestArea = 0;
        for (Camera.Size size : sizes) {
            if ((float) size.height / (float) size.width == screenRatio) {
                if (size.width == DEFAULT_PHOTO_WIDTH && size.height == DEFAULT_PHOTO_HEIGHT) {
                    // 包含特定的尺寸，直接取该尺寸
                    largestSize = size;
                    break;
                } else if (size.height == DEFAULT_PHOTO_HEIGHT || size.width == DEFAULT_PHOTO_WIDTH) {
                    largestSize = size;
                    break;
                }
                int area = size.height + size.width;
                if (area > largestArea) {//找出最大的合适尺寸
                    largestArea = area;
                    largestSize = size;
                }
            } else if (size.height == DEFAULT_PHOTO_HEIGHT || size.width == DEFAULT_PHOTO_WIDTH) {
                largestSize = size;
                break;
            }
        }
        if (largestSize == null) {
            largestSize = sizes.get(sizes.size() - 1);
        }
        return largestSize;
    }

    public void setAutoFocus(Rect rect) {
        camerFocus(caculateFocusPoint(rect.left, rect.top));
    }

    private void camerFocus(Rect rect) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if(0 == Camera.CameraInfo.CAMERA_FACING_BACK){
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//手动区域自动对焦
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            mCamera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            mCamera.setParameters(parameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }

    private Rect caculateFocusPoint(int x, int y) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / getWidth() - 1000;
        int top = rect.top * 2000 / getHeight() - 1000;
        int right = rect.right * 2000 / getWidth() - 1000;
        int bottom = rect.bottom * 2000 / getHeight() - 1000;
        // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        return new Rect(left, top, right, bottom);
    }

}
