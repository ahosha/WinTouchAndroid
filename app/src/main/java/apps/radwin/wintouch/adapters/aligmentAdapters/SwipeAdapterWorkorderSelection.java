package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import apps.radwin.wintouch.activities.alignmentActivities.WorkorderSelectionFragment;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 13/09/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */
public class SwipeAdapterWorkorderSelection extends FragmentStatePagerAdapter {

    private String[] titles = {"7 Days", "30 Days", "All" };
    String selectedProjectIdFromActivity = "";

    public SwipeAdapterWorkorderSelection(FragmentManager fm, String selectedProjectId) {
        super(fm);
        selectedProjectIdFromActivity = selectedProjectId;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new WorkorderSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt ("count", position);
        bundle.putString("Title", titles[position]);
        bundle.putString("projectId", selectedProjectIdFromActivity);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


}
