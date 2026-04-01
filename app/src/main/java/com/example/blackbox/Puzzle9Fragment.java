package com.example.blackbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Puzzle9Fragment extends PuzzleBaseFragment implements SensorEventListener {

    private static final double THRESHOLD = 2000;
    private final int ballSize = 300;
    private SensorManager mSensorManager;
    private SoundMeter sm;

    @Override
    public int getPuzzleId() { return 9; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle9, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_UI);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            if (sm == null) {
                sm = new SoundMeter();
                sm.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (sm != null) sm.stop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getView() == null || sm == null) return;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) return;

        double amp = sm.getAmplitude();
        if (amp > (22000 - THRESHOLD)) animation(0);
        if (Math.abs(amp - 10000) < THRESHOLD) animation(1);
        if (amp < THRESHOLD) animation(2);

        int deviceHeight = MainActivity.getDeviceHeightAndWidth(requireContext()).first;
        int deviceWidth = MainActivity.getDeviceHeightAndWidth(requireContext()).second;
        ((ViewGroup) getView().findViewById(R.id.merge)).removeAllViews();
        for (int i = 0; i < amp / 3000; i++) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(R.drawable.circle);
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.puzzle9translucent));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ballSize, ballSize);
            params.topMargin = (int) (Math.random() * (deviceHeight - ballSize));
            params.leftMargin = (int) (Math.random() * (deviceWidth - ballSize));
            imageView.setLayoutParams(params);
            ((RelativeLayout) getView().findViewById(R.id.merge)).addView(imageView, 0);
        }
    }
}