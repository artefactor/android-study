package by.academy.lesson4.part2.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Random;

public class CustomView extends View {

    private int mainColor;
    private int color1;
    private int color2;
    private int color3;
    private int color4;
    private final Random random = new Random();

    interface OnCustomViewActionListener {
        void onActionDown(float x, float y, int pressedColor);
    }

    private int centerX;
    private int centerY;

    private int radius;
    private int radiusOuter;

    private OnCustomViewActionListener onCustomViewActionListener;

    private final Paint paint = new Paint();

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        try {
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.CustomView);
            radius = (int) typedArray.getDimension(R.styleable.CustomView_radiusInner, 20);
            radiusOuter = (int) typedArray.getDimension(R.styleable.CustomView_radiusOuter, 100);
            mainColor = typedArray.getColor(R.styleable.CustomView_mainColor, Color.GRAY);
            color1 = typedArray.getColor(R.styleable.CustomView_color1, Color.GREEN);
            color2 = typedArray.getColor(R.styleable.CustomView_color2, Color.RED);
            color3 = typedArray.getColor(R.styleable.CustomView_color3, Color.BLUE);
            color4 = typedArray.getColor(R.styleable.CustomView_color4, Color.YELLOW);

            typedArray.recycle();
        } catch (Exception e) {
            Log.w("init", e);
            radius = 20;
            radiusOuter = 100;
            mainColor = Color.GRAY;
            color1 = Color.GREEN;
            color2 = Color.RED;
            color3 = Color.BLUE;
            color4 = Color.YELLOW;
        }
    }

    public void setOnCustomViewActionListener(OnCustomViewActionListener onCustomViewActionListener) {
        this.onCustomViewActionListener = onCustomViewActionListener;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

//       2)	При нажатии на какой-либо из секторов, он должен поменять цвет.
            float w = x - centerX;
            float h = y - centerY;
            int pressedColor = Color.BLACK;

            boolean inBigCircle = w * w + h * h <= radiusOuter * radiusOuter;
            boolean inSmallCircle = w * w + h * h <= radius * radius;

            boolean inSector1 = inBigCircle && !inSmallCircle && (w >= 0) && (h < 0);
            boolean inSector2 = inBigCircle && !inSmallCircle && (w < 0) && (h < 0);
            boolean inSector3 = inBigCircle && !inSmallCircle && (w < 0) && (h >= 0);
            boolean inSector4 = inBigCircle && !inSmallCircle && (w >= 0) && (h >= 0);

            if (inSector1) {
                color1 = randomColor();
                pressedColor = color1;
                Log.i("color", "color1= " + color1);
                invalidate();
            }

            if (inSector2) {
                color2 = randomColor();
                pressedColor = color2;
                invalidate();
            }

            if (inSector3) {
                color3 = randomColor();
                pressedColor = color3;
                invalidate();
            }

            if (inSector4) {
                color4 = randomColor();
                pressedColor = color4;
                invalidate();
            }


//            3)	При нажатии на круг в центре, все сектора долны сменить свой цвет.
            Log.i("calc", "inCenter= " + inSmallCircle);
            if (inSmallCircle) {
                color1 = randomColor();
                color2 = randomColor();
                color3 = randomColor();
                color4 = randomColor();
                invalidate();
            }

            if (onCustomViewActionListener != null) {
                onCustomViewActionListener.onActionDown(x, y, pressedColor);
            }


            performClick();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        centerX = viewWidth / 2;
        centerY = viewHeight / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculateCoords();
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final RectF oval = new RectF();

        oval.set(centerX - radiusOuter,
                centerY - radiusOuter,
                centerX + radiusOuter,
                centerY + radiusOuter);

        paint.setColor(color1);
        canvas.drawArc(oval, 0, -90, true, paint);

        paint.setColor(color2);
        canvas.drawArc(oval, -90, -90, true, paint);

        paint.setColor(color3);
        canvas.drawArc(oval, -180, -90, true, paint);

        paint.setColor(color4);
        canvas.drawArc(oval, -270, -90, true, paint);

        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);

        paint.setColor(mainColor);
        canvas.drawCircle(centerX, centerY, radius, paint);
        super.onDraw(canvas);
    }


    private void calculateCoords() {
        // TODO @Denis, do we need to calculate 8 coords here or leave calulation of them in OnDraw() method?
    }
}
