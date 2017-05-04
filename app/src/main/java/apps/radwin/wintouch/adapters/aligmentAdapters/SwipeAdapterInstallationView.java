package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import apps.radwin.wintouch.fragments.alignmentFragments.InstallationGuideFragmentAntennaAlignment;
import apps.radwin.wintouch.fragments.alignmentFragments.IstallationGuideFragmentChecklist;
import apps.radwin.wintouch.fragments.alignmentFragments.IstallationGuideFragmentVideoGudies;

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

public class SwipeAdapterInstallationView extends FragmentStatePagerAdapter {

    private String[] titles = {"Installation", "Alignment", "Checklist"};
    String selectedInstallationId;

    public SwipeAdapterInstallationView (FragmentManager fm, String installationId) {
        super(fm);
        if (selectedInstallationId == null) {
            selectedInstallationId = installationId;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("swipeAdapter", "getItem: position: "+position);

        if (position == 0) {

            Fragment fragment = new IstallationGuideFragmentVideoGudies();
            Bundle bundle = new Bundle();
            bundle.putInt("count", position);
            bundle.putString("instllationId", selectedInstallationId);
            fragment.setArguments(bundle);

            return fragment;

        } else if (position == 1) {

            Fragment fragment = new InstallationGuideFragmentAntennaAlignment();
            Bundle bundle = new Bundle();
            bundle.putInt("count", position);
            bundle.putString("instllationId", selectedInstallationId);
            fragment.setArguments(bundle);

            return fragment;

        } else {

            Fragment fragment = new IstallationGuideFragmentChecklist();
            Bundle bundle = new Bundle();
            bundle.putInt("count", position);
            bundle.putString("instllationId", selectedInstallationId);
            fragment.setArguments(bundle);

            return fragment;

        }

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
