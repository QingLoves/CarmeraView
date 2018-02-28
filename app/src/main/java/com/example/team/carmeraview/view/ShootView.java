package com.example.team.carmeraview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.team.carmeraview.util.DisplayUtil;

/**
 * Created by Team丶长相守 on 2018/2/28.
 */

public class ShootView extends View {
    private Context context;
    private static  int radiue = 100;
    private Paint deaultPaint;
    private Point deaultPoint;
    private Paint circlePaint;
    private Point circlePoint;
    private static int circleStrokeWidth = 15;

    private static int DEAULTWIDTH;
    private static int DEAULTHEIGHT;
    private boolean showView;
    private IStartCamera startCamera;


    public ShootView(Context context) {
        this(context,null);
    }

    public ShootView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShootView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        DEAULTWIDTH = DisplayUtil.getScreenWidth(context);
        DEAULTHEIGHT = DisplayUtil.getScreenHeight(context);
        deaultPoint = new Point(DEAULTWIDTH/2,DEAULTHEIGHT-(5*radiue));
        deaultPaint = new Paint();
        deaultPaint.setColor(Color.WHITE);
        deaultPaint.setStyle(Paint.Style.FILL);
        deaultPaint.setAntiAlias(true);
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circlePaint.setColor(Color.parseColor("#939498"));
        circlePaint.setAntiAlias(true);

    }


    public void setStartCamera(IStartCamera startCamera){
        this.startCamera = startCamera;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(deaultPoint.x,deaultPoint.y,radiue,deaultPaint);
        if (showView){
            canvas.drawCircle(circlePoint.x,circlePoint.y,radiue,circlePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://0
                showView = true;
                int x =(int) event.getX();
                int y =(int) event.getY();
                if (x<deaultPoint.x-radiue || x>deaultPoint.x+radiue){
                    return super.onTouchEvent(event);
                }
                if (y<deaultPoint.y-radiue || y>deaultPoint.y+radiue){
                    return super.onTouchEvent(event);
                }
                if (circlePoint != null){
                    circlePoint = null;
                }


                circlePoint = new Point(deaultPoint.x,deaultPoint.y);
                invalidate();
                if (startCamera != null){
                    startCamera.startCamera();
                }
                Log.e("TAG", "ShootView onTouchEvent 按住");
                break;
            case MotionEvent.ACTION_UP://1
                Log.e("TAG", "ShootView onTouchEvent onTouch抬起");
                break;
            case MotionEvent.ACTION_MOVE://2
                Log.e("TAG", "ShootView onTouchEvent 移动");
                break;
        }
        return true;
    }


    public interface IStartCamera{
        void startCamera();
    }
}
