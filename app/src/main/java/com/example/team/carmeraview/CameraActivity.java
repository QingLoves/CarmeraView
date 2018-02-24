package com.example.team.carmeraview;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CameraActivity extends AppCompatActivity  {
    private ImageView iv_photo;
    private CameraView camera_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_view = findViewById(R.id.camera_view);
        iv_photo = findViewById(R.id.iv_photo);
    }


}
