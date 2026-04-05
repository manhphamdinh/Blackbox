package com.example.blackbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

public class Puzzle20Fragment extends PuzzleBaseFragment {

    private BroadcastReceiver receiver;

    @Override
    public int getPuzzleId() { return 20; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle20, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null)
            getView().findViewById(R.id.imageView0).startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.slideright)
            );
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                animation(0);
            }
        };
        requireContext().registerReceiver(receiver, new IntentFilter(Intent.ACTION_SHUTDOWN));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(receiver);
    }
}