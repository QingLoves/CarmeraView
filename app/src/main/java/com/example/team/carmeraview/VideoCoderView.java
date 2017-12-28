package com.example.team.carmeraview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Team丶长相守 on 2017/12/14.
 */

public class VideoCoderView extends View {
    private Paint paint;
    private int SeenWidth;
    private int SeenHight;
    private Context context;
    public VideoCoderView(Context context) {
        this(context,null);
    }

    public VideoCoderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoCoderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.context = context;
        //paint.setColor(Color.YELLOW);
        SeenWidth = context.getResources().getDisplayMetrics().widthPixels;
        SeenHight = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        int cx = SeenWidth*2/4;
        int cy = SeenHight*2/6;
        int radius = SeenWidth*4/10;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#3B6BB4"));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);

        path.setFillType(Path.FillType.EVEN_ODD);
        path.addCircle(cx,cy,radius, Path.Direction.CW);
        canvas.drawPath(path,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#D4D0CC"));
        paint.setAlpha(70);
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRect(0,0,SeenWidth,SeenHight, Path.Direction.CW);
        canvas.drawPath(path,paint);
    }
}
