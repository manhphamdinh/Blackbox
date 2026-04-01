package com.example.blackbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    static ArrayList<ImageView> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<ImageView> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (tag.equals(child.getTag())) {
                views.add((ImageView) child);
            }
        }
        return views;
    }

    static Pair<Integer, Integer> getDeviceHeightAndWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return new Pair<>(displayMetrics.heightPixels, displayMetrics.widthPixels);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences(getString(R.string.pref), MODE_PRIVATE);
        String solved = pref.getString(getString(R.string.prefSolved), "[]");
        try {
            JSONArray jsonArray = new JSONArray(solved);
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getInt(i));
            }
            for (Integer i : set) {
                ArrayList<ImageView> ivs = getViewsByTag(
                        (ViewGroup) findViewById(R.id.ll), ".Puzzle" + i + "Activity"
                );
                for (ImageView iv : ivs) {
                    iv.setImageResource(R.drawable.filled);
                }
            }
        } catch (JSONException ignored) {}
    }

    public void puzzleLaunch(View view) {
        String tag = (String) view.getTag();
        // tag dạng ".Puzzle1Activity" → lấy số puzzle
        String numberStr = tag.replaceAll("[^0-9]", "");
        int puzzleId = Integer.parseInt(numberStr);
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.EXTRA_PUZZLE_ID, puzzleId);
        startActivity(intent);
    }
}