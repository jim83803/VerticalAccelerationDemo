package com.s10576019.cyel.verticalaccelerationdemo.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationManager implements SensorEventListener {
    public interface OrientationListener {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }

    //常數
    public static final int STATE_IDLE = 1;
    public static final int STATE_RUNNING = 2;

    //變數
    private int state = STATE_IDLE;
    private float[] accelerations;
    private float[] geomagnetic;
    float orientation[] = new float[3]; //azimuth, pitch, roll

    //物件
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private OrientationListener orientationListener;

    //建構子
    public OrientationManager(Context context) {
        Log.i(this.getClass().getName(), "取得SensorManager");
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        Log.i(this.getClass().getName(), "取得AccelerometerSensor和MagneticFieldSensor");
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelerometer == null) {
            Log.i(this.getClass().getName(), "AccelerometerSensor取得失敗。");
        }
        if (magnetometer == null) {
            Log.i(this.getClass().getName(), "MagneticFieldSensor取得失敗");
        }
    }

    //公開方法
    public void start(OrientationListener orientationListener) {
        this.orientationListener = orientationListener;

        if (accelerometer != null && magnetometer != null) {
            Log.i(this.getClass().getName(), "開始");
            if (state == STATE_IDLE) {
                mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            }
            state = STATE_RUNNING;
        } else if (accelerometer == null) {
            Log.i(this.getClass().getName(), "AccelerometerSensor取得失敗，無法開始。");
        } else {
            Log.i(this.getClass().getName(), "MagneticFieldSensor尚未取得，無法開始。");
        }
    }
    public void stop() {
        Log.i(this.getClass().getName(), "結束");
        if (state == STATE_RUNNING) {
            mSensorManager.unregisterListener(this);
        }
        state = STATE_IDLE;
    }

    //Getters
    public int getState() {
        return state;
    }

    //接口實作
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerations = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;
        if (accelerations != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelerations, geomagnetic);
            if (success) {
                SensorManager.getOrientation(R, orientation);
                if (orientationListener != null) {
                    orientationListener.onOrientationChanged(orientation[0], orientation[1], orientation[2]);
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
