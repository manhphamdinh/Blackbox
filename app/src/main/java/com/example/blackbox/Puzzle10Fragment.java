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

import androidx.core.app.ActivityCompat;

public class Puzzle10Fragment extends PuzzleBaseFragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private SoundMeter sm;

    @Override
    public int getPuzzleId() { return 10; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle10, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        // Kiểm tra sensor trước khi đăng ký để tránh crash
        Sensor gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravity != null) {
            mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            if (sm == null) {
                sm = new SoundMeter(requireContext());
            }
            try {
                sm.start();
            } catch (Exception e) { e.printStackTrace(); }
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
        // 1. Chặn Null sớm
        if (!isAdded() || getView() == null || sm == null) return;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) return;

        final double amp = sm.getAmplitude();

        // 2. Chạy trên UI Thread
        getActivity().runOnUiThread(() -> {
            View rootView = getView();
            if (rootView == null) return;

            WavyLineView wavyView = rootView.findViewById(R.id.wavyLineView);
            if (wavyView != null) {
                // Tính toán biên độ an toàn
                int calculatedAmp = (int) (90 * amp / 32767);
                wavyView.setAmplitude(calculatedAmp);
                wavyView.setPeriod(0.1f);
            }
        });
    }
}