package com.ssafy.medical.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector:SensorEventListener {
    private var SHAKE_THRESHOLD_GRAVITY:Float = 2.7F
    private var SHAKE_SLOP_TIME:Int = 500
    private var SHAKE_COUNT_RESET_TIME:Int = 3000
    private var mListener:OnShakeListener?= null
    private var mShakeTimeStamp:Long = 0L
    private var mShakeCount:Int = 0

    fun setOnShakeListener(listener: OnShakeListener){
        this.mListener = listener
    }
    interface OnShakeListener {
        fun onShake(count: Int)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (mListener != null) {
            // x,y,z 축의 값을 받아온다
            var x: Float = event!!.values[0];
            var y: Float = event.values[1];
            var z: Float = event.values[2];
            // 중력 가속도값으로 나눈 값으로 만든다
            var gX: Float = x / SensorManager.GRAVITY_EARTH;
            var gY: Float = y / SensorManager.GRAVITY_EARTH;
            var gZ: Float = z / SensorManager.GRAVITY_EARTH;

            var gForce: Float = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
            // 진동을 감지했을 때
            // gforce가 기준치 이상일 경우
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis();
                // 진동 간격이 너무 짧을 때는 무시
                // ignore shake events too close to each other (500ms)
                if (mShakeTimeStamp + SHAKE_SLOP_TIME > now) {
                    return;
                }
                // 3초 이상 걸렸을 때 reset한다
                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimeStamp + SHAKE_COUNT_RESET_TIME < now) {
                    mShakeCount = 0;
                }
                // 업데이트한다
                mShakeTimeStamp = now;
                mShakeCount++;
                // 흔들렸을 때 행동을 설정한다
                mListener!!.onShake(mShakeCount);

            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}