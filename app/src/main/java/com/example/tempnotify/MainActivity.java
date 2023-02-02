package com.example.tempnotify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private static final String CHANNEL_ID = "defaultChannel";

    private static final String CHANNEL_NAME = "Default Channel";

    private NotificationManager notificationManager;

    private Thermometer thermometer;
    private float temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thermometer = (Thermometer) findViewById(R.id.thermometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAmbientTemperature();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterAll();
    }

    private void simulateAmbientTemperature() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                temperature = Utils.randInt(-10, 35);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thermometer.setCurrentTemp(temperature);
                        getSupportActionBar().setTitle(getString(R.string.app_name) + " : " + temperature);
                    }
                });
            }
        }, 0, 3500);
    }

    private void loadAmbientTemperature() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "No Ambient Temperature Sensor !", Toast.LENGTH_LONG).show();
        }
    }

    private void unregisterAll() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values.length > 0) {
            temperature = sensorEvent.values[0];
            thermometer.setCurrentTemp(temperature);

            if (temperature > 20) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentTitle("its hot!")
                        .setContentText("wear something short it is warm outside")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(0, builder.build());

                thermometer.innerPaint.setColor(Color.RED);
                thermometer.innerPaint.setStyle(Paint.Style.FILL);

            } else if (temperature < 0) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentTitle("its freezing!")
                        .setContentText("put on something warm it is cold outside")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(0, builder.build());

                thermometer.innerPaint.setColor(Color.BLUE);
                thermometer.innerPaint.setStyle(Paint.Style.FILL);
            } else {
                thermometer.innerPaint.setColor(Color.DKGRAY);
                thermometer.innerPaint.setStyle(Paint.Style.FILL);
            }


            getSupportActionBar().setTitle(getString(R.string.app_name) + " : " + temperature);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}