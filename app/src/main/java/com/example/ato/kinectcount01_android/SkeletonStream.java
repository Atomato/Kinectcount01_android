package com.example.ato.kinectcount01_android;

import android.annotation.TargetApi;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.widget.TextView;

public class SkeletonStream extends AppCompatActivity {
    private Socket socket;
    private BufferedInputStream socket_in;
    private StreamView myView;
    private PrintWriter socket_out;
    private byte[] data = new byte[Var._CountOffset + 1];
    private Handler handler;
    private TextView breakTimeView;
    private TextView countNumText;
    private TextView setNumText;
    private TextView breakTimeText;
    private int countNum = 0;
    private int setCountNum = 0; // 현재까지 세트 수
    final static int numPerSet = 5; // 세트당 횟수
    final static int setNum = 2; // 전체 세트 수
    final static int breakTime = 5; // 휴식 시간 (초)
    int timeValue;
    boolean IsBreakTime;
    //사운드 멤버
    private SoundPool soundPool;
    private int[] sm;
    private int[] smETC;
    private static Handler timer;


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

        //sound setting
        int maxStreams = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }
        int temp;
        sm = new int[20];
        // fill your sounds
        for (int i=1; i<21; i++){
            temp = getResources().getIdentifier("sound"+i, "raw", getPackageName());
            sm[i-1] = soundPool.load(this,temp,1);
        }
        smETC = new int[4];
        smETC[0] = soundPool.load(this,R.raw.break_time,1);
        smETC[1] = soundPool.load(this,R.raw.re_start,1);
        smETC[2] = soundPool.load(this,R.raw.done,1);
        smETC[3] = soundPool.load(this,R.raw.cheer_up,1);
        //

        timeValue = breakTime;
        handler = new Handler();

        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("192.168.0.9", 8200);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_in = new BufferedInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    int countStopTIme = 0; //카운트가 얼마 동안 안 오르고 있는지
                    while (true) {
                        if (!IsBreakTime) {
                            // 서버에 조인트 좌표 요청
                            socket_out.println("y");

                            int i = socket_in.read(data, 0, data.length);
                            if ((data[0]&0xFF) != 0xFF){ // 서버쪽에서 데이터가 준비되어 있을 경우
                                myView.useData(data);

                                if ((data[0]&0x80) == 0x80){ //initialize
                                    countNum = 0;
                                    setCountNum = 0;
                                    countStopTIme = 0;
                                }

                                //MSB 가 카운트 업을 나타냄
                                if (((data[Var._CountOffset]&0x80) >> 7) == 1){
                                    countNum ++;
                                    countStopTIme = 0;
                                    soundPool.play(sm[countNum -1], 1, 1, 1, 0, 1f);
                                }
                                else {
                                    countStopTIme ++;
                                }

                                //300/30 = 10초 동안 가만히 있을 경우 운동 재촉
                                if (countStopTIme == 300){
                                    soundPool.play(smETC[3], 1, 1, 1, 0, 1f);
                                    countStopTIme = 0;
                                }
                                countCheck();

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myView.invalidate(); // myView 다시 그림
                                        countNumText.setText(countNum + "/" + numPerSet);
                                        setNumText.setText(setCountNum + "/" + setNum);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        };

        timer = new Handler() {
            public void handleMessage(Message msg) {
                timeValue--;
                if (timeValue == 0){
                    IsBreakTime = false;
                    soundPool.play(smETC[1], 1, 1, 1, 0, 1f); //세트 다시 시작
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
        };

        worker.start();
    }

    private void countCheck(){
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
            soundPool.play(smETC[0], 1, 1, 1, 0, 1f); //break_time

            timer.sendEmptyMessageDelayed(0,1000);
        }
    }

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
