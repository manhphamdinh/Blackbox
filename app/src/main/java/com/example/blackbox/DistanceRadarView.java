package com.example.blackbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * DistanceRadarView
 *
 * Vẽ các vòng tròn radar đồng tâm và vòng tròn progress.
 * KHÔNG vẽ box – các ImageView box nằm trong XML và được
 * Puzzle15Fragment điều khiển vị trí + visibility.
 *
 * Công thức tọa độ (dùng chung với Fragment để căn box):
 *   cy       = height * 0.80
 *   maxRadius = cy - 160
 *   step      = maxRadius / 4
 *   ringTop[i] = cy - step * (i + 1)   // i = 0..3
 */
public class DistanceRadarView extends View {

    private final Paint paintRing     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int RING_COLOR = 0xFF037DFF;
    private static final float STROKE_RING = 5f;
    private static final float STROKE_SOLVED = 9f;
    private static final float STROKE_PROGRESS = 8f;

    private float progress = 0f;
    private final boolean[] solvedStages = new boolean[4];


    public DistanceRadarView(Context context) {
        super(context); init();
    }
    public DistanceRadarView(Context context, AttributeSet attrs) {
        super(context, attrs); init();
    }
    public DistanceRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init();
    }

    private void init() {
        paintRing.setStyle(Paint.Style.STROKE);

        paintProgress.setColor(Color.WHITE);
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeWidth(STROKE_PROGRESS);
    }

    public void setDistance(double distanceInMeters) {
        if (distanceInMeters <= 1) {
            progress = 0f;
        } else {
            progress = (float) Math.log10(distanceInMeters);
            if (progress > 4f) progress = 4f;
        }
        invalidate();
    }

    public void setSolved(int index) {
        if (index >= 0 && index < 4) {
            solvedStages[index] = true;
            invalidate();
        }
    }

    public static float getRingTopY(int index, int viewHeight) {
        float cy        = viewHeight * 0.80f;
        float maxRadius = cy - 160f;
        float step      = maxRadius / 4f;
        float ringIndex = index + 1;
        return cy - step * ringIndex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx        = getWidth()  / 2f;
        float cy        = getHeight() * 0.80f;
        float maxRadius = cy - 160f;
        float step      = maxRadius / 4f;

        for (int i = 1; i <= 4; i++) {
            int   stageIndex = i - 1;
            float r          = step * i;
            int   alpha      = 255 - (i * 40);

            paintRing.setColor(RING_COLOR);
            paintRing.setAlpha(solvedStages[stageIndex] ? 255 : alpha);
            paintRing.setStrokeWidth(solvedStages[stageIndex] ? STROKE_SOLVED : STROKE_RING);

            canvas.drawCircle(cx, cy, r, paintRing);
        }

        if (progress > 0f) {
            float progressRadius = (progress / 4f) * maxRadius;
            canvas.drawCircle(cx, cy, progressRadius, paintProgress);
        }
    }
}