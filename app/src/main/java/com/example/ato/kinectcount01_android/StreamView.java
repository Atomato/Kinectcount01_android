package com.example.ato.kinectcount01_android;

import android.graphics.Bitmap;
import android.graphics.Rect;
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
    private Paint paintThreshold;
    private Path path;
    private int[] x;
    private int[] y;
    private int threshold;
    private int height;
    private int width;
    public Bitmap depthBitmap;
    private Rect dst;

    public StreamView(Context context) {
        super(context);

        paintSkeleton = new Paint();
        paintSkeleton.setColor(Color.RED);
        paintSkeleton.setStrokeWidth(10f);
        paintSkeleton.setStyle(Paint.Style.STROKE);
        paintThreshold = new Paint();
        paintThreshold.setColor(Color.CYAN);
        paintThreshold.setStrokeWidth(7f);
        path = new Path();

        x = new int[20];
        y = new int[20];

        depthBitmap = Bitmap.createBitmap(Var._DepthWidth, Var._DepthHeight, Bitmap.Config.ARGB_8888);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(depthBitmap,null,dst,null);

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
        canvas.drawLine(0,threshold,width,threshold,paintThreshold);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        // 뷰의 현재 사이즈
        height = yNew;
        width = xNew;

        dst = new Rect(0,0,width,height);
        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    // 데이터를 받아 뎁스 도트 이미지를 만들고 스켈레톤 관절 좌표를 업데이트
    public void useData(byte[] data) {
        //뎁스 이미지
        int xIndex;
        int yIndex;
        int pixelData;
        for (int j = 0; j < Var._DepthWidth*Var._DepthHeight/8; j++){
            xIndex = j*8 % Var._DepthWidth;
            yIndex = j*8 / Var._DepthWidth;

            pixelData = ((data[j] & 0x01)) * 0xFF;
            depthBitmap.setPixel(xIndex, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x02) >> 1) * 0xFF;
            depthBitmap.setPixel(xIndex+1, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x04) >> 2) * 0xFF;
            depthBitmap.setPixel(xIndex+2, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x08) >> 3) * 0xFF;
            depthBitmap.setPixel(xIndex+3, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x10) >> 4) * 0xFF;
            depthBitmap.setPixel(xIndex+4, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x20) >> 5) * 0xFF;
            depthBitmap.setPixel(xIndex+5, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x40) >> 6) * 0xFF;
            depthBitmap.setPixel(xIndex+6, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
            pixelData = ((data[j] & 0x80) >> 7) * 0xFF;
            depthBitmap.setPixel(xIndex+7, yIndex, Color.argb(0xFF, pixelData, pixelData, pixelData));
        }

        //스켈레톤
        for(int i=0;i<20;i++) {
            x[i] = data[Var._DepthBytesNum + 2*i];
            y[i] = data[Var._DepthBytesNum + 2*i+1];
            // x 와 y 위치를 뷰 크기에 맞춤
            x[i] = x[i] * width / Var._DepthWidth;
            y[i] = y[i] * height / Var._DepthHeight;
        }
        threshold = (data[Var._CountOffset] & 0x7F) * height / Var._DepthHeight;
    }
}

