package com.example.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdateTime;
    private static float SHAKE_THRESHOLD_GRAVITY = 2;

    private Sensor gyroScopeSensor;
    private static float MAX_TILT_ANGLE =30.0F;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gyroScopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.textview);
        view.setBackgroundColor(Color.CYAN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroScopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
        lastUpdateTime = System.currentTimeMillis();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyroscope(sensorEvent);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                gyroScopeSensor,
                sensorManager.SENSOR_DELAY_NORMAL);

    }

    private void getGyroscope(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float tiltAngleX = (float) Math.toDegrees(Math.atan2(x, Math.sqrt(y * y + z * z)));

        if (tiltAngleX > MAX_TILT_ANGLE) {
            // Display a warning to the user
            Toast.makeText(this, "Device tilted too much!", Toast.LENGTH_LONG).show();
        }
    }


    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;

        float x =values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_MOON;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z /SensorManager.GRAVITY_EARTH;

        float gForce = (float)Math.sqrt(gX*gX + gY*gY + gZ *gZ);

        long currentTime = System.currentTimeMillis();
        if(gForce >= SHAKE_THRESHOLD_GRAVITY)
        {
            if(currentTime - lastUpdateTime < 200){
                return;
            }
            lastUpdateTime = currentTime;
            TextView textView = (TextView) view;
            textView.setText("You shook me");
            Toast.makeText(this,"Device was shaken", Toast.LENGTH_LONG).show();
            if(color == true) {
                view.setBackgroundColor(Color.GREEN);
            }else{
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }
}