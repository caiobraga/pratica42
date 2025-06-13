package com.caio.braga.pratica42;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor;
    private float currentLight = -1f, currentDistance = -1f;
    private boolean lightReady = false, proximityReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        Button botao = findViewById(R.id.botao1);
        botao.setOnClickListener(v -> {
            lightReady = false;
            proximityReady = false;

            if (lightSensor != null)
                sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

            if (proximitySensor != null)
                sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                currentLight = event.values[0];
                lightReady = true;
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                currentDistance = event.values[0];
                proximityReady = true;
            }

            if (lightReady && proximityReady) {
                sensorManager.unregisterListener(this);

                Intent result = new Intent();
                result.putExtra("light", currentLight);
                result.putExtra("distance", currentDistance);
                setResult(RESULT_OK, result);
                finish();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
}
