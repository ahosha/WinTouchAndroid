package apps.radwin.wintouch.canvasRelated;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import apps.radwin.wintouch.R;

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
public class FineAlignmentView extends View {

    private class AlignSector extends Path {
        public int state = 0;
        public int elevation = 0;
        public int index = 0;
        public Integer rssi = -100;
        public boolean isBest = false;
        public float x = 0;
        public float y = 0;
    }

    // the number of elevations. change this to add/remove rows
    private final int MAX_ELEVATIONS = 10;
    private final int MAX_ELEVATION_DEGREES = 15;
    private final int MIN_ELEVATION_DEGREES = -15;

    ////////////////////////////////////////////////////////////////////////////////////////
    // paint objects
    private Paint paintNeedle, paintShadow, paintSideBorder, paintBest, paintNoSync, paintHighlight, paintText, linePaint, secondaryPaintForSlices, outSideCirclePaint, circleOutlinesPaint;

    ArrayList<AlignSector> pathsArray = new ArrayList<>();

    int width = 0;
    int height = 0;
    int currentCell = -1;
    int MaxRss = 0;
    int CurrentRss = 0;

    String hbsLinkState = "";

    // possible values - 15 to -15. outside range will show red indication
    private int currentElevationDegrees = 15;

    // the index of the elevation row
    private int currentElevationIndex = -1;

    // the device Gyro angle of the center angle
    private int centerNeedleAngle = 0;

    private boolean displayLinkStateCheat = false;

    // the angle of the needle
    private int needleAngle = 257;
    private final int screenCenterAngle = 270;

    private int max_rssi = -200;

    private String TAG = FineAlignmentView.class.getSimpleName();


    public FineAlignmentView(Context context) {
        super(context);
        this.postInvalidate();
    }

