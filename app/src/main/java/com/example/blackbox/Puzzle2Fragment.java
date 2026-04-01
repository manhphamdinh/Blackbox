package com.example.blackbox;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Puzzle2Fragment extends PuzzleBaseFragment implements SensorEventListener {

    private static final double THRESHOLD = 10.1;
    private SensorManager mSensorManager;

    @Override
    public int getPuzzleId() { return 2; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle2, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getView() == null) return;
        try {
            int brightness = Settings.System.getInt(requireContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            if (brightness < THRESHOLD) animation(0);
            if (brightness > (255 - THRESHOLD)) animation(1);
            for (ImageView ray : MainActivity.getViewsByTag(
                    (ViewGroup) getView().findViewById(R.id.ll), "rays")) {
                ViewGroup.LayoutParams layoutParams = ray.getLayoutParams();
                layoutParams.height = (int) (100 * (brightness / 255.0));
                ray.setLayoutParams(layoutParams);
            }
        } catch (Settings.SettingNotFoundException ignored) {}
    }
}