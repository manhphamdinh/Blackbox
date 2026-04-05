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
        activity.runOnUiThread(() -> {
            ImageView iv = activity.findViewById(
                    activity.getResources().getIdentifier("imageView" + index, "id", activity.getPackageName())
            );
            if (iv == null) return;
            iv.setBackgroundResource(R.drawable.animation);
            ((AnimationDrawable) iv.getBackground()).start();
            saveBoxCompleted(index);
        });
    }

    private void saveBoxCompleted(int boxIndex) {
        Context context = getContext();
        if (context == null) return;
        String key = getPuzzleId() + ":" + boxIndex;
        SharedPreferences pref = context.getSharedPreferences(
                getString(R.string.pref), Context.MODE_PRIVATE
        );
        String existing = pref.getString(getString(R.string.prefSolved), "[]");
        try {
            JSONArray jsonArray = new JSONArray(existing);
            // cek apakah sudah ada
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(key)) return; // sudah ada
            }
            jsonArray.put(key);
            pref.edit().putString(getString(R.string.prefSolved), jsonArray.toString()).apply();
        } catch (JSONException ignored) {}
    }

    protected abstract int getPuzzleId();

// Xóa hàm puzzleCompleted() cũ đi
}