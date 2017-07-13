package com.example.ato.kinectcount01_android;

import android.graphics.Bitmap;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Ato on 2017-06-28.
 */

public class StreamView extends View {
    private Paint paintSkeleton;
    private Paint paintThreshod;
    private Path path;
    private int[] x;
    private int[] y;
    private int threshold;
    private int height;
    private int width;
    public Bitmap depthBitmap = Bitmap.createBitmap(Var._DepthWidth, Var._DepthHeight, Bitmap.Config.ARGB_8888);

    public StreamView(Context context) {
        super(context);

        paintSkeleton = new Paint();
        paintSkeleton.setColor(Color.RED);
        paintSkeleton.setStrokeWidth(10f);
        paintSkeleton.setStyle(Paint.Style.STROKE);
        paintThreshod = new Paint();
        paintThreshod.setColor(Color.CYAN);
        paintThreshod.setStrokeWidth(7f);
        path = new Path();

        x = new int[20];
        y = new int[20];
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(depthBitmap,0,0, null);
        /*
        path.rewind();
        //Draw head and torso
        path.moveTo(x[Var._Head],y[Var._Head]);
        path.lineTo(x[Var._ShoulderCenter],y[Var._ShoulderCenter]);
        path.lineTo(x[Var._ShoulderLeft],y[Var._ShoulderLeft]);
        path.lineTo(x[Var._Spine],y[Var._Spine]);
        path.lineTo(x[Var._ShoulderRight],y[Var._ShoulderRight]);
        path.lineTo(x[Var._ShoulderCenter],y[Var._ShoulderCenter]);
        path.lineTo(x[Var._HipCenter],y[Var._HipCenter]);

        path.moveTo(x[Var._HipLeft],y[Var._HipLeft]);
        path.lineTo(x[Var._HipRight],y[Var._HipRight]);

        //Draw left leg
        path.moveTo(x[Var._HipCenter],y[Var._HipCenter]);
        path.lineTo(x[Var._HipLeft],y[Var._HipLeft]);
        path.lineTo(x[Var._KneeLeft],y[Var._KneeLeft]);
        path.lineTo(x[Var._AnkleLeft],y[Var._AnkleLeft]);
        path.lineTo(x[Var._FootLeft],y[Var._FootLeft]);

        //Draw right leg
        path.moveTo(x[Var._HipCenter],y[Var._HipCenter]);
        path.lineTo(x[Var._HipRight],y[Var._HipRight]);
        path.lineTo(x[Var._KneeRight],y[Var._KneeRight]);
        path.lineTo(x[Var._AnkleRight],y[Var._AnkleRight]);
        path.lineTo(x[Var._FootRight],y[Var._FootRight]);

        //Draw left arm
        path.moveTo(x[Var._ShoulderLeft],y[Var._ShoulderLeft]);
        path.lineTo(x[Var._ElbowLeft],y[Var._ElbowLeft]);
        path.lineTo(x[Var._WristLeft],y[Var._WristLeft]);
        path.lineTo(x[Var._HandLeft],y[Var._HandLeft]);

        //Draw right arm
        path.moveTo(x[Var._ShoulderRight],y[Var._ShoulderRight]);
        path.lineTo(x[Var._ElbowRight],y[Var._ElbowRight]);
        path.lineTo(x[Var._WristRight],y[Var._WristRight]);
        path.lineTo(x[Var._HandRight],y[Var._HandRight]);
        canvas.drawPath(path,paintSkeleton);

        // 카운트 쓰레시홀드
        canvas.drawLine(0,threshold,width,threshold,paintThreshod);
        */
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        // 뷰의 현재 사이즈
        height = yNew;
        width = xNew;

        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    // 데이터를 받아 x,y에 넣음
    public void setPosition(String[] positions) {
        for(int i=0;i<20;i++) {
            x[i] = Integer.parseInt(positions[2*i]);
            y[i] = Integer.parseInt(positions[2*i+1]);
            // x 와 y 위치를 뷰 크기에 맞춤
            x[i] = x[i] * width / Var._DepthWidth;
            y[i] = y[i] * height / Var._DepthHeight;
        }
        threshold = Integer.parseInt(positions[40]) * height / Var._DepthHeight;
    }
}

