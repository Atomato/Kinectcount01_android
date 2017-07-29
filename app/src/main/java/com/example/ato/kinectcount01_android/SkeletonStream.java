package com.example.ato.kinectcount01_android;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.widget.TextView;

public class SkeletonStream extends AppCompatActivity {
    private Socket socket;
    private BufferedInputStream socket_in;
    private StreamView myView;
    private PrintWriter socket_out;
    private byte[] data = new byte[Var._DepthBytesNum + Var._SkelBytesNum];
    private byte[] positions = new byte[40];
    private Handler handler;
    TextView breakTimeView;
    private TextView countNumText;
    private TextView setNumText;
    private TextView breakTimeText;
    private int countNum = 0;
    private int setCountNum = 0; // 현재까지 세트 수
    final static int numPerSet = 3; // 세트당 횟수
    final static int setNum = 2; // 전체 세트 수
    final static int breakTime = 5; // 휴식 시간
    int timeValue;
    boolean IsBreakTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skeleton_stream);

        // full screen 만들기 api level 19 이상
        int uiOption = getWindow().getDecorView().getSystemUiVisibility();
        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOption);


        // xml 구성원들
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        breakTimeView = (TextView) findViewById(R.id.breakTimeView);
        countNumText = (TextView) findViewById(R.id.countNumText);
        countNumText.setText("0/" +  numPerSet);
        setNumText = (TextView) findViewById(R.id.setNumText);
        setNumText.setText("0/" +  setNum);
        breakTimeText = (TextView) findViewById(R.id.breakTimeText);
        breakTimeText.setText(String.valueOf(breakTime));
        //

        // StreamView 세팅
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        myView = new StreamView(this);
        myView.setLayoutParams(params);
        linearLayout.addView(myView);
        //

        timeValue = breakTime;
        handler = new Handler();

        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("192.168.0.9", 8200);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    //socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    socket_in = new BufferedInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        if (!IsBreakTime) {
                            // 서버에 조인트 좌표 요청
                            socket_out.println("y");

                            socket_in.read(data, 0, data.length);
                            if ((data[0]&0xFF) != 0xFF){ // 서버쪽에서 "n\n" 을 안 보냈을 경우
                                myView.useData(data);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myView.invalidate(); // myView 다시 그림
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        };

        /*
        Thread counter = new Thread() {
            public void run() {
                while (true) {
                    if (countNum >= numPerSet) {
                        countNum = 0;
                        setCountNum++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setNumText.setText(setCountNum + "/" + setNum);
                                myView.setVisibility(View.INVISIBLE);
                                breakTimeView.setVisibility(View.VISIBLE);
                            }
                        });
                        IsBreakTime = true;

                        timer.sendEmptyMessageDelayed(0,1000);
                    }
                }
            }
        };
*/
        worker.start();
       //counter.start();

    }
/*
    Handler timer = new Handler() {
        public void handleMessage(Message msg) {
            timeValue--;
            if (timeValue == 0){
                IsBreakTime = false;
                timeValue = breakTime;
                breakTimeText.setText(String.valueOf(timeValue));
                myView.setVisibility(View.VISIBLE);
                breakTimeView.setVisibility(View.INVISIBLE);
            }
            else {
                breakTimeText.setText(String.valueOf(timeValue));

                timer.sendEmptyMessageDelayed(0,1000);
            }

        }
    };*/

    @Override
    // 마이뷰의 가로를 세로 길이의 3분의 4가 되도록
    public void onResume(){
        super.onResume();
        myView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                ViewGroup.LayoutParams layout = myView.getLayoutParams();
                layout.width = myView.getHeight() * 4 / 3;
                myView.setLayoutParams(layout);
                removeOnGlobalLayoutListener(myView, this);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            socket_in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {

        }
    }
}
