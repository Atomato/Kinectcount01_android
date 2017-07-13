package com.example.ato.kinectcount01_android;

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

        path.rewind();
        //Draw head and torso
        path.moveTo(x[Var.Item._Head],y[Var.Item._Head]);
        path.lineTo(x[Var.Item._ShoulderCenter],y[Var.Item._ShoulderCenter]);
        path.lineTo(x[Var.Item._ShoulderLeft],y[Var.Item._ShoulderLeft]);
        path.lineTo(x[Var.Item._Spine],y[Var.Item._Spine]);
        path.lineTo(x[Var.Item._ShoulderRight],y[Var.Item._ShoulderRight]);
        path.lineTo(x[Var.Item._ShoulderCenter],y[Var.Item._ShoulderCenter]);
        path.lineTo(x[Var.Item._HipCenter],y[Var.Item._HipCenter]);

        path.moveTo(x[Var.Item._HipLeft],y[Var.Item._HipLeft]);
        path.lineTo(x[Var.Item._HipRight],y[Var.Item._HipRight]);

        //Draw left leg
        path.moveTo(x[Var.Item._HipCenter],y[Var.Item._HipCenter]);
        path.lineTo(x[Var.Item._HipLeft],y[Var.Item._HipLeft]);
        path.lineTo(x[Var.Item._KneeLeft],y[Var.Item._KneeLeft]);
        path.lineTo(x[Var.Item._AnkleLeft],y[Var.Item._AnkleLeft]);
        path.lineTo(x[Var.Item._FootLeft],y[Var.Item._FootLeft]);

        //Draw right leg
        path.moveTo(x[Var.Item._HipCenter],y[Var.Item._HipCenter]);
        path.lineTo(x[Var.Item._HipRight],y[Var.Item._HipRight]);
        path.lineTo(x[Var.Item._KneeRight],y[Var.Item._KneeRight]);
        path.lineTo(x[Var.Item._AnkleRight],y[Var.Item._AnkleRight]);
        path.lineTo(x[Var.Item._FootRight],y[Var.Item._FootRight]);

        //Draw left arm
        path.moveTo(x[Var.Item._ShoulderLeft],y[Var.Item._ShoulderLeft]);
        path.lineTo(x[Var.Item._ElbowLeft],y[Var.Item._ElbowLeft]);
        path.lineTo(x[Var.Item._WristLeft],y[Var.Item._WristLeft]);
        path.lineTo(x[Var.Item._HandLeft],y[Var.Item._HandLeft]);

        //Draw right arm
        path.moveTo(x[Var.Item._ShoulderRight],y[Var.Item._ShoulderRight]);
        path.lineTo(x[Var.Item._ElbowRight],y[Var.Item._ElbowRight]);
        path.lineTo(x[Var.Item._WristRight],y[Var.Item._WristRight]);
        path.lineTo(x[Var.Item._HandRight],y[Var.Item._HandRight]);
        canvas.drawPath(path,paintSkeleton);

        // 카운트 쓰레시홀드
        canvas.drawLine(0,threshold,width,threshold,paintThreshod);
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
            x[i] = x[i] * width / Var.Item._DepthWidth;
            y[i] = y[i] * height / Var.Item._DepthHeight;
        }
        threshold = Integer.parseInt(positions[40]) * height / Var.Item._DepthHeight;
    }
}

