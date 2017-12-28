package com.example.team.carmeraview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videView = findViewById(R.id.videView);
        findViewById(R.id.start_play).setOnClickListener(this);
        findViewById(R.id.stop_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_play:
                videView.startPlay();
                break;
            case R.id.stop_play:
                videView.stopPlay();
                break;
        }
    }
}
