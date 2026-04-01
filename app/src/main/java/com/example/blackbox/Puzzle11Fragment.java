package com.example.blackbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shredzone.commons.suncalc.MoonIllumination;

public class Puzzle11Fragment extends PuzzleBaseFragment {

    private static final double THRESHOLD = 4.0 / 100;

    @Override
    public int getPuzzleId() { return 11; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle11, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MoonIllumination moonIllumination = MoonIllumination.compute()
                .now()
                .execute();
        if (moonIllumination.getFraction() < THRESHOLD) animation(0);
        if (moonIllumination.getFraction() > (1 - THRESHOLD)) animation(1);
    }
}