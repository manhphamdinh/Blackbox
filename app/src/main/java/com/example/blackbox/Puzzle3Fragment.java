package com.example.blackbox;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Puzzle3Fragment extends PuzzleBaseFragment implements SensorEventListener {

    private SensorManager mSensorManager;

    @Override
    public int getPuzzleId() { return 3; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle3, container, false);
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
        AudioManager am = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        if (am.isWiredHeadsetOn()) animation(2);
        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) animation(3);
        int media = am.getStreamVolume(AudioManager.STREAM_RING);
        if (media < 1) animation(1);
        else if (media == am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) animation(0);

        ImageView fluid = getView().findViewById(R.id.fluid);
        int deviceHeight = MainActivity.getDeviceHeightAndWidth(requireContext()).first;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fluid.getLayoutParams();
        params.height = (int) (deviceHeight * ((double) media / am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        fluid.setLayoutParams(params);
    }
}