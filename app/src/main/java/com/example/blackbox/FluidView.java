package com.example.blackbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class FluidView extends View {

    // Normalized gravity direction
    private float gravityDirX = 0f;
    private float gravityDirY = 1f;

    private final Paint fluidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path fluidPath = new Path();

    private static final float FILL_RATIO = 0.7f;
    private static final float BINARY_SEARCH_EPSILON = 0.5f;

    public FluidView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fluidPaint.setColor(ContextCompat.getColor(getContext(), R.color.puzzle1translucent));
    }

    // Update gravity direction (normalized)
    public void setGravity(float gravityX, float gravityY) {
        float magnitude = (float) Math.sqrt(gravityX * gravityX + gravityY * gravityY);
        if (magnitude == 0) return;

        gravityDirX = gravityX / magnitude;
        gravityDirY = -(gravityY / magnitude); // invert Y for screen space

        invalidate();
    }

    // Find where the fluid surface should be along gravity direction
    private float findFluidSurfaceLevel(float width, float height) {
        float targetArea = width * height * FILL_RATIO;

        float minLevel = -width - height;
        float maxLevel = width + height;

        while (maxLevel - minLevel > BINARY_SEARCH_EPSILON) {
            float midLevel = (minLevel + maxLevel) * 0.5f;
            float currentArea = calculatePolygonArea(
                    getClippedFluidPolygon(midLevel, width, height)
            );

            if (currentArea > targetArea) {
                maxLevel = midLevel;
            } else {
                minLevel = midLevel;
            }
        }

        return (minLevel + maxLevel) * 0.5f;
    }

    // Clip the container rectangle with the fluid surface
    private List<float[]> getClippedFluidPolygon(float surfaceLevel, float width, float height) {
        float[][] rectangleCorners = {
                {0, 0}, {width, 0}, {width, height}, {0, height}
        };

        List<float[]> clippedPoints = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            float[] start = rectangleCorners[i];
            float[] end = rectangleCorners[(i + 1) % 4];

            boolean startInside = isBelowSurface(start, surfaceLevel);
            boolean endInside = isBelowSurface(end, surfaceLevel);

            if (startInside && endInside) {
                clippedPoints.add(end);
            } else if (startInside || endInside) {
                clippedPoints.add(findEdgeIntersection(start, end, surfaceLevel));
                if (endInside) clippedPoints.add(end);
            }
        }

        return clippedPoints;
    }

    // Check if a point is inside (below fluid surface)
    private boolean isBelowSurface(float[] point, float surfaceLevel) {
        return gravityDirX * point[0] + gravityDirY * point[1] <= surfaceLevel;
    }

    // Find intersection between rectangle edge and fluid surface
    private float[] findEdgeIntersection(float[] start, float[] end, float surfaceLevel) {
        float startProjection = gravityDirX * start[0] + gravityDirY * start[1] - surfaceLevel;
        float endProjection = gravityDirX * end[0] + gravityDirY * end[1] - surfaceLevel;

        float t = startProjection / (startProjection - endProjection);

        return new float[]{
                start[0] + t * (end[0] - start[0]),
                start[1] + t * (end[1] - start[1])
        };
    }

    // Shoelace formula
    private float calculatePolygonArea(List<float[]> points) {
        float area = 0f;

        for (int i = 0; i < points.size(); i++) {
            float[] current = points.get(i);
            float[] next = points.get((i + 1) % points.size());

            area += current[0] * next[1] - next[0] * current[1];
        }

        return Math.abs(area) * 0.5f;
    }

    // Build drawable path from polygon
    private void buildFluidPath(List<float[]> polygonPoints) {
        fluidPath.reset();
        if (polygonPoints.isEmpty()) return;

        fluidPath.moveTo(polygonPoints.get(0)[0], polygonPoints.get(0)[1]);

        for (int i = 1; i < polygonPoints.size(); i++) {
            fluidPath.lineTo(polygonPoints.get(i)[0], polygonPoints.get(i)[1]);
        }

        fluidPath.close();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float surfaceLevel = findFluidSurfaceLevel(width, height);
        List<float[]> fluidPolygon = getClippedFluidPolygon(surfaceLevel, width, height);

        buildFluidPath(fluidPolygon);
        canvas.drawPath(fluidPath, fluidPaint);
    }
}