package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;



    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = Color.RED;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();//getHeight/getWidth???

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;//???
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
            postInvalidateDelayed(1000);

        } else {
            drawNumbers(canvas);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
        postInvalidateDelayed(1000);

    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        Paint hourValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        hourValuePaint.setStrokeCap(Paint.Cap.ROUND);
        hourValuePaint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        hourValuePaint.setColor(hoursValuesColor);
        hourValuePaint.setTextSize(100);
        int itime = 12;//长的刻度要显示的数字，这里从12点刻度开始顺时针绘制
        for (int i = 0; i < 60; i++) {///2π圆形分成60份,一秒钟与一分钟,所以要绘制60次,这里是从0到59
            float x1, y1, x2, y2;//刻度的两端的坐标即起始于结束的坐标
            float scale;//每个刻度离圆心的最近端坐标点到圆心的距离
            Double angle = 2 * Math.PI / 60 * i;//当前所占的角度du
            Double sinx = Math.sin(angle);//该角度的sin值
            Double cosy = Math.cos(angle);//该角度的cos值

            Rect textBound = new Rect();//字体被全部包裹的最小的矩形边框
            x1 = (float) (mRadius + mRadius * sinx);//以默认坐标系通过三角函数算出刻度离圆心最远的端点的x轴坐标
            y1 = (float) (mRadius - mRadius * cosy);//以默认坐标系通过三角函数算出刻度离圆心最远的端点的y轴坐标
            if (i % 5 == 0) {//筛选刻度长度
                scale = 5 * mRadius / 6;//长刻度绘制,刻度离圆心的最近端坐标点到圆心的距离,这里取半径的五分之六的长度,可以通过情况来定

                //绘制长刻度上的数字1~12
                String number = itime + "";//当前数字变为String类型
                itime++;//数字加1
                if (itime > 12) {//如果大于数字12,重置为1
                    itime = 1;
                }
                float numScale = 4 * mRadius / 5;//数字离圆心的距离,这里取半径的五分之四的长度,可以通过情况来定
                float x3 = (float) (mRadius + numScale * sinx);//以默认坐标系通过三角函数算出x轴坐标
                float y3 = (float) (mRadius - numScale * cosy);//以默认坐标系通过三角函数算出x轴坐标
                hourValuePaint.getTextBounds(number, 0, number.length(), textBound);//获取每个数字被全部包裹的最小的矩形边框数值

                //绘制数字,通过x3,y3根据文字最小包裹矩形边框数值进行绘制点调整
                canvas.drawText(number, x3 - textBound.width() / 2, y3 + textBound.height() / 2, hourValuePaint);

            } else {
                scale = 9 * mRadius / 10;//短刻度绘制,这里取半径的十分之六九的长度,可以通过情况来定
            }
            x2 = (float) (mRadius + scale * sinx);//以默认坐标系通过三角函数算出该刻度离圆心最近的端点的x轴坐标
            y2 = (float) (mRadius - scale * cosy);//以默认坐标系通过三角函数算出该刻度离圆心最近的端点的y轴坐标
            canvas.drawLine(x1, y1, x2, y2, hourValuePaint);//通过两端点绘制刻度

        }

    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {//针数
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor

        Paint hoursNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint minutesNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint secondsNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //画笔移动到中心
        canvas.translate(mCenterX,mCenterY);
        //画笔设置
        hoursNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);//设置画笔颜色为填充
        hoursNeedlePaint.setStrokeWidth(2*mWidth * DEFAULT_DEGREE_STROKE_WIDTH);//设置画笔宽度
        hoursNeedlePaint.setColor(hoursNeedleColor);

        minutesNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);//设置画笔颜色为填充
        minutesNeedlePaint.setStrokeWidth(2*mWidth * DEFAULT_DEGREE_STROKE_WIDTH);//设置画笔宽度
        minutesNeedlePaint.setColor(minutesNeedleColor);
        minutesNeedlePaint.setAlpha(CUSTOM_ALPHA);

        secondsNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);//设置画笔颜色为填充
        secondsNeedlePaint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);//设置画笔宽度
        secondsNeedlePaint.setColor(secondsNeedleColor);
        secondsNeedlePaint.setAlpha(FULL_ALPHA);

        //获得日历时间
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);// 分
        double hour = calendar.get(Calendar.HOUR) + minute /12*0.2;// 时
        int second = calendar.get(Calendar.SECOND);// 秒

        // 绘制时针/分针/秒针终点位置
        float hourStopX = (float)Math.cos(Math.toRadians(hour*30-90))*mRadius*0.5f;
        float hourStoY = (float)Math.sin(Math.toRadians(hour*30-90))*mRadius*0.5f;
        float minuteStopX = (float)Math.cos(Math.toRadians(minute*6-90))*mRadius*0.8f;
        float minuteStoY = (float)Math.sin(Math.toRadians(minute*6-90))*mRadius*0.8f;
        float secondStopX = (float)Math.cos(Math.toRadians(second*6-90))*mRadius*0.8f;
        float secondStoY = (float)Math.sin(Math.toRadians(second*6-90))*mRadius*0.8f;

        //绘制时/分/秒针
        canvas.drawLine(0,0,hourStopX,hourStoY,hoursNeedlePaint);
        canvas.drawLine(0,0,minuteStopX,minuteStoY,minutesNeedlePaint);
        canvas.drawLine(0,0,secondStopX,secondStoY,secondsNeedlePaint);
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {//中心原点
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);//设置画笔颜色为填充
        //paint.setStrokeCap(Paint.Cap.ROUND);
        pointPaint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);//设置画笔宽度
        pointPaint.setColor(centerInnerColor);
        canvas.drawPoint(mCenterX,mCenterY,pointPaint);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}