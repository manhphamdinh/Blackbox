package com.example.blackbox;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static java.lang.Math.acos;

public class Puzzle1Fragment extends PuzzleBaseFragment implements SensorEventListener {

    private static final double THRESHOLD = 9.7;
    private SensorManager mSensorManager;

    @Override
    public int getPuzzleId() { return 1; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle1, container, false);
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
        ImageView fluid = getView().findViewById(R.id.fluid);
        float x = event.values[0], y = event.values[1], z = event.values[2];
        int deviceHeight = MainActivity.getDeviceHeightAndWidth(requireContext()).first;
        int deviceWidth = MainActivity.getDeviceHeightAndWidth(requireContext()).second;
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) fluid.getLayoutParams();
        marginParams.topMargin = deviceHeight / 2;
        fluid.setPivotX(deviceWidth / 2f + 5000);
        fluid.setPivotY(0);
        fluid.setRotation((float) Math.toDegrees(acos(y / 9.8)));
        if (x < 0) fluid.setRotation(fluid.getRotation() * -1);
        if (Double.isNaN(fluid.getRotation())) {
            fluid.setRotation(y > 0 ? 0 : 180);
        }
        if (x < -THRESHOLD) animation(0);
        if (x > THRESHOLD) animation(1);
        if (y < -THRESHOLD) animation(2);
        if (y > THRESHOLD) animation(3);
        if (z < -THRESHOLD) animation(4);
        if (z > THRESHOLD) animation(5);
        getView().findViewById(R.id.ll).invalidate();
    }
}