package com.example.team.carmeraview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.FaceDetector;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Team丶长相守 on 2017/12/6.
 */

public class HenCoderView extends View {

    private Paint paint;
    private int SeenWidth;
    private int SeenHight;
    private Context context;
    private int left,right,top,bottom;
    private final  static int DEFOUT_LEFT = 50;
    private  final  static int DEFOUT_RIGHT = 50;
    private final static int DEFOUT_TOP = 0;
    private final static int DEFOUT_BOTTOM = 640;

    public HenCoderView(Context context) {
        this(context,null);
    }

    public HenCoderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HenCoderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        paint.setColor(Color.BLACK);
        paint.setTextSize(45);
        canvas.drawText("录制本人5秒视频，头部居中，衣着得体",SeenWidth*2/8,100,paint);
        canvas.drawText("随机码在90秒后失效，请及时提交",SeenWidth*2/8,260,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#3B6BB4"));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);

        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRect(left!=0?left:DEFOUT_LEFT,top!=0?top:DEFOUT_TOP,right!=0?SeenWidth-right:SeenWidth-DEFOUT_RIGHT,bottom!=0?bottom:DEFOUT_BOTTOM, Path.Direction.CCW);
        //path.addCircle(cx,cy,(SeenWidth*4/10), Path.Direction.CW);
        canvas.drawPath(path,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#D4D0CC"));
        paint.setAlpha(70);
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRect(0,0,SeenWidth,SeenHight, Path.Direction.CW);
        canvas.drawPath(path,paint);
    }

    //左边距
    public void setMarginLeft(int left){
        this.left = left;
    }
    //右边距
    public void setMarginRight(int right){
        this.right = right;
    }
    //上边距--距离顶部高度默认为0
    public void setMarginTop(int top){
        this.top = top;

    }
    //高度，图片的高度
    public void setMarginBottom(int bottom){
        this.bottom =bottom;

    }

}
