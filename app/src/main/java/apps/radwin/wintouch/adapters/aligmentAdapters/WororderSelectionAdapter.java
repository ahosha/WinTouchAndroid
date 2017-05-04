package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;
import apps.radwin.wintouch.models.WorkingOrdersModel;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 14/09/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */
public class WororderSelectionAdapter extends ArrayAdapter<WorkingOrdersModel> {

    Context _context;
    View transperentView;

    public final String TAG = WororderSelectionAdapter.class.getSimpleName();

    public static HashMap<String, Drawable> mapWorkOrderToImage = new HashMap<String, Drawable>();

    static class ViewHolder {
        ImageView workorderImageView;
        ImageView nextArrow;
        TextView workorderName;
        TextView workorderDistance;
        TextView workorderTime;
        TextView nearestSiteText;
        ImageView ivBack;
        View transperentView;
    }

    public WororderSelectionAdapter(Context context, List<WorkingOrdersModel> workorderModel) {
        super(context, 0, workorderModel);

        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        WorkingOrdersModel selectedWorkorder = getItem(position);

        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_workorder_selection, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.workorderImageView = (ImageView) convertView.findViewById(R.id.workoderSelectionListImageView);
            viewHolder.workorderName = (TextView) convertView.findViewById(R.id.workoderSelectionListWorkorderName);
            viewHolder.workorderDistance = (TextView) convertView.findViewById(R.id.workoderSelectionListWorkorderDistance);
            viewHolder.workorderTime = (TextView) convertView.findViewById(R.id.workorderSelectionListDateText);
            viewHolder.nearestSiteText = (TextView) convertView.findViewById(R.id.workorderListNearestSite);
            viewHolder.nextArrow = (ImageView) convertView.findViewById(R.id.workorderNextArrow);
            transperentView = (View) convertView.findViewById(R.id.trasnparentBackground);

            transperentView.setVisibility(View.INVISIBLE);

            viewHolder.nearestSiteText.setVisibility(View.INVISIBLE);
            viewHolder.nextArrow.setVisibility(View.INVISIBLE);
            viewHolder.ivBack= (ImageView) convertView.findViewById(R.id.grad_background);
            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if ((selectedWorkorder.isSelectedInList != null) && (selectedWorkorder.isSelectedInList)) {
//            transperentView.setVisibility(View.VISIBLE);
//        }

        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.workorderImageView.getLayoutParams();

        int dimensionInPixel = 80;

        Drawable imgStreetview = null;
        String key = selectedWorkorder.workordertId + "~" + selectedWorkorder.workingOrderAdress;
        if (mapWorkOrderToImage.containsKey(key) == false) {

            try {
                String lng = String.valueOf(selectedWorkorder.orderLongitude);
                String lat = String.valueOf(selectedWorkorder.orderLatitude);
                String url = "https://maps.googleapis.com/maps/api/streetview?size=400x200&location=" + lat + "," + lng;

                new LoadStreetviewImageTask().execute(url, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imgStreetview = mapWorkOrderToImage.get(key);
        }


        if (position == 0) {

            if (imgStreetview != null)
                convertView.setBackground(imgStreetview);
            else
                convertView.setBackgroundResource(R.drawable.streetview_new);
            int backH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getContext().getResources().getDisplayMetrics());
            convertView.getLayoutParams().height = backH;
            convertView.requestLayout();
            viewHolder.workorderName.setTextColor(Color.WHITE);
            viewHolder.workorderDistance.setTextColor(Color.WHITE);
            dimensionInPixel = 50;
            viewHolder.workorderName.setTextSize(33);

            viewHolder.nearestSiteText.setVisibility(View.VISIBLE);
            viewHolder.nextArrow.setVisibility(View.VISIBLE);

            int rightConvertedMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getContext().getResources().getDisplayMetrics());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) viewHolder.workorderName.getLayoutParams();
            mlp.setMargins(0, 10, rightConvertedMargin, 0); //(left, top, right, bottom);

            int leftConvertedMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getContext().getResources().getDisplayMetrics());

            ViewGroup.MarginLayoutParams viewGroupNearestSite = (ViewGroup.MarginLayoutParams) viewHolder.nearestSiteText.getLayoutParams();
            viewGroupNearestSite.setMargins(leftConvertedMargin, 70, 0, 0); //(left, top, right, bottom);


            viewHolder.ivBack.setVisibility(View.VISIBLE);



        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            int backH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getContext().getResources().getDisplayMetrics());
            int leftConvertedMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getContext().getResources().getDisplayMetrics());
            int rightConvertedMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getContext().getResources().getDisplayMetrics());

            convertView.getLayoutParams().height = backH;
            convertView.requestLayout();
            viewHolder.workorderName.setTextColor(Color.BLACK);
            viewHolder.workorderDistance.setTextColor(Color.rgb(0x7d, 0x92, 0xa3));

            dimensionInPixel = 50;
            viewHolder.workorderName.setTextSize(18);

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) viewHolder.workorderName.getLayoutParams();
            mlp.setMargins(leftConvertedMargin, 0, rightConvertedMargin, 30); //(left, top, right, bottom);

            viewHolder.nearestSiteText.setVisibility(View.INVISIBLE);
            viewHolder.nextArrow.setVisibility(View.INVISIBLE);

            ViewGroup.MarginLayoutParams viewGroupNearestSite = (ViewGroup.MarginLayoutParams) viewHolder.nearestSiteText.getLayoutParams();
            viewGroupNearestSite.setMargins(15, 0, 0, 0); //(left, top, right, bottom);
            viewHolder.ivBack.setVisibility(View.GONE);
        }
        int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, getContext().getResources().getDisplayMetrics());
        viewHolder.workorderImageView.getLayoutParams().height = dimensionInDp;
        viewHolder.workorderImageView.getLayoutParams().width = dimensionInDp;


        //////////////////////////////
        ///////////SET IMAGE//////////
        //////////////////////////////

        if (selectedWorkorder.lastUpdateStatus.equals("inComplete")) {

            viewHolder.workorderImageView.setImageResource(R.drawable.dashboard_inprograss_icon);

            //shows a different icon in the first workorder
            if (position == 0) {
//                viewHolder.workorderImageView.setImageResource(R.drawable.ic_incomplete_white);
            }

        } else if (selectedWorkorder.lastUpdateStatus.equals("complete")) {
            viewHolder.workorderImageView.setImageResource(R.drawable.dashboard_completed_icon);

            try {
                if ((selectedWorkorder.downLinkResult < selectedWorkorder.truePutDown) ||
                        (selectedWorkorder.upLinkResult < selectedWorkorder.truePutUp)) {
                    viewHolder.workorderImageView.setImageResource(R.drawable.workorder_selection_complete_fail);
                }
            } catch (Exception e) {
            }
        } else {
            viewHolder.workorderImageView.setImageResource(R.drawable.dashboard_complet_empty);

            //shows a different icon in the first workorder
            if (position == 0) {
//                viewHolder.workorderImageView.setImageResource(R.drawable.ic_planed_white);
            }

        }


        try { // try to put the workorder name
            viewHolder.workorderName.setText(selectedWorkorder.workingOrderName);

            // FIRST ITEM ONLY
            if (position==0) {
                if (selectedWorkorder.workingOrderName.length() > 20) { // try to limit project name
                    viewHolder.workorderName.setText(selectedWorkorder.workingOrderName.substring(0, 20) + "...");
                }
            }
            else {
                if (selectedWorkorder.workingOrderName.length() > 47) { // try to limit project name
                    viewHolder.workorderName.setText(selectedWorkorder.workingOrderName.substring(0, 47) + "...");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try { // set days passed
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String[] sep = selectedWorkorder.lastUpdateTime.split("_");
            Date date1 = sdf.parse(sep[0]);
            Date date2 = sdf.parse(sdf.format(new Date()));
            long diff = date2.getTime() - date1.getTime();
            int daysPassed = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

            if (daysPassed <= 0) {
                viewHolder.workorderTime.setText("Today");
            } else {
                viewHolder.workorderTime.setText(daysPassed + "d");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Distance calculation. If distance not set in order or GPS not enabled - "0 km" will be present as a default value
        String distanceStr = "0 km";

        try {
            if (selectedWorkorder.orderLatitude != 0 && selectedWorkorder.orderLongitude != 0) {
                GeoLocationWrapper geoLocationWrapper = ((appContext) _context.getApplicationContext()).getGeoLocationWrapper();

                GeoLocation currentLocation = geoLocationWrapper.GetGeoLocation();
                if (currentLocation != null) {

                    if (currentLocation.isAllZero())
                    {
                        viewHolder.workorderDistance.setVisibility(View.GONE);
                    }
                    else {
                        GeoLocation elementLocation = new GeoLocation(selectedWorkorder.orderLatitude, selectedWorkorder.orderLongitude);

                        double distance = GeoLocationWrapper.calculateDistance(currentLocation, elementLocation);

                        if (distance > 1000) {
                            distanceStr = String.valueOf(Math.round (distance / 1000)) + " km";
                        } else {
                            distanceStr = String.valueOf(Math.round(distance)) + " m";
                        }

                        if (distance > 100000) {
                            // why?
                            Log.i("GPS", "too far?");
                        }
                        viewHolder.workorderDistance.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                viewHolder.workorderDistance.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            viewHolder.workorderDistance.setVisibility(View.GONE);
        }

        // End of Distance calculation

        viewHolder.workorderDistance.setText(distanceStr);
        return convertView;

    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static Date updateTime = new Date();
    private static GeoLocation geoLocation = new GeoLocation(0,0);

    public GeoLocation getCachedCurrentLocation()
    {
        Boolean bContinue = true;
        Date dateNow = new Date();
        try
        {
            dateNow = dateFormat.parse(dateFormat.format(new Date()));
            long diff = dateNow.getTime() - updateTime.getTime();
            int secsPassed = (int) (TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS));
            bContinue = ((secsPassed > 60) || geoLocation.isAllZero());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.i(TAG, "NOT Updating Location...");

        // return cached value
        if (!bContinue) {
            try {
                GeoLocationWrapper geoLocationWrapper = ((appContext) getContext().getApplicationContext()).getGeoLocationWrapper();
                geoLocationWrapper.DeInit();
            }
            catch (Exception e) {}

            return geoLocation;
        }

        updateTime = dateNow;
        Log.i(TAG, "Updating Location...");


        try {
            GeoLocationWrapper geoLocationWrapper = ((appContext) getContext().getApplicationContext()).getGeoLocationWrapper();
            geoLocationWrapper.Init();
            GeoLocation currentLocation = geoLocationWrapper.GetGeoLocation();
            if (currentLocation != null) {

                if (!currentLocation.isAllZero())
                {
                    WororderSelectionAdapter.geoLocation = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                }
            }
        }
        catch (Exception e)
        {

        }
        return geoLocation;
    }
    public void sort() {
        super.sort(new Comparator<WorkingOrdersModel>() {
            @Override
            public int compare(WorkingOrdersModel item1, WorkingOrdersModel item2) {
                try {
                    GeoLocation g1 = new GeoLocation(0, 0);
                    GeoLocation g2 = new GeoLocation(0, 0);

                    if (item1 != null)
                        g1 = new GeoLocation(item1.orderLatitude, item1.orderLongitude);

                    if (item2 != null)
                        g2 = new GeoLocation(item2.orderLatitude, item2.orderLongitude);

                    GeoLocation current = getCachedCurrentLocation();
                    if (current.isAllZero())
                        return 0;

                    double distance1 = GeoLocationWrapper.calculateDistance(current, g1);
                    double distance2 = GeoLocationWrapper.calculateDistance(current, g2);

                    if (distance1 > distance2)
                        return 1;
                    if (distance1 < distance2)
                        return -1;
                    return 0;
                }
                catch (Exception e)
                {
                    return 0;
                }
            }
        });

        notifyDataSetChanged();
    }

    private class LoadStreetviewImageTask extends AsyncTask<String, Void, String> {

        Drawable drawable = null;
        String id = "";

        @Override
        protected String doInBackground(String... params) {

            try {
                String url = params[0];
                id = params[1];
                InputStream is = (InputStream) new URL(url).getContent();
                drawable = Drawable.createFromStream(is, "src name");

                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                int color1 = bitmap.getPixel(5, 5);
                int color2 = bitmap.getPixel(15, 15);
                int color3 = bitmap.getPixel(25, 25);

                if ((color1 == color2) && (color1 == color3))
                    drawable = null;


            } catch (Exception e) {
                return "ERROR";
            }

            return "OK";
        }


        @Override
        protected void onPostExecute(String result) {
            try {
                if (drawable != null) {
                    mapWorkOrderToImage.put(id, drawable);
                    notifyDataSetInvalidated();
//                    ImageView img = (ImageView) findViewById(R.id.mainBackgroundaddNewWorkorder);
//                    img.setBackground(drawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
