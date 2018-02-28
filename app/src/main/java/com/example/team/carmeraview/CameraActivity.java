package com.example.team.carmeraview;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.team.carmeraview.view.CameraRectView;

public class CameraActivity extends AppCompatActivity  {
    private ImageView iv_photo;
    private CameraView camera_view;
    private CameraRectView rectView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_view = findViewById(R.id.camera_view);
        iv_photo = findViewById(R.id.iv_photo);
        rectView = findViewById(R.id.rectView);
        rectView.setIAutoFocus(new CameraRectView.IAutoFocus() {
            @Override
            public void autoFocus(Rect rect) {
                camera_view.setAutoFocus(rect);
            }
        });
    }


}
