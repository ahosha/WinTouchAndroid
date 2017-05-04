package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.InstallationGuideMoviesModel;

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

public class VideoguidesAdapter extends ArrayAdapter<InstallationGuideMoviesModel> {

    final static String TAG = VideoguidesAdapter.class.getSimpleName();

    public VideoguidesAdapter(Context context, List<InstallationGuideMoviesModel> installationGuideModel) {
        super(context, 0, installationGuideModel);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        InstallationGuideMoviesModel currentModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_installation_video_guide, parent, false);
        }

        TextView installationName = (TextView) convertView.findViewById(R.id.installagion_guide_video_name);
        ImageView installationImage = (ImageView) convertView.findViewById(R.id.installagion_guide_video_image);

        try {
            installationName.setText(currentModel.movieName);
//            installationDesscription.setText(currentModel.movieDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            installationImage.setImageResource(currentModel.movieLink);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }


}