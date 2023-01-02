package com.example.apptracking.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.ColorInt;
import com.example.apptracking.R;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {
    private final int barSizeMargin;
    private final int textMarginVertical;
    private int barWidth;
    private int textDescent;
    private int textHeight;

    private ArrayList<Float> percentList;
    private ArrayList<Float> targetPercentList;
    private ArrayList<String> bottomTextList;
    private ArrayList<String> topTextList;

    private final Paint bottomTextPaint;
    private final Paint topTextPain;
    private final Paint columnPaint;
    private final Paint linePaint;
    private final Rect column;

    private Runnable animator = new Runnable() {
        @Override public void run() {
            boolean needNewFrame = false;
            for (int i = 0; i < targetPercentList.size(); i++) {
                if (percentList.get(i) < targetPercentList.get(i)) {
                    percentList.set(i, percentList.get(i) + 0.02f);
                    needNewFrame = true;
                } else if (percentList.get(i) > targetPercentList.get(i)) {
                    percentList.set(i, percentList.get(i) - 0.02f);
                    needNewFrame = true;
                }
                if (Math.abs(targetPercentList.get(i) - percentList.get(i)) < 0.02f) {
                    percentList.set(i, targetPercentList.get(i));
                }
            }
            if (needNewFrame) {
                postDelayed(this, 20);
            }
            invalidate();
        }
    };

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        columnPaint = new Paint();
        columnPaint.setColor(Color.parseColor("#A3BDF8"));
        column = new Rect();

        barWidth = Utils.dip2px(context, 21);
        barSizeMargin = Utils.dip2px(context, 1);
        textMarginVertical = Utils.dip2px(context, 5);

        bottomTextPaint = new Paint();
        int textBottomSize = Utils.sp2px(context, 12);
        initTextPaint(bottomTextPaint, Color.parseColor("#9B9A9B"), textBottomSize);

        topTextPain = new Paint();
        int textTopSize = Utils.sp2px(context, 8);
        initTextPaint(topTextPain, getResources().getColor(R.color.white), textTopSize);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#9B9A9B"));

        percentList = new ArrayList<>();
        bottomTextList = new ArrayList<>();

        targetPercentList = new ArrayList<>();
        topTextList = new ArrayList<>();

        setBottomTextList();
    }

    private void initTextPaint(Paint paint, @ColorInt int color, int textSize) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setBottomTextList() {
//        this.bottomTextList = new ArrayList<>();
        for (int i = 0 ; i <24 ; i++) {
            this.bottomTextList.add(String.valueOf(i));
        }
        Rect r = new Rect();
        textDescent = 0;
        for (String s : bottomTextList) {
            bottomTextPaint.getTextBounds(s, 0, s.length(), r);
            if (textHeight < r.height()) {
                textHeight = r.height();
            }
            if (barWidth < r.width()) {
                barWidth = r.width();
            }
            if (textDescent < (Math.abs(r.bottom))) {
                textDescent = Math.abs(r.bottom);
            }
        }
        setMinimumWidth(2);
        postInvalidate();
    }

    public void setDataList(List<Float> list, int max, boolean isTime) {
        if (max == 0) max = 1;

        percentList.clear();
        targetPercentList.clear();
        topTextList.clear();

        for (Float data : list) {

            if (data > max) {
                targetPercentList.add(0f);
            } else {
                targetPercentList.add(1 - data / (float) max);
            }
            if (isTime) {
                if (data > max) {
                    topTextList.add(getContext().getString(R.string.usage_time_minus, String.valueOf(max)));
                } else {
                    if (0 < data && data < 1) {
                        int second = (int) ((data * Const.A_MINUS) / 1000);
                        topTextList.add(getContext().getString(R.string.usage_time_second, String.valueOf(second)));
                    } else if (data == 0) {
                        topTextList.add("");
                    } else {
                        topTextList.add(getContext().getString(R.string.usage_time_minus, String.valueOf(Math.round(data))));
                    }
                }
            } else {
                if (data == 0) {
                    topTextList.add("");
                } else {
                    topTextList.add(String.valueOf(Math.round(data)));
                }
            }

        }

        // Make sure percentList.size() == targetPercentList.size()
        if (percentList.isEmpty() || percentList.size() < targetPercentList.size()) {
            int temp = targetPercentList.size() - percentList.size();
            for (int i = 0; i < temp; i++) {
                percentList.add(1f);
            }
        } else if (percentList.size() > targetPercentList.size()) {
            int temp = percentList.size() - targetPercentList.size();
            for (int i = 0; i < temp; i++) {
                percentList.remove(percentList.size() - 1);
            }
        }
        setMinimumWidth(2);
        removeCallbacks(animator);
        post(animator);
    }

    @Override protected void onDraw(Canvas canvas) {

        drawColumn(canvas);
        drawBottomText(canvas);
        drawTopText(canvas);
        drawUnderLineChart(canvas);
    }

    private void drawColumn(Canvas canvas) {
        if (percentList != null && !percentList.isEmpty()) {
            for (int i = 1 ; i <= percentList.size() ; i++) {
                column.set(barSizeMargin * i + barWidth * (i - 1),
                        (textHeight+ textMarginVertical) + (int) ((getHeight() - 2*textHeight - 2*textMarginVertical) * percentList.get(i - 1)),
                        (barSizeMargin + barWidth ) * i,
                        getHeight() - textHeight - textMarginVertical);
                canvas.drawRect(column, columnPaint);
            }
        }
    }

    private void drawBottomText(Canvas canvas) {
        if (bottomTextList != null && !bottomTextList.isEmpty() && !percentList.isEmpty()) {
            int i = 1;
            for (String s : bottomTextList) {
                canvas.drawText(s, barSizeMargin * i + barWidth * (i - 1) + (float)barWidth / 2,
                        getHeight() - textDescent, bottomTextPaint);
                i++;
            }
        }
    }

    private void drawTopText(Canvas canvas) {
        if (topTextList != null && !topTextList.isEmpty()) {
            int i = 1;
            for (String s : topTextList) {
                canvas.drawText(s, barSizeMargin * i + barWidth * (i - 1) + (float)barWidth / 2,
                        (int) ((getHeight() - 2*textHeight - 2*textMarginVertical) * (percentList.get(i - 1))) + textHeight,
                        topTextPain);
                i++;
            }
        }
    }

    private void drawUnderLineChart(Canvas canvas) {
        if (bottomTextList != null && !bottomTextList.isEmpty() && !percentList.isEmpty()) {
            canvas.drawLine(barSizeMargin,
                    getHeight() - textHeight - textMarginVertical,
                    getWidth(),
                    getHeight() - textHeight - textMarginVertical,
                    linePaint);
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec);
        int mViewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    private int measureWidth(int measureSpec) {
        int preferred = 0;
        if (bottomTextList != null) {
            preferred = bottomTextList.size() * (barWidth + barSizeMargin);
        }
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec) {
        int preferred = 222;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred) {
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }
}