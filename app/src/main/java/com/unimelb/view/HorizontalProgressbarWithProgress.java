package com.unimelb.view;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.util.AttributeSet;
import android.content.Context;

import com.unimelb.ienv.R;

public class HorizontalProgressbarWithProgress extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = 0xFFC00D1;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor = DEFAULT_COLOR_REACH;
    private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();
    private int mRealWidth;

    public HorizontalProgressbarWithProgress(Context context) {
        this(context,null);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainStyledAttrs(attrs);
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressbarWithProgress);

        mTextSize = (int)ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_size,
                mTextSize);
        mTextColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_color,
                mTextColor);
        mTextOffset = (int)ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_offset,
                mTextOffset);
        mUnReachColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color,
                mUnReachColor);
        mUnReachHeight = (int)ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height,
                mUnReachHeight);
        mReachColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_reach_color,
                mReachColor);
        mReachHeight = (int)ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_reach_height,
                mReachHeight);

        ta.recycle();

        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal, height);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }
        else {
            int textHeight = (int)(mPaint.descent() - mPaint.ascent());
            result = getPaddingTop()
                    + getPaddingBottom()
                    + Math.max(Math.max(mReachHeight, mUnReachHeight),
                        Math.abs(textHeight));
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }

        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight()/2);

        //draw reach bar
        String text = getProgress() + "%";
        int textWidth = (int)mPaint.measureText(text);
        boolean noNeedUnRech = false;
        float radio = getProgress() * 1.0f/getMax();

        float progressX = radio * mRealWidth;
        if(progressX + textWidth > mRealWidth){
            progressX = mRealWidth - textWidth;
            noNeedUnRech = true;
        }


        float endX = progressX - mTextOffset/2;
        if(endX > 0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX, 0, mPaint);
        }

        //draw text
        mPaint.setColor(mTextColor);
        int y = (int)(-(mPaint.descent()+ mPaint.ascent())/2);
        canvas.drawText(text, progressX, y, mPaint);

        // draw unreach bar
        if(!noNeedUnRech){
            float start = progressX + mTextOffset/2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    private int dp2px(int dpValue){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                getResources().getDisplayMetrics());
    }

    private int sp2px(int spValue){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                getResources().getDisplayMetrics());
    }

}
