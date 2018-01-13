package com.xuxiang.envirmonitor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hsian on 2017/12/7.
 */
public class ChartHintView extends View {
    private Paint mRectPaint;
    private TextPaint mTextPaint;
    private Paint mCirclePaint;
    private RectF mRect;
    private float mTextHeight;
    private float mBaseLineOffset;
    private float mLeading;
    private float mRadius;
    private float mRectWidth;
    private float mRectHeight;
    private String mDate = "NaN";
    private String mTemperature = "NaN";
    private String mHumidity = "NaN";
    private String mVoltage = "NaN";

    public ChartHintView(Context context) {
        super(context);
        initPaint();
    }

    public ChartHintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mRectPaint = new Paint();
        mRectPaint.setColor(0x60000000);
        mRectPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setAntiAlias(true);

        TextPaint.FontMetrics _fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = _fontMetrics.bottom - _fontMetrics.top;
        mLeading = mTextHeight * 0.2f;
        mBaseLineOffset = -_fontMetrics.top;
        mRadius = mTextHeight * 0.25f;
        mRectWidth = mTextPaint.measureText("2017/12/09 21:01:30") + 2 * mRadius;
        mRectHeight = mTextHeight * 4 + mLeading * 5;
        mRect = new RectF(0, 0, mRectWidth, mRectHeight);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
    }

    public float getRectWidth() {
        return mRectWidth;
    }

    public float getRectHeight() {
        return mRectHeight;
    }

    public void setText(String pDate, String pTemperature, String pHumidity, String pVoltage) {
        mDate = pDate.replace('-', '/');
        mTemperature = "温度: " + pTemperature;
        mHumidity = "湿度: " + pHumidity;
        mVoltage = "电压: " + pVoltage;
        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);
        pCanvas.drawRoundRect(mRect, 5, 5, mRectPaint);

        mCirclePaint.setColor(0xffff5252);
        pCanvas.drawCircle(mRadius * 2, mTextHeight * 1.5f + mLeading * 2, mRadius, mCirclePaint);
        mCirclePaint.setColor(0xff26c6da);
        pCanvas.drawCircle(mRadius * 2, mTextHeight * 2.5f + mLeading * 3, mRadius, mCirclePaint);
        mCirclePaint.setColor(0xfffbc02d);
        pCanvas.drawCircle(mRadius * 2, mTextHeight * 3.5f + mLeading * 4, mRadius, mCirclePaint);
        pCanvas.drawText(mDate, mRadius, mLeading + mBaseLineOffset, mTextPaint);
        pCanvas.drawText(mTemperature, mRadius * 4, mLeading * 2 + mTextHeight + mBaseLineOffset, mTextPaint);
        pCanvas.drawText(mHumidity, mRadius * 4, mLeading * 3 + mTextHeight * 2 + mBaseLineOffset, mTextPaint);
        pCanvas.drawText(mVoltage, mRadius * 4, mLeading * 4 + mTextHeight * 3 + mBaseLineOffset, mTextPaint);
    }
}