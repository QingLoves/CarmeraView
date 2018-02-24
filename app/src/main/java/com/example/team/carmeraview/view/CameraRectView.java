package com.example.team.carmeraview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.team.carmeraview.util.LogUtil;

/**
 * Created by Team丶长相守 on 2018/2/23.
 */

public class CameraRectView extends View {
    private Paint rectpaint;
    private Path rectPath;
    private Point centerPoint;
    private int screenWidth,screenHeight;
    private int radius;
    private static final int DEFAULT_LEFT = 50;
    private static final int DEFAULT_TOP = 300;
    private static final int DEAFULT_RIGHT = 50;
    private static final int DEAFULT_BOTTOM = 1000;
    private final int DURATION_TIME = 1000;
    private boolean isShow = false;
    private int lastValue;
    private ValueAnimator lineAnimator;
    public CameraRectView(Context context) {
        this(context,null);
    }

    public CameraRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        rectpaint = new Paint();
        rectPath = new Path();
        centerPoint = new Point(screenWidth/2,(DEFAULT_TOP+DEAFULT_BOTTOM)/2);
        radius = (int) (screenWidth * 0.1);
        rectpaint.setStyle(Paint.Style.STROKE);
        rectpaint.setColor(Color.WHITE);
        rectpaint.setStrokeWidth(3);
        rectpaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rect = new RectF(DEFAULT_LEFT,DEFAULT_TOP,screenWidth-DEAFULT_RIGHT,DEAFULT_BOTTOM);
        rectPath.addRect(rect, Path.Direction.CW);
        canvas.drawPath(rectPath,rectpaint);
        if (isShow){
            canvas.drawCircle(centerPoint.x,centerPoint.y,radius,rectpaint);
            //rectPath.addCircle(centerPoint.x,centerPoint.y,radius, Path.Direction.CW);
            //canvas.drawPath(rectPath,rectpaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                lastValue = 0;
                centerPoint = null;
                if (x>DEFAULT_LEFT && x<screenWidth-DEAFULT_RIGHT && y>DEFAULT_TOP && y<DEAFULT_BOTTOM){
                    LogUtil.i("",x+":"+y);
                    centerPoint = new Point(x, y);
                    showAnimView();
                    //invalidate();
                }


                break;
        }

        return true;
    }

    private void showAnimView() {
        isShow = true;
        if (lineAnimator == null) {
            LogUtil.e("","if");
            lineAnimator = ValueAnimator.ofInt(0, 20);
            lineAnimator.setDuration(DURATION_TIME);
            lineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LogUtil.e("","addUpdateListener");
                    int animationValue = (Integer) animation
                            .getAnimatedValue();
                    if(lastValue!=animationValue&&radius>=(int) ((screenWidth * 0.1)-20)){
                        radius = radius - animationValue;
                        lastValue = animationValue;
                    }
                    isShow = true;
                    invalidate();
                }
            });
            lineAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    LogUtil.e("","addListener");
                    isShow = false;
                    lastValue = 0;
                    rectpaint.setColor(Color.WHITE);
                    radius = (int) (screenWidth * 0.1);
                    invalidate();
                }
            });
        }else{
            LogUtil.e("","else");
            lineAnimator.end();
            lineAnimator.cancel();
            lineAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            lineAnimator.start();
        }
    }
}
