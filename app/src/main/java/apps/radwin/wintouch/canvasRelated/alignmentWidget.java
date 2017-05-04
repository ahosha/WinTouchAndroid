package apps.radwin.wintouch.canvasRelated;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.screenManagers.AligmentManager;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 19/09/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */
public class alignmentWidget extends View {

    public class alignmentSlice extends Path {
        public alignmentSlice() {
            state = 0;
            elvation = 0;
        }

        public int state;
        public int elvation;

    }

    Paint mainPaintForSlices, secondaryPaintForSlices, outSideCirclePaint, circleOutlinesPaint, fillColorForSlicesNoHbs, fillColorForSlicesWithHbs, firstCirclePaint, highlitedCirclePaint, lowlitedCirclePaint, paintMainNeedle, paintSecondaryNeedle, outOfBoundsPaint;
    alignmentSlice mediumHighlightSlicePath, highHighlightSlicePath, lowHighlightSlicePath, highlightedPath;
    alignmentSlice mediumLowlightSlicePath, highLowlightSlicePath, lowLowlightSlicePath, lowlightedPathInner, lowlightedPathOuter, getOutOfBoundsOuter, getOutOfBoundsInner;
    TextView angleTextView;
    String angleText = "0";
    boolean outOfBoundsOuter = false;
    boolean outOfBoundsInner = false;
    boolean showDegreesText = false;
    boolean canvasRotateInitlize = false;
    //ArrayList<Path> pathsArray = new ArrayList<Path>();
    ArrayList<alignmentSlice> pathsArray = new ArrayList<alignmentSlice>();
    Canvas mainCanvas;
    int width = 340;
    int height = 1200;
    int OUTER_RING_DIAMETER = 600;
    int RINGS_STEP = 200;
    String TAG = "alignmentWidget";
    private long startTime;
    private static final float CIRCLE_LIMIT = 359.9999f;
    private int needleAngle = 257;
    AligmentManager aligmentManagerClass;
    int azimutBeamwidth; // the opening of the spacific angle
    int elevationBeamwidth; // the margnef;lksan

    /**
     * construuctor
     *
     * @param context
     */
    public alignmentWidget(Context context) {
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
    public alignmentWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getWidth();
        height = getHeight();

        while (pathsArray.size() > 0) {
            pathsArray.remove(0);
        }

        init();
    }

