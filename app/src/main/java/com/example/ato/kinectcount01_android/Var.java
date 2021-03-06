package com.example.ato.kinectcount01_android;

/**
 * Created by Ato on 2017-06-28.
 */

public class Var {

        // 키넥트 뎁스 이미지의 해상도
        final static int    _DepthHeight = 60;
        final static int    _DepthWidth = 80;
        //서버에서 오는 뎁스 바이트 수
        final static  int _DepthBytesNum = _DepthHeight*_DepthWidth / 8;
        //서버에서 오는 스켈레톤 바이트 수 (관절 20개. X, Y 좌표 각각 두 개씩)
        final static  int _SkelBytesNum = 40;

        final static int _DepthOffset = 1;
        final static int _SkelOffset = _DepthOffset + _DepthBytesNum;
        final static int _CountOffset = _SkelOffset + _SkelBytesNum;

        // 조인트 인덱스
        final static int _HipCenter = 0;
        final static int _Spine = 1;
        final static int _ShoulderCenter = 2;
        final static int _Head = 3;
        final static int _ShoulderLeft = 4;
        final static int _ElbowLeft = 5;
        final static int _WristLeft = 6;
        final static int _HandLeft = 7;
        final static int _ShoulderRight = 8;
        final static int _ElbowRight = 9;
        final static int _WristRight = 10;
        final static int _HandRight = 11;
        final static int _HipLeft = 12;
        final static int _KneeLeft = 13;
        final static int _AnkleLeft = 14;
        final static int _FootLeft = 15;
        final static int _HipRight = 16;
        final static int _KneeRight = 17;
        final static int _AnkleRight = 18;
        final static int _FootRight = 19;

}
