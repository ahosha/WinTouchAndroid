package apps.radwin.wintouch.canvasRelated;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import apps.radwin.wintouch.R;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 28/11/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class CircleProgressWidget extends View {

    int width = 340;
    int height = 1200;
    Paint outlinePaint, blackPaint, fillRedPaint, fillWhitePaint, fillGreyPaint;
    Boolean initInitilize = false;
    Boolean changeColorOutlineLighter = false;
    int[] circleStateArray = new int[] {0,0,0,0,0};

    /**
     * construuctor
     *
     * @param context
     */
    public CircleProgressWidget(Context context) {
        super(context);
        this.postInvalidate();
        init();
    }

    /**
     * constructor
     *
     * @param context
     * @param attrs
     */
    public CircleProgressWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.postInvalidate();
    }

    /**
     * constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CircleProgressWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.postInvalidate();
    }

    //////////////////////////////////
    //////////////On Draw//////////////
    //////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (! initInitilize) { // initlizes the init
            init();
        }

        //selta's is the spacing between the circles
        ////////////////////////////
        int deltaX = width/10;
        float circleRadius = height / 9;

        // fills circle - first layer
        ///////////////////////
        for (int i = 0; i < 5; i++) {
            if (circleStateArray[i] == 0) {
                canvas.drawCircle(width / 3.3f + (deltaX*i), height/2, circleRadius, fillGreyPaint);
            } else if (circleStateArray[i] == 1) {
                canvas.drawCircle(width / 3.3f + (deltaX*i), height/2, circleRadius, fillRedPaint);
            } else if (circleStateArray[i] == 2) {
                canvas.drawCircle(width / 3.3f + (deltaX*i), height/2, circleRadius, fillWhitePaint);
            }

        }

        // circle outline - second layer
        ///////////////////////
        canvas.drawCircle(width / 3.3f + (deltaX*0), height/2, circleRadius, outlinePaint);
        canvas.drawCircle(width / 3.3f + (deltaX*1), height/2, circleRadius, outlinePaint);
        canvas.drawCircle(width / 3.3f + (deltaX*2), height/2, circleRadius, outlinePaint);
        canvas.drawCircle(width / 3.3f + (deltaX*3), height/2, circleRadius, outlinePaint);
        canvas.drawCircle(width / 3.3f + (deltaX*4), height/2, circleRadius, outlinePaint);

        // line between the circles - third layer
        ///////////////////////
        canvas.drawLine(width / 3.3f + (deltaX*0)+circleRadius, height/2, width / 3.3f + (deltaX*1)-circleRadius, height/2, outlinePaint);
        canvas.drawLine(width / 3.3f + (deltaX*1)+circleRadius, height/2, width / 3.3f + (deltaX*2)-circleRadius, height/2, outlinePaint);
        canvas.drawLine(width / 3.3f + (deltaX*2)+circleRadius, height/2, width / 3.3f + (deltaX*3)-circleRadius, height/2, outlinePaint);
        canvas.drawLine(width / 3.3f + (deltaX*3)+circleRadius, height/2, width / 3.3f + (deltaX*4)-circleRadius, height/2, outlinePaint);


    }


    private void init() {

//        if (initInitilize) { // initlizes the init
//            return;
//        }

        initInitilize = true;

        if (changeColorOutlineLighter) {
            outlinePaint = new Paint();
            outlinePaint.setColor(getResources().getColor(R.color.alignment_canvasOutline));
            outlinePaint.setStrokeWidth(3f);
            outlinePaint.setStyle(Paint.Style.STROKE);
        } else {
            outlinePaint = new Paint();
            outlinePaint.setColor(getResources().getColor(R.color.alignment_canvasBackground));
            outlinePaint.setStrokeWidth(3f);
            outlinePaint.setStyle(Paint.Style.STROKE);
        }

        fillGreyPaint = new Paint();
        fillGreyPaint.setColor(getResources().getColor(R.color.colorIndicateorSwitchGrey));
        fillGreyPaint.setStrokeWidth(5f);
        fillGreyPaint.setStyle(Paint.Style.FILL);

        fillWhitePaint = new Paint();
        fillWhitePaint.setColor(getResources().getColor(R.color.alignementWhite));
        fillWhitePaint.setStrokeWidth(5f);
        fillWhitePaint.setStyle(Paint.Style.FILL);

        fillRedPaint = new Paint();
        fillRedPaint.setColor(getResources().getColor(R.color.mainAlignementColor));
        fillRedPaint.setStrokeWidth(5f);
        fillRedPaint.setStyle(Paint.Style.FILL);

        //gets teh width and height, initlized only after ondraw happend so it wouldn't be 0 0
        width = getWidth();
        height = getHeight();

    }

    /**
     * changes the indication of the header
     * @param activeIndicator // needs to be in a range between and including 1 - 5 (protected)
     */
    public void changeIndication (int activeIndicator) {

        if (activeIndicator > 0 && activeIndicator<6) {

            // 1 is on // 3 is white // 0 is grey

            // initlizes the array
            ////////////////////////
            for (int i = 0; i < circleStateArray.length; i++) {
                if ((i+1) < activeIndicator) {
                    circleStateArray[i] = 0;
                } else if ((i+1) == activeIndicator) {
                    circleStateArray[i] = 1;
                } else {
                    circleStateArray[i] = 2;
                }

            }

        }

    }

    /**
     * changes the color of the outline
     * @param isChangeColor true if u want to change to a lighter color, or false to a darker color
     */
    public void changeOutlineColor (Boolean isChangeColor) {

        if (isChangeColor) {
            changeColorOutlineLighter = true;
            outlinePaint = new Paint();
            outlinePaint.setColor(getResources().getColor(R.color.alignment_canvasShading_light));
            outlinePaint.setStrokeWidth(3f);
            outlinePaint.setStyle(Paint.Style.STROKE);
        } else {
            changeColorOutlineLighter = false;
            outlinePaint = new Paint();
            outlinePaint.setColor(getResources().getColor(R.color.alignment_canvasBackground));
            outlinePaint.setStrokeWidth(3f);
            outlinePaint.setStyle(Paint.Style.STROKE);
        }

    }

}
