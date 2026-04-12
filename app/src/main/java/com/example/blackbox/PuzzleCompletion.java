package com.example.blackbox;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class PuzzleCompletion {
    private final Context context;
    private final String solvedKey;

    public PuzzleCompletion(Context context) {
        this.context = context;
        this.solvedKey = context.getString(R.string.prefSolved);
    }

    public void markCompleted(int puzzleId, int boxIndex) {
        String tag = puzzleId + ":" + boxIndex;

        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.prefCompletion), Context.MODE_PRIVATE);

        String existing = pref.getString(solvedKey, "[]");

        try {
            JSONArray array = new JSONArray(existing);

            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(tag)) return;
            }

            array.put(tag);
            pref.edit().putString(solvedKey, array.toString()).apply();

        } catch (JSONException ignored) {}
    }

    public static void resetAllCompletion(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.prefCompletion), Context.MODE_PRIVATE);

        pref.edit().clear().apply();
    }
}
