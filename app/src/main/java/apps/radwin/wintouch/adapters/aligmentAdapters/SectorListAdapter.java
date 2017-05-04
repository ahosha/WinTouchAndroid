package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.BestPositionListModel;

public class SectorListAdapter extends ArrayAdapter<BestPositionListModel> {
    private final Context context;
    private final ArrayList<BestPositionListModel> values;
    private int clickedPosition = 0;

    public SectorListAdapter(Context context, ArrayList<BestPositionListModel> values) {
        super(context, 0, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BestPositionListModel hbs = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sector, parent, false);
        }

        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.name);
        TextView txtRssi = (TextView) convertView.findViewById(R.id.rssi);
        LinearLayout linearLayoutBg = (LinearLayout) convertView.findViewById(R.id.sectorListLenearLayout);
        TextView txtDistance = (TextView) convertView.findViewById(R.id.distance);
        TextView txtNetworkId = (TextView) convertView.findViewById(R.id.tvNetowkrId);
        TextView sep = (TextView) convertView.findViewById(R.id.separator);
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout_best);
        ImageView hbsType = (ImageView) convertView.findViewById(R.id.hbd_type);
        ViewGroup.LayoutParams lp = hbsType.getLayoutParams();


        if (clickedPosition == position) {
            linearLayoutBg.setBackgroundColor(context.getResources().getColor(R.color.alignment_canvasShading_light));
        } else {
            linearLayoutBg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
//        if (position == 0) {
//            txtName.setTextColor(Color.WHITE);
//            txtName.setTextSize(24);
//            txtDistance.setTextColor(Color.WHITE);
//            txtRssi.setTextColor(Color.WHITE);
//            hbsType.setImageResource(R.drawable.sector_large);
//            layout.setBackgroundColor(Color.parseColor("#273238"));
//            lp.width = 200;
//            lp.height = 200;
//        } else {
//            lp.width = 156;
//            lp.height = 170;
            txtName.setTextSize(16);
            txtName.setTextColor(Color.BLACK);
            txtDistance.setTextColor(Color.BLACK);
            txtRssi.setTextColor(Color.BLACK);
            layout.setBackgroundColor(Color.parseColor("#ffffff"));
            hbsType.setImageResource(R.drawable.sector_green);
//        }



        //MICHAEL PLEASE CAN U CALCULATE DISTANCE HERE ?
        ////////////////////////////////////////////////
        int distance_from_mikael = 0;

        try {
            hbsType.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            txtName.setText(hbs.sectorID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("SectorListAdapteer", "getView: bestRSS: "+ hbs.bestRSS);
            txtRssi.setText(String.valueOf(hbs.bestRSS));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            txtDistance.setText(distance_from_mikael + "Km");
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtNetworkId.setText("");

        return convertView;
    }

    public void setLayoutColor(int position) {
        clickedPosition = position;

        notifyDataSetChanged();
    }
}