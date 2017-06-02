package com.s10576019.cyel.verticalaccelerationdemo.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SimpleAccelerometerManager implements SensorEventListener {
    //接口
    public interface AccelerometerListener {
        void onNewValue(double x, double y, double z);
    }

    //常數
    public static final int FILTER_MODE_HIGH_PASS = 1;
    public static final int FILTER_MODE_LOW_PASS = 2;

    //變數
    protected int filterMode;
    protected double cutOffFrequency;
    private double alpha = 0.8; //預設值，程式中根據sensorDelayMode修改。
    private double gravity[] = {0, 0, 0};
    private double linear_acceleration[] = {0, 0, 0};

    //物件
    private SensorManager sensorManager;
    private Sensor sensor;
    private AccelerometerListener accelerometerListener;

    //建構子
    public SimpleAccelerometerManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public SimpleAccelerometerManager(Context context, int filterMode, double cutOffFrequency) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        setFilterMode(filterMode);
        setCutOffFrequency(cutOffFrequency);
    }

    //操作方法
    public void start(AccelerometerListener accelerometerListener) {
        int sensorDelayMode = SensorManager.SENSOR_DELAY_GAME;

        final double deltaTime = 20.0 / 1000.0;
        if (this.filterMode == 0) {
            throw new RuntimeException(this.getClass().getName() + "請呼叫setFilterMode(int filterMode)設定濾波模式");
        }
        if (this.cutOffFrequency == 0) {
            throw new RuntimeException(this.getClass().getName() + "請setCutOffFrequency(double cutOffFrequency )設定截止頻率，需大於0");
        }
        final double cutOffFrequency = this.cutOffFrequency;
        final double timeConstant = 1.0 / (2 * Math.PI * cutOffFrequency);
        alpha = timeConstant / (timeConstant + deltaTime);

        sensorManager.registerListener(this, sensor, sensorDelayMode);

        this.accelerometerListener = accelerometerListener;
    }
    public void stop() {
        accelerometerListener = null;
        sensorManager.unregisterListener(this);
    }

    //Setters
    public void setFilterMode(int filterMode) {
        this.filterMode = filterMode;
    }
    public void setCutOffFrequency(double cutOffFrequency) {
        this.cutOffFrequency = cutOffFrequency;
    }

    //Getters
    public int getFilterMode() {
        return filterMode;
    }
    public double getCutOffFrequency() {
        return cutOffFrequency;
    }

    //接口實作
    @Override
    public void onSensorChanged(SensorEvent event) {
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        if (filterMode == FILTER_MODE_HIGH_PASS) {
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
        } else if (filterMode == FILTER_MODE_LOW_PASS) {
            linear_acceleration[0] = gravity[0];
            linear_acceleration[1] = gravity[1];
            linear_acceleration[2] = gravity[2];
        } else {
            throw new RuntimeException(this.getClass().getName()
                    + "setFilterMode()");
        }

        if (accelerometerListener != null) {
            accelerometerListener.onNewValue(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