    /**
     * constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public alignmentWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    //////////////////////////////////
    //////////////On Draw//////////////
    //////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Log.d(TAG, "onDraw: pathsarray size is: " + pathsArray.size());
        if (pathsArray.size() == 0) { // initlizes the init
            init();
        }

        // draws the first circle
        ////////////////////////////////////////////
        canvas.drawCircle(width / 2f, height / 2f, OUTER_RING_DIAMETER, firstCirclePaint);

        //go through the array and paints the corisponding cells
        ///////////////////////////////////////////
        for (int i = 0; i < pathsArray.size(); i++) {

            if (pathsArray.get(i).state == 1) { // scanned
                canvas.drawPath(pathsArray.get(i), fillColorForSlicesNoHbs);
                canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);

            } else if (pathsArray.get(i).state == 2) { // found hbs
                canvas.drawPath(pathsArray.get(i), fillColorForSlicesWithHbs);
                canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);

            } else {
                canvas.drawPath(pathsArray.get(i), mainPaintForSlices);
                canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);
            }
        }

        //paint the outer circles
        ///////////////////////////////////////////
        canvas.drawCircle(width / 2f, height / 2f, OUTER_RING_DIAMETER, outSideCirclePaint);

        canvas.drawCircle(width / 2f, height / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP)), circleOutlinesPaint);

        canvas.drawCircle(width / 2f, height / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 2)), circleOutlinesPaint);

        canvas.drawCircle(width / 2f, height / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), firstCirclePaint);

        //draws the higlighted circle slice
        ///////////////////////////////////////////
        canvas.drawPath(highlightedPath, highlitedCirclePaint);

        //draws line on top of highlighted circle
        ///////////////////////////////////////////
        for (int i = 0; i < pathsArray.size(); i++) {

            if (pathsArray.get(i).elvation == highlightedPath.elvation) {

                if (pathsArray.get(i).state == 1) { // scanned
                    canvas.drawPath(pathsArray.get(i), fillColorForSlicesNoHbs);
                    canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);

                } else if (pathsArray.get(i).state == 2) { // found hbs
                    canvas.drawPath(pathsArray.get(i), fillColorForSlicesWithHbs);
                    canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);

                } else {
                    canvas.drawPath(pathsArray.get(i), circleOutlinesPaint);
                }

            }
        }

        //draws the dark opacity on top of everything else
        ///////////////////////////////////////////
        canvas.drawPath(lowlightedPathOuter, lowlitedCirclePaint);
        canvas.drawPath(lowlightedPathInner, lowlitedCirclePaint);

        // draw the needle
        // draw the circle of the current elevantion.
        ///////////////////////////////////////////
        alignmentSlice staticNeedle = getSlicesPaths(width / 2f, height / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), OUTER_RING_DIAMETER + 100, 0, 0.6f, 0);
        alignmentSlice mainNeedle = getSlicesPaths(width / 2f, height / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), OUTER_RING_DIAMETER + 100, needleAngle * -1, 0.6f, 0);

        //canvas.drawPath(staticNeedle, paintSecondaryNeedle);
        canvas.drawPath(mainNeedle, paintMainNeedle);

//        //puts a text view
//        ///////////////////////////////////////////
        if (showDegreesText) {
            Paint textPaint = new Paint();
            textPaint.setColor(getResources().getColor(R.color.alignementWhite));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(width / 10);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

            canvas.drawText(angleText, xPos, yPos, textPaint);
        }

        //show out of bounds
        ///////////////////////////////////////////
        if (outOfBoundsOuter) {
            canvas.drawPath(getOutOfBoundsOuter, outOfBoundsPaint);

        } else if (outOfBoundsInner) {
            canvas.drawPath(getOutOfBoundsInner, outOfBoundsPaint);
        }

    }

    /**
     * the function pupose is to color a spacific cell in the array
     *
     * @param sliceNumber  the slice number to draw from 1-36
     * @param elvationCell the elvation, (low, medium, high)
     * @param isFoundHbs   if it's a sector found or an hbs found
     * @return returns true if sussful
     */
    public Boolean changeColor(int sliceNumber, String elvationCell, Boolean isFoundHbs) {

        ///////////////////LOW AND HIGH ARE CHEATED

        ////////Try the functino to see if worked
        ///////////////////////////////////////////
        try {
            if (pathsArray.size() > 0) {

                Log.d(TAG, "changeColor: pathsArray.size(): " + pathsArray.size());

//                sliceNumber = sliceNumber + ((pathsArray.size()/3) / 4);
//
//                if (sliceNumber > pathsArray.size()) {
//                    sliceNumber = sliceNumber - pathsArray.size();
//                }


                if (elvationCell.equals("high")) {
                    if (isFoundHbs) { // if found hbs changes color fill to the designated fill
                        pathsArray.get(sliceNumber).state = 2;
                    } else {
                        pathsArray.get(sliceNumber).state = 1;
                    }

                } else if (elvationCell.equals("middle")) {
                    if (isFoundHbs) { // if found hbs changes color fill to the designated fill
                        pathsArray.get(sliceNumber + (360 / azimutBeamwidth)).state = 2;
                    } else {
                        pathsArray.get(sliceNumber + (360 / azimutBeamwidth)).state = 1;
                    }

                } else if (elvationCell.equals("low")) {
                    if (isFoundHbs) { // if found hbs changes color fill to the designated fill
                        pathsArray.get(sliceNumber + ((360 / azimutBeamwidth) * 2)).state = 2;
                    } else {
                        pathsArray.get(sliceNumber + ((360 / azimutBeamwidth) * 2)).state = 1;
                    }

                }
                invalidate();
            } else {
                return false;
            }
            return true;

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * sets the angle for the niddle and sets the text
     *
     * @param angle angle to set
     */
    public void setAngle(int angle) {

        angle = angle * -1;

        int correctionOffset = 90;

        //sets the angle text
        ///////////////////////////////////////////
        angleText = (angle * -1) + "°";

//        angle = angle + 180 + correctionOffset;
        angle = angle + correctionOffset;

        if (angle > 360) {
            angle = angle - 360;
        }

        if (angle < -360) {
            angle = angle + 360;
        }

        //sets needle angle
        ///////////////////////////////////////////
        needleAngle = angle;
        postInvalidateDelayed(10);

    }

    /**
     * sets the best cell to a spacific cell
     *
     * @param sliceNumber  the slice number
     * @param elvationCell the elvation cell low, middle, high
     */
    public void setBestCell(int sliceNumber, String elvationCell) {
        int elvationInt;

        if (elvationCell.equals("high")) {
            elvationInt = 0;
        } else if (elvationCell.equals("middle")) {
            elvationInt = 1;
        } else {
            elvationInt = 2;
        }


        // adoppts the cell number to the new elevation
        ///////////////////////////////////////////
        if (elvationCell.equals("middle")) {
            sliceNumber = sliceNumber + (360 / azimutBeamwidth);

        } else if (elvationCell.equals("low")) {
            sliceNumber = sliceNumber + ((360 / azimutBeamwidth) * 2);

        }

        // runs on all cells changing the cell state
        ///////////////////////////////////////////
        Log.d(TAG, "setBestCell: pathArray size: " + pathsArray.size());
        for (int i = 0; i < pathsArray.size(); i++) {

            if ((i == sliceNumber) && (pathsArray.get(i).elvation == elvationInt)) {

                pathsArray.get(i).state = 2;
            } else {

                pathsArray.get(i).state = 1;
            }
            invalidate();
        }

    }

    public void setDegreesCheat (Boolean isCheatOn) {
        if (isCheatOn) {
            showDegreesText = true;
            Log.d(TAG, "setDegreesCheat: degrees cheat implemented");
        }
    }


    /**
     * changes elvation highlighted cell
     *
     * @param elvationCell elvation to change high \ middle \ low
     * @return returns true when finishes
     */
    public Boolean changeElvation(String elvationCell, int elevationAngle) {

        if (elvationCell.equals("low")) {
            highlightedPath = lowHighlightSlicePath;

            lowlightedPathOuter = mediumLowlightSlicePath; // initlizes the highlighted as the medium
            lowlightedPathInner = highLowlightSlicePath; // initlizes the highlighted as the medium

        } else if (elvationCell.equals("middle")) {
            highlightedPath = mediumHighlightSlicePath;

            lowlightedPathOuter = lowLowlightSlicePath; // initlizes the highlighted as the medium
            lowlightedPathInner = highLowlightSlicePath; // initlizes the highlighted as the medium

        } else if (elvationCell.equals("high")) {
            highlightedPath = highHighlightSlicePath;
            ;

            lowlightedPathOuter = lowLowlightSlicePath; // initlizes the highlighted as the medium
            lowlightedPathInner = mediumLowlightSlicePath; // initlizes the highlighted as the medium

        }


        if (elevationAngle < (elevationBeamwidth * 1.5) * -1) { // changes the boolean to see if the user is out of bounds
            outOfBoundsInner = true;

        } else if (elevationAngle > (elevationBeamwidth * 1.5)) {
            outOfBoundsOuter = true;

        } else {
            outOfBoundsInner = false;
            outOfBoundsOuter = false;
        }


        return true;

    }

    private void init() {
        if (pathsArray.size() > 0) { // initlizes the init
            return;
        }

        aligmentManagerClass = ((appContext) getContext().getApplicationContext()).getAligmentManagerVar(); // points to the aligment manager
        azimutBeamwidth = aligmentManagerClass.getAzimutBeamwidth();
        elevationBeamwidth = aligmentManagerClass.getElevationBeamwidth();

        //gets teh width and height, initlized only after ondraw happend so it wouldn't be 0 0
        width = getWidth();
        height = getHeight();

        OUTER_RING_DIAMETER = (int) (getWidth() * 0.45);
        RINGS_STEP = height / 14;

        //defining paints
        ///////////////////////////////////////////
        mainPaintForSlices = new Paint();
        mainPaintForSlices.setColor(getResources().getColor(R.color.alignment_canvasBackground));

        secondaryPaintForSlices = new Paint();
        secondaryPaintForSlices.setColor(getResources().getColor(R.color.alignment_canvasOutline));

        outSideCirclePaint = new Paint();
        outSideCirclePaint.setColor(getResources().getColor(R.color.alignment_canvasOutline));
        outSideCirclePaint.setStrokeWidth(40f);
        outSideCirclePaint.setStyle(Paint.Style.STROKE);

        firstCirclePaint = new Paint();
        firstCirclePaint.setColor(getResources().getColor(R.color.alignment_canvasBackground));
        firstCirclePaint.setStrokeWidth(40f);
        firstCirclePaint.setShadowLayer(width / 15, 0, 0, (getResources().getColor(R.color.half_black)));
        firstCirclePaint.setStyle(Paint.Style.FILL);

        highlitedCirclePaint = new Paint();
        highlitedCirclePaint.setColor(getResources().getColor(R.color.alignment_canvasBackground));
        highlitedCirclePaint.setShadowLayer(width / 20, 0, 0, (getResources().getColor(R.color.alignment_canvasOutline)));
        highlitedCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        lowlitedCirclePaint = new Paint();
        lowlitedCirclePaint.setColor(getResources().getColor(R.color.hardBlack));
        lowlitedCirclePaint.setAlpha(125);
        lowlitedCirclePaint.setStyle(Paint.Style.FILL);

        circleOutlinesPaint = new Paint();
        circleOutlinesPaint.setColor(getResources().getColor(R.color.alignment_canvasOutline));
        circleOutlinesPaint.setStrokeWidth(3f);
        circleOutlinesPaint.setStyle(Paint.Style.STROKE);

        paintMainNeedle = new Paint();
        paintMainNeedle.setStrokeWidth(1f);
        paintMainNeedle.setColor(getResources().getColor(R.color.alignment_needle_color));
        paintMainNeedle.setShadowLayer(30, 0, 0, Color.BLACK);
        paintMainNeedle.setStyle(Paint.Style.FILL_AND_STROKE);

        outOfBoundsPaint = new Paint();
        outOfBoundsPaint.setColor(Color.RED);
        outOfBoundsPaint.setShadowLayer(30, 0, 0, Color.RED);
        outOfBoundsPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        paintSecondaryNeedle = new Paint();
        paintSecondaryNeedle.setStrokeWidth(1f);
        paintSecondaryNeedle.setColor(Color.RED);
        paintSecondaryNeedle.setShadowLayer(30, 0, 0, Color.BLACK);
        paintSecondaryNeedle.setStyle(Paint.Style.FILL_AND_STROKE);

        fillColorForSlicesNoHbs = new Paint();
        fillColorForSlicesNoHbs.setColor(getResources().getColor(R.color.alignment_canvasScanned));

        fillColorForSlicesWithHbs = new Paint();
        fillColorForSlicesWithHbs.setColor(getResources().getColor(R.color.alignment_canvasHbs));


        // applyes hardware Acceleration
        ///////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, outSideCirclePaint);
        }

        // gets the main slices paths
        ///////////////////////////////////////////
        for (int i = 0; i < 360; i += azimutBeamwidth) {
            Log.d(TAG, "init: drawing circle at point: " + i);
            pathsArray.add(getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - RINGS_STEP), OUTER_RING_DIAMETER, i + 90, azimutBeamwidth, 0));
        }

