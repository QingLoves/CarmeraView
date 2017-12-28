package com.example.team.carmeraview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.camera).setOnClickListener(this);
        findViewById(R.id.video).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.camera:
                intent.setClass(this,CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.video:
                intent.setClass(this,VideoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
