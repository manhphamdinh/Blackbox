package com.example.blackbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Puzzle15Fragment extends PuzzleBaseFragment {

    private static final double[] MILESTONES  = { 10, 100, 1000, 10000 };

    private final ImageView[] boxes  = new ImageView[4];
    private final int[] boxIds = {
            R.id.imageView0,
            R.id.imageView1,
            R.id.imageView2,
            R.id.imageView3
    };

    private LocationManager locationManager;
    private Location lastLocation = null;
    private double totalDistance = 0;
    private DistanceRadarView radarView;


    @Override
    public int getPuzzleId() { return 15; }

    @Override
    public int getTotalBoxes()  { return boxes.length; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_puzzle15, container, false);

        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = root.findViewById(boxIds[i]);
        }
        radarView = root.findViewById(R.id.radarView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        syncProgress();

        ImageView pin = view.findViewById(R.id.pinIcon);

        radarView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        radarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        placeBoxes();
                        syncProgress();

                        // Place pin
                        pin.setX((radarView.getWidth() / 2f) - (pin.getWidth() / 2f));
                        pin.setY((radarView.getHeight() * 0.80f) - pin.getHeight());

                        pin.setVisibility(View.VISIBLE);
                    }
                }
        );

        if (radarView != null) radarView.setDistance(totalDistance);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        } else {
            startTracking();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTracking();
    }

    private void placeBoxes() {
        if (radarView == null) return;
        int radarH = radarView.getHeight();
        float boxHalf = 37.5f * getResources().getDisplayMetrics().density;

        for (int i = 0; i < boxes.length; i++) {
            float ringTopY = DistanceRadarView.getRingTopY(i, radarH);
            boxes[i].setY(ringTopY - boxHalf);
            boxes[i].setVisibility(View.VISIBLE);
        }
    }

    private void syncProgress() {
        for (int boxIndex : getCompletedThisRun()) {
            applyCurrentProgress(boxes[boxIndex]);
            if (radarView != null) { radarView.setSolved(boxIndex); }
        }

        totalDistance = requireContext()
                .getSharedPreferences(getString(R.string.prefProgress), Context.MODE_PRIVATE)
                .getFloat("puzzle15_distance", 0f);
    }


    private void startTracking() {
        locationManager = (LocationManager) requireContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return;
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000, 2f, locationListener);
        } catch (SecurityException e) {
            Log.d("PUZZLE 15", "Location permission error", e);
        }
    }

    private void stopTracking() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (lastLocation != null) {
                totalDistance += lastLocation.distanceTo(location);

                requireContext()
                        .getSharedPreferences(getString(R.string.prefProgress), Context.MODE_PRIVATE)
                        .edit()
                        .putFloat("puzzle15_distance", (float) totalDistance)
                        .apply();

                if (radarView != null) radarView.setDistance(totalDistance);
                checkMilestones();
            }
            lastLocation = location;
        }
    };

    private void checkMilestones() {
        for (int i = 0; i < MILESTONES.length; i++) {
            if (totalDistance >= MILESTONES[i]) {
                updatePuzzle(boxes[i], i);

                if (getCompletedThisRun().contains(i) && radarView != null) {
                    radarView.setSolved(i);
                }
            }
        }

        if (isPuzzleCompletedThisRun()) {
            stopTracking();
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (getActivity() != null && isAdded()) {
                    getActivity().finish();
                }
            }, 1500);
        }
    }
}
