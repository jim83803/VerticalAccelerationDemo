package com.s10576019.cyel.verticalaccelerationdemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.s10576019.cyel.verticalaccelerationdemo.sensors.VerticalAccelerationFinder;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements VerticalAccelerationFinder.VerticalAccelerationListener {
    private TextView verticalAccTextView, xAccTextView, yAccTextView, zAccTextView, pitchTextView, rollTextView;
    private VerticalAccelerationFinder verticalAccelerationFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verticalAccelerationFinder = new VerticalAccelerationFinder(this);

        verticalAccTextView = (TextView) findViewById(R.id.verticalAcc);
        xAccTextView = (TextView) findViewById(R.id.xAcc);
        yAccTextView = (TextView) findViewById(R.id.yAcc);
        zAccTextView = (TextView) findViewById(R.id.zAcc);
        pitchTextView = (TextView) findViewById(R.id.pitch);
        rollTextView = (TextView) findViewById(R.id.roll);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.i(this.getClass().getName(), String.format(Locale.getDefault(),"%.2f %.2f %.2f", event.values[0], event.values[1], event.values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (verticalAccelerationFinder.getState() == VerticalAccelerationFinder.STATE_IDLE) {
//            verticalAccelerationFinder.start(this);
//        }
    }

    @Override
    protected void onStop() {
        if (verticalAccelerationFinder.getState() == VerticalAccelerationFinder.STATE_RUNNING) {
            verticalAccelerationFinder.stop();
        }
        super.onStop();
    }

    @Override
    public void onVerticalAccelerationChanged(double verticalAcceleration) {
        verticalAccTextView.setText(String.format(Locale.getDefault(), "%02.3f", verticalAcceleration));
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        pitchTextView.setText(String.format(Locale.getDefault(), "%02.3f", pitch));
        rollTextView.setText(String.format(Locale.getDefault(), "%02.3f", roll));
    }

    @Override
    public void onAccelerationChanged(double xAcc, double yAcc, double zAcc) {
        xAccTextView.setText(String.format(Locale.getDefault(), "%02.3f", xAcc));
        yAccTextView.setText(String.format(Locale.getDefault(), "%02.3f", yAcc));
        zAccTextView.setText(String.format(Locale.getDefault(), "%02.3f", zAcc));
    }
}