    public FineAlignmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.postInvalidate();
    }

    public FineAlignmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        width = getWidth();
        height = getHeight();

        while (pathsArray.size()>0)
        {
            pathsArray.remove(0);
        }

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = getHeight();

        // nothing to draw, add slices only once
        if (pathsArray.size() == 0)
            init();

        ////////////////////////////////////////////////////////////////////////////////////////
        // draw all elements
        for (int i = 0; i < pathsArray.size(); i++) {
            AlignSector p = pathsArray.get(i);
            if (p.isBest) {
                //Log.d(TAG, "onDraw: paints the best Hbs: position: "+i);
                canvas.drawPath(p, paintBest);

            } else if (p.state == 1) {
                canvas.drawPath(p, linePaint);
                canvas.drawPath(p, circleOutlinesPaint);
            } else {
                canvas.drawPath(p, circleOutlinesPaint);
            }

            if (p.rssi != -100) {
//                canvas.drawText(p.rssi.toString(), p.x, p.y, paintText);
                setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Required for API level 11 or higher.
            }
        }


        ////////////////////////////////////////////////////////////////////////////////////////
        // draw the top most circle
        int step = (int) (getHeight() * 0.05);
        int h = step * MAX_ELEVATIONS;
        canvas.drawCircle(width - (width / 2), height + h, width + step + (step * MAX_ELEVATIONS), outSideCirclePaint);

        ////////////////////////////////////////////////////////////////////////////////////////
        // draw the large slice of the right/left borders.
        AlignSector as = getSegments(width - (width / 2), height + h, width + step, width + step + (step * MAX_ELEVATIONS), 257, 25);
        canvas.drawPath(as, paintSideBorder);

        ////////////////////////////////////////////////////////////////////////////////////////
        // draw the circle of the current elevation.
        if (currentElevationIndex != -1) {
            /// @@@
//                AlignSector as1 = getSegments(width - (width / 2), height + h, width + step + (step * currentElevationIndex), width + step + (step * (currentElevationIndex + 1)), 257, 25);
//                canvas.drawPath(as1, paintShadow);

            ////////////////////////////////////////////////////////////////////////////////////////
            // draw the slices of the selected elevation
            for (int i = 0; i < pathsArray.size(); i++) {
                AlignSector p = pathsArray.get(i);
                if (p.elevation == currentElevationIndex) {

                    if (p.isBest) {
                        canvas.drawPath(p, paintBest);
                    } else if (p.state == 1) {
                        canvas.drawPath(p, linePaint);
                        canvas.drawPath(p, circleOutlinesPaint);
                    } else {
                        canvas.drawPath(p, circleOutlinesPaint);
                    }
                }
                if (p.rssi != -100) {
//                    canvas.drawText(p.rssi.toString(), p.x, p.y, paintText);
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Required for API level 11 or higher.
                }

            }
        }
        if (currentCell != -1) {
            AlignSector current = pathsArray.get(currentCell);
            canvas.drawPath(current, paintHighlight);
//            canvas.drawText(current.rssi.toString(), current.x, current.y, paintText);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Required for API level 11 or higher.

        }
        ////////////////////////////////////////////////////////////////////////////////////////
        // draw the needle
        AlignSector asNeedle = getSegments(width - (width / 2), height + h, width + step, width + step + (step * (MAX_ELEVATIONS + 1)), getAdjustedAngle(), 0.2f);
        canvas.drawPath(asNeedle, paintNeedle);

        ////////////////////////////////////////////////////////////////////////////////////////
        // draw too-low indication
        if (currentElevationDegrees < MIN_ELEVATION_DEGREES) {
            AlignSector tooLow = getSegments(width - (width / 2), height + h, width + step - 20, width + step, 242, 305);
            canvas.drawPath(tooLow, paintNeedle);
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        // draw too-high indication
        if (currentElevationDegrees > MAX_ELEVATION_DEGREES) {
            AlignSector tooLow = getSegments(width - (width / 2), height + h, width + step + (step * (MAX_ELEVATIONS)), width + step + (step * MAX_ELEVATIONS + 20), 242, 305);
            canvas.drawPath(tooLow, paintNeedle);
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        // draw the left and right borders
        AlignSector borderLeft = getSegments(width - (width / 2), height + h, width + step, width + step + (step * (MAX_ELEVATIONS + 1)), screenCenterAngle - 13, 0.5f);
        AlignSector borderRight = getSegments(width - (width / 2), height + h, width + step, width + step + (step * (MAX_ELEVATIONS + 1)), screenCenterAngle + 12, 0.5f);

        if (getAdjustedAngle() < (screenCenterAngle - 13)) {
            canvas.drawPath(borderLeft, paintNoSync);
        } else if (getAdjustedAngle() > (screenCenterAngle + 12)) {
            canvas.drawPath(borderRight, paintNoSync);
        }


        ///////////////////////////////////////////
        //puts RSS headline
        Paint textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.alignementWhite));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(width / 16);

        if (displayLinkStateCheat) {

            ///////////////////////////////////////////
            //puts a text view
            int xPos = (canvas.getWidth() / 2);
            int yPos = canvas.getHeight() - 150;

            if (currentCell != -1) {
                AlignSector current = pathsArray.get(currentCell);

                // make sure we don't show the default -1000 value
//                if (MaxRss>-100) {
                    canvas.drawText("Link State: " + hbsLinkState, xPos, yPos, textPaint);
//                }
            }

        }


        ///////////////////////////////////////////
        //puts a text view
        int xPos = (canvas.getWidth() / 2);
        int yPos = canvas.getHeight() - 40;

        if (currentCell != -1) {
            AlignSector current = pathsArray.get(currentCell);

            // make sure we don't show the default -1000 value
            if (MaxRss>-100) {
                canvas.drawText("Best: " + MaxRss + " Current: "+ CurrentRss, xPos, yPos, textPaint);
            }
        }


    }

    private void init() {

        //defining paints
        secondaryPaintForSlices = new Paint();
        secondaryPaintForSlices.setColor(getResources().getColor(R.color.alignment_canvasOutline));

        outSideCirclePaint = new Paint();
        outSideCirclePaint.setColor(getResources().getColor(R.color.alignment_canvasOutline));
        outSideCirclePaint.setStrokeWidth((int) height / 50);
        outSideCirclePaint.setStyle(Paint.Style.STROKE);

        paintSideBorder = new Paint();
        paintSideBorder.setColor(getResources().getColor(R.color.alignment_canvasOutline));
        paintSideBorder.setStrokeWidth(12);
        paintSideBorder.setStyle(Paint.Style.STROKE);

        paintText = new Paint();
        paintText.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        paintText.setColor(ContextCompat.getColor(getContext(), R.color.alignementWhite));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(width / 25);

        paintBest = new Paint();
        paintBest.setStrokeWidth(1f);
        paintBest.setColor(ContextCompat.getColor(getContext(), R.color.alignment_canvasHbs));
        paintBest.setShadowLayer(30, 0, 0, Color.BLUE);
        paintBest.setStyle(Paint.Style.FILL);

        paintNoSync = new Paint();
        paintNoSync.setStrokeWidth(1f);
        paintNoSync.setColor(ContextCompat.getColor(getContext(), R.color.alignment_noSync));
        paintNoSync.setShadowLayer(30, 0, 0, Color.RED);
        paintNoSync.setStyle(Paint.Style.FILL);

        paintHighlight = new Paint();
        paintHighlight.setStrokeWidth(1f);
        paintHighlight.setColor(ContextCompat.getColor(getContext(), R.color.highlightAlignementColor));
        paintHighlight.setShadowLayer(100, 0, 0, Color.BLACK);
        paintHighlight.setStyle(Paint.Style.FILL);

        paintNeedle = new Paint();
        paintNeedle.setStrokeWidth(1f);
        paintNeedle.setColor(Color.WHITE);
        paintNeedle.setShadowLayer(30, 0, 0, Color.BLACK);
        paintNeedle.setStyle(Paint.Style.FILL);

        paintShadow = new Paint();
        paintShadow.setColor(ContextCompat.getColor(getContext(), R.color.alignment_canvasBackground));
        paintShadow.setStyle(Paint.Style.FILL);
        paintShadow.setStrokeWidth(40f);
        paintShadow.setShadowLayer(width / 15, 0, 0, Color.argb(0xff, 0x80, 0x80, 0x80));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // applyes hardware Acceleration
            setLayerType(LAYER_TYPE_SOFTWARE, paintShadow);
        }

        circleOutlinesPaint = new Paint();
        circleOutlinesPaint.setColor(ContextCompat.getColor(getContext(), R.color.alignment_canvasOutline));

        circleOutlinesPaint.setStrokeWidth(3f);
        circleOutlinesPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE); // Set the color
        linePaint.setStyle(Paint.Style.FILL); // set the border and fills the inside of needle
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(1.0f); // width of the border
        linePaint.setShadowLayer(8.0f, 0.1f, 0.1f, Color.GRAY); // Shadow of the needle

        int step = (int) (getHeight() * 0.05);
        int h = step * MAX_ELEVATIONS;

        // create all sectors and add to the path array
        for (int j = 0; j < MAX_ELEVATIONS; j++) {
            for (int i = 242; i < 305; i += 5) {
                AlignSector as = getSegments(width - (width / 2), height + h, width + step + (step * j), width + step + (step * (j + 1)), i, 5);
                as.elevation = j;
                as.index = (i - 242) / 5;
                pathsArray.add(as);
            }
        }
    }


    // That's is the Gyro's angle we place as the center of the screen
    public void setCenterAngle(int angle) {
        centerNeedleAngle = angle;
    }

    public void setLinkStateCheat (Boolean isCheatOn) {
        if (isCheatOn) {
            displayLinkStateCheat = true;
        }
    }

    // return the adjust angle with the screen offset
    public int getAdjustedAngle() {
        return needleAngle - centerNeedleAngle + screenCenterAngle;
    }

    public void setNeedleAngle(int angle) {
        needleAngle = angle;
    }

    public void setSelectedElevationIndex(int elevation) {
        if (elevation < 0 || elevation >= MAX_ELEVATIONS)
            currentElevationIndex = -1;
        else
            currentElevationIndex = elevation;

    }

    /**
     * @param elevation
     * @param angle
     * @param rssi
     * @param linkState
     * @param maxRss
     */
    public void setCellInfo(int elevation, int angle, int rssi, String linkState, int maxRss)

    {

        Log.d(TAG, "setCellInfo: setting cell with: " + rssi + "and link state: " + linkState + " elevation: " + elevation+" angle: "+angle);

        // no elements
        if (pathsArray.size() == 0) {
            return;
        }

        // sets the link state
        hbsLinkState = linkState;

        // set elevation even if out of range
        currentElevationDegrees = elevation;

        // make sure in range
        if ((elevation < MIN_ELEVATION_DEGREES) || (elevation > MAX_ELEVATION_DEGREES)) {
            // hide selected elevation
            setSelectedElevationIndex(-1);
        } else {
            // from -15 bring to scale 0-30
            elevation += 15;

            // 3 degrees per row
            int elevationIndex = (int) elevation / 3;

            setSelectedElevationIndex(elevationIndex);
        }

        setNeedleAngle(angle);

        // get distance from the center
        float c = angle - centerNeedleAngle + 0.5f;
        int distance = (int) Math.round(c / 5);
        distance += 5;

        for (int i = 0; i < pathsArray.size(); i++) {
            AlignSector as = pathsArray.get(i);
            if ((as.elevation == currentElevationIndex)) {
                if (as.index == distance) {
//                    if (rssi > as.rssi)
                    as.rssi = rssi;
                    if (linkState != null) {
                        as.state = 1; // changes the cell state
                    }
                    currentCell = i;
                    break;
                }

            }
        }

        // update max RSSI only if on a valid elevation
        if (currentElevationIndex != -1) {
            updateMaxRssi(rssi);
        }

        postInvalidateDelayed(10);

        MaxRss = maxRss;

    }

    private void updateMaxRssi(int rssi) {

        if (rssi == 0) {
            return;
        }

        //updates the current Rss
        CurrentRss = rssi;

        //Log.d(TAG, "updateMaxRssi: updates rssi with: "+rssi+" max is: "+max_rssi);
        if (rssi < max_rssi) {
            return;
        }

        if (pathsArray.size() == 0) {
            return;
        }

        max_rssi = rssi;

        Boolean isSetTrueIntiated = false;

        for (int i = 0; i < pathsArray.size(); i++) {
            AlignSector as = pathsArray.get(i);

            //Log.d(TAG, "updateMaxRssi: checking rss: "+rssi);
            as.isBest = false;

            if (as.rssi == rssi) {
                if (isSetTrueIntiated == false) {
                    Log.d(TAG, "updateMaxRssi: setting best with: " + rssi + " cell number : " + i);
                    as.isBest = true;
                    isSetTrueIntiated = true;
                }

            }
        }

    }

    private AlignSector getSegments(float cx, float cy, float rInn, float rOut, float startAngle, float sweepAngle) {

        final float CIRCLE_LIMIT = 359.9999f;

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
        float outerEndX = (float) (cx + rOut * Math.cos(end));
        float outerEndY = (float) (cy + rOut * Math.sin(end));

        AlignSector segmentPath = new AlignSector();
        segmentPath.moveTo(innerStartX, innerStartY);
        segmentPath.lineTo(outerStartX, outerStartY);
        segmentPath.arcTo(outerRect, startAngle, sweepAngle);

        // Path currently at outerEndX,outerEndY
        segmentPath.lineTo(innerEndX, innerEndY);
        segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle); // drawn backwards
        segmentPath.moveTo(innerStartX, innerStartY);
        segmentPath.x = (innerEndX - innerStartX) / 2 + innerStartX;
        segmentPath.y = innerStartY - 5;
        return segmentPath;
    }


}
