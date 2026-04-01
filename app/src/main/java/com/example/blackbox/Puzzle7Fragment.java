package com.example.blackbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

public class Puzzle7Fragment extends PuzzleBaseFragment {

    private static final double PERCENTAGE = .75;
    private int RADIUS = 1200;

    @Override
    public int getPuzzleId() { return 7; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_puzzle7, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        puzzleCompletedClock(new Date().getHours() % 12);

        SharedPreferences pref = requireContext().getSharedPreferences(
                getString(R.string.pref), Context.MODE_PRIVATE
        );
        String completed = pref.getString(getString(R.string.prefClock), "[]");
        try {
            JSONArray jsonArray = new JSONArray(completed);
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getInt(i));
            }
            for (Integer i : set) {
                arc(view, i * 30);
            }
            if (set.size() == 12) animation(0);
        } catch (JSONException ignored) {}
    }

    private void puzzleCompletedClock(int hour) {
        Context context = getContext();
        if (context == null) return;
        SharedPreferences pref = context.getSharedPreferences(
                getString(R.string.pref), Context.MODE_PRIVATE
        );
        try {
            String existing = pref.getString(getString(R.string.prefClock), "[]");
            JSONArray jsonArray = new JSONArray(existing);
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getInt(i));
            }
            set.add(hour);
            JSONArray newArray = new JSONArray();
            for (int val : set) newArray.put(val);
            pref.edit().putString(getString(R.string.prefClock), newArray.toString()).apply();
        } catch (JSONException ignored) {}
    }

    private void arc(View root, int start) {
        int screenH = MainActivity.getDeviceHeightAndWidth(requireContext()).first;
        int screenW = MainActivity.getDeviceHeightAndWidth(requireContext()).second;
        RADIUS = Collections.min(Arrays.asList(RADIUS, screenH - 64, screenW - 64));

        ShapeDrawable arcShape = new ShapeDrawable(new ArcShape(start - 90, 30));
        arcShape.getPaint().setColor(requireContext().getResources().getColor(R.color.bg, null));
        arcShape.setIntrinsicHeight(RADIUS);
        arcShape.setIntrinsicWidth(RADIUS);
        ImageView imageView = new ImageView(requireContext());
        imageView.setX((float) (screenW / 2.0 - RADIUS / 2.0));
        imageView.setY((float) (screenH / 2.0 - RADIUS / 2.0 - 75 / 2.0));
        imageView.setImageDrawable(arcShape);
        ((ViewGroup) root.findViewById(R.id.ll)).addView(imageView);

        ShapeDrawable arcShape2 = new ShapeDrawable(new ArcShape(start - 90, 30));
        arcShape2.getPaint().setColor(requireContext().getResources().getColor(R.color.puzzle7translucent, null));
        arcShape2.setIntrinsicHeight((int) (RADIUS * PERCENTAGE));
        arcShape2.setIntrinsicWidth((int) (RADIUS * PERCENTAGE));
        ImageView imageView2 = new ImageView(requireContext());
        imageView2.setX((float) (screenW / 2.0 - RADIUS * PERCENTAGE / 2.0));
        imageView2.setY((float) (screenH / 2.0 - RADIUS * PERCENTAGE / 2.0 - 75 / 2.0));
        imageView2.setImageDrawable(arcShape2);
        ((ViewGroup) root.findViewById(R.id.ll)).addView(imageView2);

        root.findViewById(R.id.imageView0).bringToFront();
    }
}