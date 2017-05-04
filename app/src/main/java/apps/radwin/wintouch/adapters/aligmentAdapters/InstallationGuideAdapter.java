package apps.radwin.wintouch.adapters.aligmentAdapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.InstallationGuideModel;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 25/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class InstallationGuideAdapter extends ArrayAdapter<InstallationGuideModel> {

    final static String TAG = InstallationGuideAdapter.class.getSimpleName();

    public InstallationGuideAdapter(Context context, List<InstallationGuideModel> installationGuideModel) {
        super(context, 0, installationGuideModel);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        InstallationGuideModel currentModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_installation_guide, parent, false);
        }

        TextView installationName = (TextView) convertView.findViewById(R.id.installationGuide_name_text);
        TextView installationDesscription = (TextView) convertView.findViewById(R.id.installationGuide_description_text);

        installationName.setText(currentModel.installationGuideName);
        installationDesscription.setText(currentModel.installationGuideDescription);


        return convertView;

    }


}