        for (int i = 0; i < 360; i += azimutBeamwidth) {
            pathsArray.add(getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 2)), (OUTER_RING_DIAMETER - RINGS_STEP), i + 90, azimutBeamwidth, 1));
        }

        for (int i = 0; i < 360; i += azimutBeamwidth) {
            pathsArray.add(getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), (OUTER_RING_DIAMETER - (RINGS_STEP * 2)), i + 90, azimutBeamwidth, 2));
        }

        // gets the main highlited paths
        ///////////////////////////////////////////
        highHighlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - RINGS_STEP), OUTER_RING_DIAMETER, 20, 360, 0);

        mediumHighlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 2)), (OUTER_RING_DIAMETER - RINGS_STEP), 20, 360, 1);

        lowHighlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), (OUTER_RING_DIAMETER - (RINGS_STEP * 2)), 20, 360, 2);

        highlightedPath = mediumHighlightSlicePath; // initlizes the highlighted as the medium

        //gets all the low lighted (dark & black) paths
        ///////////////////////////////////////////
        highLowlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - RINGS_STEP) - 5, OUTER_RING_DIAMETER + 5, 20, 360, 0);

        mediumLowlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 2)) - 5, (OUTER_RING_DIAMETER - RINGS_STEP) + 5, 20, 360, 1);

        lowLowlightSlicePath = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)) - 5, (OUTER_RING_DIAMETER - (RINGS_STEP * 2)) + 5, 20, 360, 2);

        // initlizes the highlighted as the medium
        ///////////////////////////////////////////
        lowlightedPathOuter = lowLowlightSlicePath;
        lowlightedPathInner = highLowlightSlicePath;


        //gets getout of bounds paths
        ///////////////////////////////////////////
        getOutOfBoundsOuter = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, OUTER_RING_DIAMETER, OUTER_RING_DIAMETER + 10, 0, 360, 0);
        getOutOfBoundsInner = getSlicesPaths(getWidth() / 2f, getHeight() / 2f, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)) - 10, (OUTER_RING_DIAMETER - (RINGS_STEP * 3)), 0, 360, 0);

    }


    /**
     * main function to get the slices paths
     *
     * @param cx         middle X of slice
     * @param cy         middle Y of slice
     * @param rInn       inner radius
     * @param rOut       outer radius
     * @param startAngle start angle of slice
     * @param sweepAngle sweep angle of slice
     * @param elvation   elvation to update the skuce.elvation
     * @return returns alignmentSlice class path
     */
    public alignmentSlice getSlicesPaths(float cx, float cy, float rInn, float rOut, float startAngle, float sweepAngle, int elvation) {

        if (sweepAngle > CIRCLE_LIMIT) {
            sweepAngle = CIRCLE_LIMIT;
        }
        if (sweepAngle < -CIRCLE_LIMIT) {
            sweepAngle = -CIRCLE_LIMIT;
        }

        RectF outerRect = new RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut);
        RectF innerRect = new RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn);

        double start = Math.toRadians(startAngle);
        double end = Math.toRadians(startAngle + sweepAngle);
        float innerStartX = (float) (cx + rInn * Math.cos(start));
        float innerStartY = (float) (cy + rInn * Math.sin(start));
        float innerEndX = (float) (cx + rInn * Math.cos(end));
        float innerEndY = (float) (cy + rInn * Math.sin(end));
        float outerStartX = (float) (cx + rOut * Math.cos(start));
        float outerStartY = (float) (cy + rOut * Math.sin(start));

        alignmentSlice segmentPath = new alignmentSlice();
        segmentPath.moveTo(innerStartX, innerStartY);
        segmentPath.lineTo(outerStartX, outerStartY);
        segmentPath.arcTo(outerRect, startAngle, sweepAngle);
        segmentPath.lineTo(innerEndX, innerEndY);
        segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle); // drawn backwards

        segmentPath.elvation = elvation;

        return segmentPath;

    }
}
