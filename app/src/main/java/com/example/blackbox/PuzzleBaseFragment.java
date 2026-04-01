package com.example.blackbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;

public abstract class PuzzleBaseFragment extends Fragment {

    protected void animation(int index) {
        Activity activity = getActivity();
        if (activity == null) return;
        ImageView iv = activity.findViewById(
                activity.getResources().getIdentifier("imageView" + index, "id", activity.getPackageName())
        );
        if (iv == null) return;
        iv.setBackgroundResource(R.drawable.animation);
        ((AnimationDrawable) iv.getBackground()).start();
        puzzleCompleted();
    }

    protected abstract int getPuzzleId();

    protected void puzzleCompleted() {
        Context context = getContext();
        if (context == null) return;
        String prefString = getString(R.string.prefSolved);
        SharedPreferences pref = context.getSharedPreferences(
                getString(R.string.pref), Context.MODE_PRIVATE
        );
        try {
            String existing = pref.getString(prefString, "[]");
            JSONArray jsonArray = new JSONArray(existing);
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getInt(i));
            }
            set.add(getPuzzleId());
            JSONArray newArray = new JSONArray();
            for (int val : set) {
                newArray.put(val);
            }
            pref.edit().putString(prefString, newArray.toString()).apply();
        } catch (JSONException ignored) {}
    }
}