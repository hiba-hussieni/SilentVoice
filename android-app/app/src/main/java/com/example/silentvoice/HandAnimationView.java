package com.example.silentvoice;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class HandAnimationView extends View {

    private static final int STATE_OPEN = 0;
    private static final int STATE_HALF = 1;
    private static final int STATE_CLOSED = 2;

    private float thumbValue = 0f;
    private float indexValue = 0f;
    private float middleValue = 0f;
    private float ringValue = 0f;
    private float pinkyValue = 0f;

    private String patternText = "";
    private boolean darkMode = true;

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint palmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wristPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint patternPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint decorationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HandAnimationView(Context context) {
        super(context);
        init();
    }

    public HandAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        darkMode = isSystemDarkMode();

        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeCap(Paint.Cap.ROUND);
        outlinePaint.setStrokeJoin(Paint.Join.ROUND);

        palmPaint.setStyle(Paint.Style.FILL);
        wristPaint.setStyle(Paint.Style.FILL);
        segmentPaint.setStyle(Paint.Style.FILL);

        labelPaint.setTextAlign(Paint.Align.LEFT);
        patternPaint.setTextAlign(Paint.Align.CENTER);

        applyTheme();
    }

    private boolean isSystemDarkMode() {
        int nightMode =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void applyTheme() {
        if (darkMode) {
            backgroundPaint.setColor(Color.parseColor("#10141F"));
            palmPaint.setColor(Color.parseColor("#FFE7C7"));
            wristPaint.setColor(Color.parseColor("#F1CDA8"));
            outlinePaint.setColor(Color.parseColor("#071323"));
            labelPaint.setColor(Color.parseColor("#EAF0FF"));
            patternPaint.setColor(Color.parseColor("#EAF0FF"));
            decorationPaint.setColor(Color.parseColor("#5532D6A1"));
        } else {
            backgroundPaint.setColor(Color.parseColor("#F6F8FC"));
            palmPaint.setColor(Color.parseColor("#FFE7C7"));
            wristPaint.setColor(Color.parseColor("#F1CDA8"));
            outlinePaint.setColor(Color.parseColor("#071323"));
            labelPaint.setColor(Color.parseColor("#111827"));
            patternPaint.setColor(Color.parseColor("#111827"));
            decorationPaint.setColor(Color.parseColor("#5532D6A1"));
        }

        labelPaint.setTextSize(dp(13));
        patternPaint.setTextSize(dp(13));
    }

    // Keep these methods unchanged for compatibility
    public void setDarkMode(boolean enabled) {
        this.darkMode = enabled;
        applyTheme();
        invalidate();
    }

    public void setFingerSensorValues(int thumb, int index, int middle, int ring, int pinky) {
        this.thumbValue = sensorToProgress(thumb);
        this.indexValue = sensorToProgress(index);
        this.middleValue = sensorToProgress(middle);
        this.ringValue = sensorToProgress(ring);
        this.pinkyValue = sensorToProgress(pinky);
        invalidate();
    }

    public void setFingerValues(int thumb, int index, int middle, int ring, int pinky) {
        this.thumbValue = normalizePercent(thumb);
        this.indexValue = normalizePercent(index);
        this.middleValue = normalizePercent(middle);
        this.ringValue = normalizePercent(ring);
        this.pinkyValue = normalizePercent(pinky);
        invalidate();
    }

    public void updateFingers(int thumb, int index, int middle, int ring, int pinky) {
        setFingerSensorValues(thumb, index, middle, ring, pinky);
    }

    public void updateFingers(int thumb, int index, int middle, int ring, int pinky, String pattern) {
        this.patternText = pattern == null ? "" : pattern;
        setFingerSensorValues(thumb, index, middle, ring, pinky);
    }

    private float normalizePercent(int value) {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        return value / 100f;
    }

    private float sensorToProgress(int sensorValue) {
        float progress = sensorValue / 2600f;

        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        return progress;
    }

    private int getState(float progress) {
        if (progress < 0.34f) return STATE_OPEN;
        if (progress < 0.67f) return STATE_HALF;
        return STATE_CLOSED;
    }

    private String getStateText(float progress) {
        int state = getState(progress);

        if (state == STATE_OPEN) return "OPEN";
        if (state == STATE_HALF) return "HALF";
        return "CLOSED";
    }

    private int getStateColor(float progress) {
        int state = getState(progress);

        if (state == STATE_OPEN) return Color.parseColor("#00C875");
        if (state == STATE_HALF) return Color.parseColor("#FFAA00");
        return Color.parseColor("#E63946");
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        applyTheme();

        float w = getWidth();
        float h = getHeight();

        canvas.drawRoundRect(
                new RectF(0, 0, w, h),
                dp(20),
                dp(20),
                backgroundPaint
        );

        drawLegend(canvas, w);
        drawDecorations(canvas, w, h);

        float centerX = w / 2f;

        /*
         * This detects the main page only.
         * Main page view is usually wider than tall.
         * Add page is not affected.
         */
        boolean isMainPageView = w > h;

        /*
         * Main page only:
         * Shift the 4 fingers up a little.
         * Thumb is NOT shifted.
         */
        float mainFingerShiftUp = isMainPageView ? dp(16) : 0f;

        float handTop = h * 0.25f;
        float palmTop = h * 0.52f;

        float basePalmWidth = Math.min(w * 0.36f, h * 0.28f);

        /*
         * Main page only:
         * Make the 4 fingers slightly thicker and adjust the gap.
         * Add page keeps the exact old values.
         */
        float fingerWidth = isMainPageView
                ? basePalmWidth * 0.162f
                : basePalmWidth * 0.145f;

        float fingerGap = isMainPageView
                ? fingerWidth * 0.31f
                : fingerWidth * 0.26f;

        float totalFingersWidth = fingerWidth * 4 + fingerGap * 3;
        float firstX = centerX - totalFingersWidth / 2f;

        /*
         * Palm/square is unchanged in logic.
         * It follows the fingers so the main page stays aligned.
         */
        float palmLeft = firstX + fingerWidth * 0.05f;
        float palmRight = firstX + 3f * (fingerWidth + fingerGap) + fingerWidth * 0.95f;
        float palmWidth = palmRight - palmLeft;
        float palmHeight = palmWidth * 1.08f;

        float outline = Math.max(dp(5), palmWidth * 0.040f);
        outlinePaint.setStrokeWidth(outline);

        float fingerBase = palmTop + dp(10);

        /*
         * Only the 4 fingers are shifted up on main page.
         * Add page gets 0f shift, so it stays unchanged.
         */
        drawFinger(
                canvas,
                firstX,
                fingerBase,
                handTop + dp(34) - mainFingerShiftUp,
                fingerWidth,
                indexValue,
                "Index"
        );

        drawFinger(
                canvas,
                firstX + fingerWidth + fingerGap,
                fingerBase,
                handTop + dp(14) - mainFingerShiftUp,
                fingerWidth,
                middleValue,
                "Middle"
        );

        drawFinger(
                canvas,
                firstX + 2f * (fingerWidth + fingerGap),
                fingerBase,
                handTop + dp(24) - mainFingerShiftUp,
                fingerWidth,
                ringValue,
                "Ring"
        );

        drawFinger(
                canvas,
                firstX + 3f * (fingerWidth + fingerGap),
                fingerBase,
                handTop + dp(50) - mainFingerShiftUp,
                fingerWidth,
                pinkyValue,
                "Pinky"
        );

        /*
         * Thumb is intentionally NOT shifted up.
         */
        float thumbReferenceLeft = firstX - fingerWidth * 0.95f;
        drawThumb(canvas, thumbReferenceLeft, palmTop, palmWidth, palmHeight, thumbValue);

        drawPalmAndWrist(canvas, palmLeft, palmTop, palmRight, palmHeight);

        drawPattern(canvas, centerX, h);
    }

    private void drawLegend(Canvas canvas, float width) {
        float y = dp(28);
        float radius = dp(5);
        float startX = width * 0.23f;
        float gap = width * 0.27f;

        drawLegendItem(canvas, startX, y, radius, "#00C875", "Open");
        drawLegendItem(canvas, startX + gap, y, radius, "#FFAA00", "Half");
        drawLegendItem(canvas, startX + gap * 2f, y, radius, "#E63946", "Closed");
    }

    private void drawLegendItem(Canvas canvas, float x, float y, float radius, String color, String text) {
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.parseColor(color));

        canvas.drawCircle(x, y, radius, dotPaint);

        labelPaint.setTextAlign(Paint.Align.LEFT);
        labelPaint.setTextSize(dp(13));
        canvas.drawText(text, x + dp(12), y + dp(5), labelPaint);
    }

    private void drawDecorations(Canvas canvas, float w, float h) {
        decorationPaint.setStyle(Paint.Style.STROKE);
        decorationPaint.setStrokeWidth(dp(2));

        canvas.drawCircle(w * 0.22f, h * 0.42f, dp(10), decorationPaint);
        canvas.drawCircle(w * 0.78f, h * 0.66f, dp(9), decorationPaint);

        decorationPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w * 0.33f, h * 0.50f, dp(4), decorationPaint);
        canvas.drawCircle(w * 0.68f, h * 0.76f, dp(3), decorationPaint);

        decorationPaint.setStrokeWidth(dp(2));
        decorationPaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(
                w * 0.17f,
                h * 0.76f,
                w * 0.25f,
                h * 0.76f,
                decorationPaint
        );

        canvas.drawLine(
                w * 0.70f,
                h * 0.43f,
                w * 0.78f,
                h * 0.43f,
                decorationPaint
        );
    }

    private void drawFinger(

            Canvas canvas,
            float left,
            float baseY,
            float openTop,
            float fingerWidth,
            float progress,
            String label
    ) {
        openTop += dp(18);
        float closedTop = baseY - (baseY - openTop) * 0.55f;
        float top = lerp(openTop, closedTop, progress);

        RectF fingerRect = new RectF(
                left,
                top,
                left + fingerWidth,
                baseY + dp(18)
        );

        // finger skin
        Paint skinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        skinPaint.setStyle(Paint.Style.FILL);
        skinPaint.setColor(Color.parseColor("#FFE7C7"));

        canvas.drawRoundRect(
                fingerRect,
                fingerWidth / 2f,
                fingerWidth / 2f,
                skinPaint
        );

        canvas.drawRoundRect(
                fingerRect,
                fingerWidth / 2f,
                fingerWidth / 2f,
                outlinePaint
        );

        // colored state indicator inside finger
        Paint statePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statePaint.setStyle(Paint.Style.FILL);
        statePaint.setColor(getStateColor(progress));

        RectF stateBar = new RectF(
                left + fingerWidth * 0.28f,
                top + fingerWidth * 0.55f,
                left + fingerWidth * 0.72f,
                baseY - dp(4)
        );

        canvas.drawRoundRect(
                stateBar,
                fingerWidth * 0.22f,
                fingerWidth * 0.22f,
                statePaint
        );

        // finger joint lines
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#D6A77A"));
        linePaint.setStrokeWidth(dp(2));
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        float height = fingerRect.height();
        float y1 = top + height * 0.36f;
        float y2 = top + height * 0.62f;

        canvas.drawLine(left + dp(4), y1, left + fingerWidth - dp(4), y1, linePaint);
        canvas.drawLine(left + dp(4), y2, left + fingerWidth - dp(4), y2, linePaint);

        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(dp(8));
        canvas.drawText(label, left + fingerWidth / 2f, top - dp(6), labelPaint);
    }
    private void drawThumb(
            Canvas canvas,
            float thumbReferenceLeft,
            float palmTop,
            float palmWidth,
            float palmHeight,
            float progress
    ) {
        float pivotX = thumbReferenceLeft + palmWidth * 0.20f;
        float pivotY = palmTop + palmHeight * 0.64f;

        float openAngle = -132f;
        float halfAngle = -112f;
        float closedAngle = -88f;

        float angle;
        if (progress < 0.5f) {
            angle = lerp(openAngle, halfAngle, progress / 0.5f);
        } else {
            angle = lerp(halfAngle, closedAngle, (progress - 0.5f) / 0.5f);
        }

        float length = lerp(palmWidth * 0.54f, palmWidth * 0.34f, progress);
        float thickness = palmWidth * 0.24f;

        canvas.save();

        canvas.translate(pivotX, pivotY);
        canvas.rotate(angle);

        Paint skinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        skinPaint.setStyle(Paint.Style.FILL);
        skinPaint.setColor(Color.parseColor("#FFE7C7"));

        RectF thumbRect = new RectF(
                -thickness / 2f,
                -thickness / 2f,
                length,
                thickness / 2f
        );

        canvas.drawRoundRect(
                thumbRect,
                thickness / 2f,
                thickness / 2f,
                skinPaint
        );

        canvas.drawRoundRect(
                thumbRect,
                thickness / 2f,
                thickness / 2f,
                outlinePaint
        );

        // colored state indicator
        Paint statePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statePaint.setStyle(Paint.Style.FILL);
        statePaint.setColor(getStateColor(progress));

        RectF stateBar = new RectF(
                length * 0.18f,
                -thickness * 0.22f,
                length * 0.72f,
                thickness * 0.22f
        );

        canvas.drawRoundRect(
                stateBar,
                thickness * 0.20f,
                thickness * 0.20f,
                statePaint
        );

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#D6A77A"));
        linePaint.setStrokeWidth(dp(2));
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawLine(
                length * 0.45f,
                -thickness / 2f + dp(4),
                length * 0.45f,
                thickness / 2f - dp(4),
                linePaint
        );

        canvas.restore();

        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(dp(8));

        canvas.drawText(
                "Thumb",
                pivotX - palmWidth * 0.24f,
                palmTop + palmHeight * 0.50f,
                labelPaint
        );
    }

    private void drawPalmAndWrist(
            Canvas canvas,
            float palmLeft,
            float palmTop,
            float palmRight,
            float palmHeight
    ) {
        float palmBottom = palmTop + palmHeight;
        float palmWidth = palmRight - palmLeft;
        float centerX = (palmLeft + palmRight) / 2f;

        RectF palmRect = new RectF(
                palmLeft - dp(4),
                palmTop + dp(6),
                palmRight + dp(4),
                palmBottom + dp(10)
        );

        float wristWidth = palmWidth * 0.45f;
        float wristHeight = palmHeight * 0.32f;

        RectF wristRect = new RectF(
                centerX - wristWidth / 2f,
                palmBottom + dp(8),
                centerX + wristWidth / 2f,
                palmBottom + dp(8) + wristHeight
        );

        float palmRadius = palmWidth * 0.28f;
        float wristRadius = wristWidth * 0.20f;

        canvas.drawRoundRect(palmRect, palmRadius, palmRadius, palmPaint);
        canvas.drawRoundRect(palmRect, palmRadius, palmRadius, outlinePaint);

        canvas.drawRoundRect(wristRect, wristRadius, wristRadius, wristPaint);
        canvas.drawRoundRect(wristRect, wristRadius, wristRadius, outlinePaint);

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp(2.2f));
        linePaint.setColor(Color.parseColor("#D6A77A"));
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawArc(
                new RectF(
                        palmLeft + dp(10),
                        palmTop + dp(35),
                        palmRight - dp(8),
                        palmBottom - dp(12)
                ),
                205,
                55,
                false,
                linePaint
        );

        canvas.drawArc(
                new RectF(
                        palmLeft + dp(14),
                        palmTop + dp(58),
                        palmRight - dp(12),
                        palmBottom + dp(2)
                ),
                200,
                60,
                false,
                linePaint
        );

        canvas.drawArc(
                new RectF(
                        palmLeft - dp(12),
                        palmTop + dp(45),
                        centerX + dp(20),
                        palmBottom + dp(25)
                ),
                260,
                70,
                false,
                linePaint
        );
    }

    private void drawPattern(Canvas canvas, float centerX, float height) {
        String text = patternText;

        if (text == null || text.trim().isEmpty()) {
            text =
                    getStateText(thumbValue) + "," +
                            getStateText(indexValue) + "," +
                            getStateText(middleValue) + "," +
                            getStateText(ringValue) + "," +
                            getStateText(pinkyValue);
        }

        patternPaint.setTextSize(dp(13));
        patternPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                text,
                centerX,
                height - dp(18),
                patternPaint
        );
    }
}