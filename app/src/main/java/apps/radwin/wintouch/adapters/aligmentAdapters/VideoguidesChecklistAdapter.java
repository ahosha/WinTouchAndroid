package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.InstallationGuideChecklistModel;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 28/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class VideoguidesChecklistAdapter  extends ArrayAdapter<InstallationGuideChecklistModel> {

    final static String TAG = VideoguidesChecklistAdapter.class.getSimpleName();

    public VideoguidesChecklistAdapter(Context context, List<InstallationGuideChecklistModel> installationGuideModel) {
        super(context, 0, installationGuideModel);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        InstallationGuideChecklistModel currentModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            try {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_installation_checklist, parent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TextView installationName = (TextView) convertView.findViewById(R.id.installationGuide_checklist_main_text);

        installationName.setText(currentModel.checkMarkName);


        return convertView;

    }


}