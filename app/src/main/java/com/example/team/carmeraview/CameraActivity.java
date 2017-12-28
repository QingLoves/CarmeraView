package com.example.team.carmeraview;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CameraActivity extends AppCompatActivity implements CameraView.PhotoInfo {
    private ImageView iv_photo;
    private CameraView camera_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_view = findViewById(R.id.camera_view);
        camera_view.setMarginLeft(50);
        camera_view.setMarginRight(50);
        camera_view.setMarginTop(50);
        camera_view.setMarginBottom(700);
        camera_view.setZoom(1);
        camera_view.setPhotoInfo(this);
        iv_photo = findViewById(R.id.iv_photo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera_view.onPause();
    }

    @Override
    public void cameraError(String error) {

    }

    @Override
    public void cameraPath(String photoPath) {

    }

    @Override
    public void photoBitmap(Bitmap bitmap) {

        //bitmap = CameraUtil.getInstance().rotaingImageView(0,180,bitmap);
        iv_photo.setImageBitmap(bitmap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        camera_view.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
