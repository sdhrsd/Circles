package com.herasiddiqui.circles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;


public class CircleView extends View implements View.OnTouchListener{

    private static Handler growHandler = new Handler();
    static Paint blackThick;
    static Paint blackFramed;
    public static List<Circle> allCircles = new ArrayList<>();
    public static List<Circle> movingCircles = new ArrayList<>();
    public static Circle currentCircle = new Circle();
    public static int viewWidth;
    public static int viewHeight;
    static boolean isGrowing;
    static boolean isOverlapping;
    static float cenX;
    static float cenY;
    float radius = 0;
    private GestureDetector gestureDetector;

    static {
        blackThick = new Paint();
        blackThick.setColor(Color.BLACK);
        blackThick.setStrokeWidth(5.0f);

        blackFramed = new Paint();
        blackFramed.setColor(Color.BLACK);
        blackFramed.setStyle(Paint.Style.STROKE);
        blackFramed.setStrokeWidth(4.0f);
    }

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, new GestureListener());
        Log.i("Activity", "Inside the CircleView constructor");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(currentCircle.getX(),currentCircle.getY(),currentCircle.getRadius(), blackFramed);
        for(Circle circle : allCircles) {
        canvas.drawCircle(circle.getX(),circle.getY(),circle.getRadius(),blackFramed);
        }
        if(movingCircles.size() != 0) {
            move();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        Log.i("OnMEASURE","Inside on measure");
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        System.out.println(viewWidth);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(viewWidth,viewHeight);
    }


    @Override
    public boolean onTouch( View v, MotionEvent event) {
        super.onTouchEvent(event);
        Log.i("hs", event.toString());
        logTouchType(event);
        boolean didUseEvent = gestureDetector.onTouchEvent(event);
        Log.i("hs", "gesture did consume " + didUseEvent);
        Log.i("hs", "number of touches; " + event.getPointerCount());
        Log.i("hs", "x; " + event.getX() + " y: " + event.getY());
        for (int k = 1; k < event.getPointerCount();k++ )
            Log.i("hs", "x; " + event.getX(k) + " y: " + event.getY(k));
        return true;
    }

    private void logTouchType(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isCircleOverlapping(event.getX(), event.getY())) {
                    if (allCircles.size() < 15) {
                        Log.i("hs", "down");
                        isGrowing = true;
                        cenX = event.getX();
                        cenY = event.getY();
                        //invalidate();
                        grow();
                    } else {
                        Toast.makeText(getContext(), R.string.limit, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.overlapWarn, Toast.LENGTH_SHORT).show();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("hs", "move " + event.getHistorySize());
                break;
            case MotionEvent.ACTION_UP:
                Log.i("hs", "UP");
                isGrowing = false;
                if (!isOverlapping) {
                    //growHandler.removeCallbacks(Growing);
                    radius = currentCircle.getRadius();
                    allCircles.add(new Circle(cenX, cenY, radius));
                    currentCircle.resetRadius(0);
                    Log.i("HS", "Out Of Grow Function");
                }
                else {
                    isOverlapping = false;
                }
                break;
            default:
                Log.i("hs","other action " + event.getAction());
        }
    }

    public void grow() {
        if(isGrowing) {
            boolean touchedAnother = false;
            currentCircle.setX(cenX);
            currentCircle.setY(cenY);
            for(Circle circle: allCircles) {
                touchedAnother = ((cenX - circle.getX())*(cenX - circle.getX())) + ((cenY - circle.getY())*(cenY - circle.getY())) <((currentCircle.getRadius() + circle.getRadius()) * (currentCircle.getRadius() + circle.getRadius()));
                if(touchedAnother) {
                    //Toast.makeText(getContext(),"Has touched",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if(!touchedAnother && !xIsOutOfBounds(currentCircle) && !yIsOutOfBounds(currentCircle)) {
                currentCircle.setRadius(1);
                growHandler.postDelayed(new Growing(), 50);
                invalidate();
                Log.i("GROW", "In Grow Function");
            }
        }
    }

    public void move() {
        for(Circle circle: movingCircles) {
            circle.setX(circle.getX() + circle.getVelocityX()/100);
            circle.setY(circle.getY() + circle.getVelocityY()/100);
            changeOnCollison(circle);
        }
    }

    public boolean isCircleOverlapping(float pointTouchedX, float pointTouchedY) {
        boolean overlapping = false;
        float distanceX;
        float distanceY;
        for(Circle circle: allCircles) {
            distanceX = pointTouchedX - circle.getX();
            distanceY = pointTouchedY - circle.getY();
            overlapping = (distanceX * distanceX + distanceY * distanceY) <= (circle.getRadius() * circle.getRadius());
            if(overlapping) {
                isOverlapping = true;
                break;
            }
        }
        return overlapping;
    }

    class Growing implements Runnable {
        @Override public void run() {
            grow();
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("FLING", "On Fling Called");
            boolean touched;
            float distanceX;
            float distanceY;
            for(Circle circle: allCircles) {
                distanceX = e1.getX() - circle.getX();
                distanceY = e1.getY() - circle.getY();
                touched = (distanceX * distanceX + distanceY * distanceY) <= (circle.getRadius() * circle.getRadius());
                if(touched) {
                    Log.i("TOUCHED","Circle Touched");
                    circle.setVelocityX(velocityX);
                    circle.setVelocityY(velocityY);
                    movingCircles.add(circle);
                    invalidate();
                    break;
                }
            }
            return true;
        }
    } /*
    Runnable Growing = new Runnable() {
        @Override
        public void run() {
            grow();
        }
    };*/

    private void changeOnCollison(Circle circle) {
            if (xIsOutOfBounds(circle)) {
                circle.setVelocityX(circle.getVelocityX() * -1);
            }
            if (yIsOutOfBounds(circle))
                circle.setVelocityY(circle.getVelocityY()* -1);
    }

    private boolean xIsOutOfBounds(Circle circle) {
            float x = circle.getX();
            if (x-circle.getRadius()<0) return true;
            if (x + circle.getRadius()> viewWidth) return true;
        return false;
    }

    private boolean yIsOutOfBounds(Circle circle) {
        float y = circle.getY();
        if (y-circle.getRadius()<0) return true;
        if (y + circle.getRadius()> viewHeight)
            return true;
        return false;
    }
}



