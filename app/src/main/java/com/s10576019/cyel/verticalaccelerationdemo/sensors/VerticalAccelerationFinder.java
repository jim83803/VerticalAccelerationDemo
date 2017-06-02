package com.s10576019.cyel.verticalaccelerationdemo.sensors;

import android.content.Context;
import android.util.Log;

/**
 * Created by Jim on 2017/6/1.
 */

public class VerticalAccelerationFinder implements SimpleAccelerometerManager.AccelerometerListener, OrientationManager.OrientationListener{
    //接口
    public interface VerticalAccelerationListener {
        void onVerticalAccelerationChanged(double verticalAcceleration);
        void onOrientationChanged(float pitch, float roll);
        void onAccelerationChanged(double xAcc, double yAcc, double zAcc);
    }

    //常數
    public static final int STATE_IDLE = 1;
    public static final int STATE_RUNNING = 2;

    //變數
    private int state = STATE_IDLE;
    private double xAcc, yAcc, zAcc;
    private float azimuth, pitch, roll;

    //物件
    private SimpleAccelerometerManager simpleAccelerometerManager;
    private OrientationManager orientationManager;
    private VerticalAccelerationListener verticalAccelerationListener;

    //建構子
    public VerticalAccelerationFinder (Context context) {
        simpleAccelerometerManager = new SimpleAccelerometerManager(context, SimpleAccelerometerManager.FILTER_MODE_LOW_PASS, 1.0);
        orientationManager = new OrientationManager(context);
    }

    //公開方法
    public void start(VerticalAccelerationListener verticalAccelerationListener) {
        this.verticalAccelerationListener = verticalAccelerationListener;

        if (state == STATE_IDLE) {
            orientationManager.start(this);
            simpleAccelerometerManager.start(this);
        }
        state = STATE_RUNNING;
    }

    public void stop() {
        if (state == STATE_RUNNING) {
            orientationManager.stop();
            simpleAccelerometerManager.stop();
        }
        state = STATE_IDLE;
    }

    //Getters
    public int getState() {
        return state;
    }

    //接口實作
    @Override
    public void onNewValue(double x, double y, double z) {
        VerticalAccelerationFinder.this.xAcc = x;
        VerticalAccelerationFinder.this.yAcc = y;
        VerticalAccelerationFinder.this.zAcc = z;
        //算出對地的分量
        if (verticalAccelerationListener != null) {
            verticalAccelerationListener.onVerticalAccelerationChanged(z/Math.cos(pitch)/Math.cos(roll));
            verticalAccelerationListener.onAccelerationChanged(xAcc,yAcc,zAcc);
        }
        Log.i(this.getClass().getName(), String.valueOf(z/Math.cos(pitch)/Math.cos(roll)));
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        VerticalAccelerationFinder.this.azimuth = azimuth;
        VerticalAccelerationFinder.this.pitch = pitch;
        VerticalAccelerationFinder.this.roll = roll;
        if (verticalAccelerationListener != null) {
            verticalAccelerationListener.onOrientationChanged(pitch,roll);
        }
    }
}
