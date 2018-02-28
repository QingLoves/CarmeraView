package com.example.team.carmeraview;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.team.carmeraview.view.CameraRectView;
import com.example.team.carmeraview.view.ShootView;

public class CameraActivity extends AppCompatActivity  {
    private ImageView iv_photo;
    private CameraView camera_view;
    private CameraRectView rectView;
    private ShootView startCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_view = findViewById(R.id.camera_view);
        iv_photo = findViewById(R.id.iv_photo);
        rectView = findViewById(R.id.rectView);
        startCamera = findViewById(R.id.startCamera);
        rectView.setIAutoFocus(new CameraRectView.IAutoFocus() {
            @Override
            public void autoFocus(Rect rect) {
                camera_view.setAutoFocus(rect);
            }
        });
        startCamera.setStartCamera(new ShootView.IStartCamera() {
            @Override
            public void startCamera() {
                camera_view.capturePicture();
            }
        });
    }


}
