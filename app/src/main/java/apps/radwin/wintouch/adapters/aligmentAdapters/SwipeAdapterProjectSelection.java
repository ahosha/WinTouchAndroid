package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import apps.radwin.wintouch.activities.alignmentActivities.ProjectSelectionFragmentSevenDays;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 11/09/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */
public class SwipeAdapterProjectSelection extends FragmentStatePagerAdapter{

    private String[] titles = {"7 Days", "30 Days", "All" };

    public SwipeAdapterProjectSelection(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        Log.d("swipeAdapter", "getItem: position: "+position);

        Fragment fragment = new ProjectSelectionFragmentSevenDays();
        Bundle bundle = new Bundle();
        bundle.putInt ("count", position);
        bundle.putString("Title", titles[position]);
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
