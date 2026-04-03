package com.example.blackbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

public class Puzzle6Fragment extends PuzzleBaseFragment {

    private BroadcastReceiver receiver;

    @Override
    public int getPuzzleId() { return 6; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle6, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                animation(0);
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addDataScheme("file");
        int flags = ContextCompat.RECEIVER_EXPORTED;

        ContextCompat.registerReceiver(requireContext(), receiver, filter, flags);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(receiver);
    }
}