package apps.radwin.wintouch.adapters.aligmentAdapters;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 28/02/2017.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.ExpendebleMenuPositionModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class ExpandableFrequencyListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    AligmentManager aligmentManagerClass;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableFrequencyListAdapter (Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, AligmentManager mainAlignmentManager) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        aligmentManagerClass = mainAlignmentManager;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        String firstStringText = "";
        ArrayList<ExpendebleMenuPositionModel> alignmentArrayList =  aligmentManagerClass.getCheckedExpendbleMenuItems();

        try {
            firstStringText = (String) getChild(groupPosition, childPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.list_item_layout, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        ImageView childImageCiew = (ImageView) convertView.findViewById(R.id.checkItemImageView);

        childImageCiew.setImageResource(R.drawable.ic_check_circle_black_24dp);

        txtListChild.setText(firstStringText);

        for (int i = 0; i < alignmentArrayList.size(); i++) {
            if ((alignmentArrayList.get(i).groupPosition == groupPosition) && (alignmentArrayList.get(i).itemPosition == childPosition)) {

                //adds teh correct frequency
                alignmentArrayList.get(i).frequencyId = firstStringText;

                childImageCiew.setImageResource(R.drawable.ic_check_circle_black_24dp_2);
            }
        }

        //updates the array list
        aligmentManagerClass.setCheckedExpendbleMenuItems(alignmentArrayList);


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        int mainSize = Math.round(this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size());

        return mainSize;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        ArrayList<ExpendebleMenuPositionModel> alignmentArrayList =  aligmentManagerClass.getCheckedExpendbleMenuItems();

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_item, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        TextView groupViewSelectionCount = (TextView) convertView
                .findViewById(R.id.group_count_header);

        groupViewSelectionCount.setText("0");

        for (int i = 0; i < alignmentArrayList.size(); i++) {
            if (alignmentArrayList.get(i).groupPosition == groupPosition) {
                int textToUpdate = Integer.valueOf(String.valueOf(groupViewSelectionCount.getText()))  + 1;
                groupViewSelectionCount.setText(String.valueOf(textToUpdate));
            }
        }



        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
