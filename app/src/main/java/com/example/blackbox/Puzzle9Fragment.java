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

        // Kiểm tra xem cảm biến có tồn tại không trước khi đăng ký
        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravitySensor != null) {
            mSensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            if (sm == null) {
                sm = new SoundMeter(requireContext());
            }
            try {
                sm.start();
            } catch (Exception e) {
                e.printStackTrace();
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
        // 1. Kiểm tra Fragment còn gắn vào Activity không
        if (!isAdded() || getContext() == null || getView() == null || sm == null) return;

        // 2. Kiểm tra quyền ghi âm
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) return;

        final double amp = sm.getAmplitude();

        // 3. Chuyển sang UI Thread
        getActivity().runOnUiThread(() -> {
            View rootView = getView();
            Context safeContext = getContext();
            if (rootView == null || safeContext == null) return;

            if (amp > 0) {
                if (amp > (22000 - THRESHOLD)) animation(0);
                if (Math.abs(amp - 10000) < THRESHOLD) animation(1);
                if (amp > 0.001 && amp < THRESHOLD) animation(2);
            }

            RelativeLayout mergeLayout = rootView.findViewById(R.id.merge);
            if (mergeLayout != null) {
                mergeLayout.removeAllViews();

                int numBalls = Math.min((int) (amp / 3000), 10);

                // Dùng safeContext thay vì requireContext()
                var sizeData = MainActivity.getDeviceHeightAndWidth(safeContext);
                int deviceHeight = sizeData.first;
                int deviceWidth = sizeData.second;

                for (int i = 0; i < numBalls; i++) {
                    ImageView imageView = new ImageView(safeContext);
                    imageView.setImageResource(R.drawable.circle);
                    imageView.setColorFilter(ContextCompat.getColor(safeContext, R.color.puzzle9translucent));

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ballSize, ballSize);
                    // Đảm bảo không bị số âm khi random
                    int maxTop = Math.max(1, deviceHeight - ballSize);
                    int maxLeft = Math.max(1, deviceWidth - ballSize);

                    params.topMargin = (int) (Math.random() * maxTop);
                    params.leftMargin = (int) (Math.random() * maxLeft);

                    mergeLayout.addView(imageView);
                }
            }
        });
    }
}