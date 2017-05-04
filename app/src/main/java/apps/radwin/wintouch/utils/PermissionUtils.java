package apps.radwin.wintouch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Utility class which handles all permissions
 */
public class PermissionUtils {

    /**
     * Check if camera permission is granted and if not aks for it
     *
     * @param context Context of the application
     */
    public static boolean isPermissionGrantedInActivity(Activity context, String permission) {
        boolean isPermissionGranted = true;
        if (PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(context, permission)) {
            isPermissionGranted = false;
            requestPermission(null, context, permission);
        }

        return isPermissionGranted;
    }

    /**
     * Only checks if camera is permission is granted or not
     *
     * @param context Context of the application
     */
    public static boolean isPermissionGrantedInActivity(Activity context, String permission, Runnable task) {
        boolean isPermissionGranted = true;
        if (PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(context, permission)) {
            isPermissionGranted = false;
            task.run();
        }

        return isPermissionGranted;
    }

    public static boolean isPermissionGrantedInFragment(Fragment fragment, String permission) {
        boolean isPermissionGranted = true;
        if (PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(fragment.getContext(), permission)) {
            isPermissionGranted = false;
            requestPermission(fragment, fragment.getContext(), permission);
        }

        return isPermissionGranted;
    }

    /**
     * Request permission from the user
     *
     * @param context Context of the application
     * @param permission Permission to be asked
     */
    private static void requestPermission(final Fragment fragment, final Context context, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            new android.app.AlertDialog.Builder(context)
                    .setMessage(
                            "This app needs permission to use the phone`s"
                                    .concat(
                                            permission.substring(
                                                    permission.lastIndexOf(".", permission.length())
                                            )
                                    ).concat(" in order continue.")
                    )
                    .setPositiveButton(
                            "Allow",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(fragment == null) {
                                        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 1);
                                    } else {
                                        fragment.requestPermissions(new String[]{permission}, 1);
                                    }
                                }
                            }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 1);
        }
    }

    //TODO : Implement all permission request in PermissionUtils class
}
