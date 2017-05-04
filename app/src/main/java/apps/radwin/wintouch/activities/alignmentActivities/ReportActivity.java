package apps.radwin.wintouch.activities.alignmentActivities;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 02/08/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;
import apps.radwin.wintouch.utils.PermissionUtils;

import static apps.radwin.wintouch.activities.alignmentActivities.Connect_To_Wif_iActivity.isActivityVisible;

public class ReportActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String selectedWorkorderId, linkEvaluationStatus, selectedProjectId;
    AligmentManager aligmentManagerClass; // will hold an instance of the aligment manager
    String TAG = "reportActivity";
    WorkingOrdersModel selectedWorkoder;
    Double linkEvaluationUpLink, linkEvaluationDownLink;
    TextView resultThroughputDown;
    TextView resultThroughputUp;
    TextView requiredThroughputDown;
    TextView requriedThroughputUp;
    TextView statusText;
    TextView updatedText;
    TextView selectedBand;
    TextView selectedFrequency;
    TextView selectedServiceType;
    TextView selectedBandwidth;
    TextView workorderName;
    TextView workorderPhone;
    Tracker analyticsTracker;
    ImageView statusImage;
    ImageView phoneIcon;
    ImageView cameraImageOne, cameraImageTwo, cameraImageThree;
    static final int REQUEST_IMAGE_ONE = 1;
    static final int REQUEST_IMAGE_TWO = 2;
    static final int REQUEST_IMAGE_THREE = 3;

    String testString;

    Bitmap bitmapFromCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workorder_settings_report_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar(); // points to the aligment manager

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
//                startActivity(i);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////////////
        //////////START OF IMPLEMENTATION//////////
        //////////////////////////////////////////

        resultThroughputDown = (TextView) findViewById(R.id.report_result_tput_down);
        resultThroughputUp = (TextView) findViewById(R.id.report_result_tput_up);
        requiredThroughputDown = (TextView) findViewById(R.id.report_required_tput_up);
        requriedThroughputUp = (TextView) findViewById(R.id.report_required_tput_down);
        statusText = (TextView) findViewById(R.id.report_passed_status_text);
        updatedText = (TextView) findViewById(R.id.report_passed_date_text);
        selectedBand = (TextView) findViewById(R.id.report_bandwidth_text);
        selectedFrequency = (TextView) findViewById(R.id.report_frequency_text);
        selectedServiceType = (TextView) findViewById(R.id.report_service_type_text);
        selectedBandwidth = (TextView) findViewById(R.id.report_chanel_bandwidth_text);
        workorderName = (TextView) findViewById(R.id.report_name_text);
        workorderPhone = (TextView) findViewById(R.id.report_phone_text);
        statusImage = (ImageView) findViewById(R.id.report_passed_pic);
        phoneIcon = (ImageView) findViewById(R.id.report_phone_icon);
        cameraImageOne = (ImageView) findViewById(R.id.image_Text_one_image);
        cameraImageTwo = (ImageView) findViewById(R.id.image_Text_Two_image);
        cameraImageThree = (ImageView) findViewById(R.id.image_Text_Three_image);

        //adds click listeners for picture buttons
        ///////////////////////////////////////
        cameraImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null &&
                        PermissionUtils.isPermissionGrantedInActivity(ReportActivity.this, Manifest.permission.CAMERA)) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_ONE);
                }

            }
        });

        cameraImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_TWO);
                }

            }
        });

        cameraImageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_THREE);
                }

            }
        });


        // gets bundle
        ///////////////////
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedWorkorderId = "";
                linkEvaluationUpLink = 0.0;
                linkEvaluationDownLink = 0.0;
                linkEvaluationStatus = "";
                selectedProjectId = "";
            } else {
                selectedWorkorderId = extras.getString("selectedWorkorderId");
                linkEvaluationUpLink = extras.getDouble("upLinkResult");
                linkEvaluationDownLink = extras.getDouble("downLinkResult");
                linkEvaluationStatus = extras.getString("status");
                selectedProjectId = extras.getString("selectedProjectId");
            }
        } else {
            try {
                selectedWorkorderId = (String) savedInstanceState.getSerializable("selectedWorkorderId");
                selectedProjectId = (String) savedInstanceState.getSerializable("selectedProjectId");
                linkEvaluationUpLink = (Double) savedInstanceState.getSerializable("upLinkResult");
                linkEvaluationDownLink = (Double) savedInstanceState.getSerializable("downLinkResult");
                linkEvaluationStatus = (String) savedInstanceState.getSerializable("status");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder

        phoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickPhone(v);
            }
        });

        try {
            Log.d(TAG, "onCreate: linkEvaluationDownLink; " + linkEvaluationDownLink);
            Log.d(TAG, "onCreate: linkEvaluationUpLink; " + linkEvaluationUpLink);

            Log.d(TAG, "onCreate: WorkingOrdersModel downLinkResult: " + selectedWorkoder.downLinkResult);
            Log.d(TAG, "onCreate: WorkingOrdersModel upLinkResult: " + selectedWorkoder.upLinkResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // selected trueput
        //////////////////////////
        try {
            resultThroughputDown.setText(String.valueOf(selectedWorkoder.downLinkResult) + " Mbps");
            resultThroughputUp.setText(String.valueOf(linkEvaluationUpLink) + " Mbps");
            requiredThroughputDown.setText(selectedWorkoder.truePutDown + " Mbps required");

            try {
                if (String.valueOf(selectedWorkoder.truePutDown).contains(".0")) {
                    String[] parts = String.valueOf(selectedWorkoder.truePutDown).split("\\.");
                    requiredThroughputDown.setText(parts[0] + " Mbps required");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            requriedThroughputUp.setText(String.valueOf(selectedWorkoder.truePutUp) + " Mbps required");

            try {
                if (String.valueOf(selectedWorkoder.truePutUp).contains(".0")) {
                    String[] parts = String.valueOf(selectedWorkoder.truePutUp).split("\\.");
                    requriedThroughputUp.setText(parts[0] + " Mbps required");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // status & date
        ////////////////////////////
        try {

            //supports backward combatibility
            ////////////////////////////
            if (selectedWorkoder.lastUpdateTime.length() < 16) {

                try {
                    ArrayList<String> monthNames = new ArrayList<String>();
                    monthNames.add("Jan");
                    monthNames.add("Feb");
                    monthNames.add("Mar");
                    monthNames.add("Apr");
                    monthNames.add("May");
                    monthNames.add("Jun");
                    monthNames.add("July");
                    monthNames.add("Aug");
                    monthNames.add("Sep");
                    monthNames.add("Oct");
                    monthNames.add("Nov");
                    monthNames.add("Dec");

                    String year = selectedWorkoder.lastUpdateTime.substring(0, 4);
                    String monthText = selectedWorkoder.lastUpdateTime.substring(4, 6);
                    String day = selectedWorkoder.lastUpdateTime.substring(6, 8);

                    String hourText = selectedWorkoder.lastUpdateTime.substring(9, 11);
                    String minute = selectedWorkoder.lastUpdateTime.substring(11, 13);

                    String monthName = monthNames.get(Integer.parseInt(monthText)-1);
                    String hourName;

                    if (Integer.parseInt(hourText) > 12) {
                        hourName = String.valueOf(Integer.parseInt(hourText) - 12) + ":" + minute + "p.m.";
                    } else {
                        hourName = String.valueOf((hourText)) + ":" + minute + "a.m.";
                    }

                    selectedWorkoder.lastUpdateTime = monthName + " " + day + "," + year + " " + hourName;

                    selectedWorkoder.save();
                } catch (NumberFormatException e) {
                    Log.d(TAG, "onCreate: couldn't finish text support in report screen");
                    e.printStackTrace();

                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm aaa");
                    String dateString = sdf.format(date);

                    selectedWorkoder.lastUpdateTime = dateString;
                    selectedWorkoder.save();
                }

            }

            updatedText.setText(selectedWorkoder.lastUpdateTime);


        } catch (Exception e) {
            e.printStackTrace();
        }

        // workorder Frequency details
        //////////////////////////////////////
        try {
            selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder

            //saves the selected frequency from the report screen
            ///////////////////////////////////////
            if (aligmentManagerClass.getWorkorderFrequency() != null && !aligmentManagerClass.getWorkorderFrequency().equals("Not Set")) {
                selectedWorkoder.selectedFrequency = Integer.valueOf(aligmentManagerClass.getWorkorderFrequency());

                selectedWorkoder.save();
            }

            selectedBand.setText(selectedWorkoder.CurrentSelectedBand.substring(0, 10) + "..");

            selectedFrequency.setText(selectedWorkoder.selectedFrequency + " MHz");

            // adds auto text if the bandwiddth is bigger then twenty
            //////////////////////////////////////
            if (Integer.valueOf(selectedWorkoder.currentChannelBandwith) >= 20) {
                selectedBandwidth.setText("AUTO");
            } else {
                selectedBandwidth.setText(selectedWorkoder.currentChannelBandwith + " MHz");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        // service type
        ////////////////////////////
        if ((selectedWorkoder.isBestEffort != null) && selectedWorkoder.isBestEffort) {
            selectedServiceType.setText("Best Effort");
        } else {
            selectedServiceType.setText("CIR");
        }

        // name and phone
        /////////////////////////
        try {
            workorderPhone.setText(selectedWorkoder.workingOrderPhoneNumber);
            if (selectedWorkoder.workingOrderName.length() > 29) {
                workorderName.setText(selectedWorkoder.workingOrderName.substring(0, 30) + "..");
            } else {
                workorderName.setText(selectedWorkoder.workingOrderName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // updates the selected workorder status to complete
        /////////////////////////
        try {
            selectedWorkoder.lastUpdateStatus = linkEvaluationStatus;
            selectedWorkoder.save();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //wodking order model - saving to model
        /////////////////////////
        try {
            WorkingOrdersModel selectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

//            selectedWorkorder.downLinkResult = String.valueOf(linkEvaluationDownLink);
//            selectedWorkorder.upLinkResult = String.valueOf(linkEvaluationUpLink);

            selectedWorkorder.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // sets the status image red if status failed
        //////////////////////////
        // sets the status image red if status failed
        //////////////////////////
        try {
            if ((selectedWorkoder.downLinkResult < selectedWorkoder.truePutDown) || linkEvaluationUpLink < selectedWorkoder.truePutUp) {
                statusImage.setImageResource(R.drawable.ic_cancel_black_24dp);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        //sets up  the edit dialog
        //////////////////
        com.getbase.floatingactionbutton.FloatingActionButton editFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_edit);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ReportActivity.this);
                dialog.setContentView(R.layout.dialog_edit_report);
                dialog.setTitle("Edit");
//            dialog.setCancelable(false);
                dialog.show();

                Button dialogSaveButton = (Button) dialog.findViewById(R.id.settingsDialog_button_save);
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.settingsDialog_button_cancel);
                final EditText dialogEditNameText = (EditText) dialog.findViewById(R.id.editDialog_editText_Name);
                final EditText dialogEditPhoneText = (EditText) dialog.findViewById(R.id.editDialog_editText_Phone);


                try {
                    dialogEditNameText.setText(selectedWorkoder.workingOrderName);
                    dialogEditPhoneText.setText(selectedWorkoder.workingOrderPhoneNumber);
                    // Avner - we cannot trim the name when we edit the value

//                    if (selectedWorkoder.workingOrderName.length() > 29) {
//                        dialogEditNameText.setText(selectedWorkoder.workingOrderName.substring(0, 30) + "..");
//                    } else {
//                    }
//                    if (selectedWorkoder.workingOrderPhoneNumber.length() > 29) {
//                        dialogEditPhoneText.setText(selectedWorkoder.workingOrderPhoneNumber.substring(0, 30) + "..");
//                    } else {
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //cancel button
                /////////////////////////
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });

                //save button
                /////////////////////////
                dialogSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        selectedWorkoder.workingOrderName = String.valueOf(dialogEditNameText.getText());
                        selectedWorkoder.workingOrderPhoneNumber = String.valueOf(dialogEditPhoneText.getText());

                        if (selectedWorkoder.workingOrderName.trim().isEmpty()) {
                            Toast.makeText(getApplication(), getResources().getString(R.string.missing_workorder_name),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        selectedWorkoder.save();

                        try {
                            if (selectedWorkoder.workingOrderName.length() > 30) {
                                workorderName.setText(selectedWorkoder.workingOrderName.substring(0, 29) + "..");
                            } else {
                                workorderName.setText(selectedWorkoder.workingOrderName);
                            }
                            workorderPhone.setText(selectedWorkoder.workingOrderPhoneNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dialog.hide();


                    }
                });

            }
        });


        // sends complete action
        ////////////////////////
        aligmentManagerClass.sendComplete(callbackForactionCompleteCallback);


        com.getbase.floatingactionbutton.FloatingActionButton emailFab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_email);
        emailFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ///////////

                    WorkingOrdersModel myWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);


                    if ((BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_one.png") == null) &&
                            (BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_two.png") == null) &&
                            (BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_three.png") == null)) {


                        new AlertDialog.Builder(ReportActivity.this)
                                .setTitle("Pictures Missing")
                                .setMessage("Do you want to send this report without the installation pictures?")
                                .setPositiveButton("Send Anyway", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        inislizeSendEmail ();
                                    }
                                })
                                .setNegativeButton("Don't Send", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    } else {
                        inislizeSendEmail ();
                    }



                } catch (Exception c) {
                    c.printStackTrace();
                }
            }
        });

        // show images in imageview if they already taken amd are in the gallery
        //////////////////////////////////////////////////
        WorkingOrdersModel myWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

        if (BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_one.png") != null) {
            cameraImageOne.setImageBitmap(BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_one.png"));
        } else {
            cameraImageOne.setImageResource(R.drawable.ic_aspect_ratio_black_24dp);
        }

        if (BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_two.png") != null) {
            cameraImageTwo.setImageBitmap(BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_two.png"));
        } else {
            cameraImageTwo.setImageResource(R.drawable.ic_aspect_ratio_black_24dp);
        }

        if (BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_three.png") != null) {
            cameraImageThree.setImageBitmap(BitmapFactory.decodeFile("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_three.png"));
        } else {
            cameraImageThree.setImageResource(R.drawable.ic_aspect_ratio_black_24dp);
        }

    }

    private void inislizeSendEmail () {

        WorkingOrdersModel myWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

        if (selectedWorkoder == null) {
            selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder
        }

        String woBE;

        if (selectedWorkoder.isBestEffort) {
            woBE = "Best Effort";
        } else {
            woBE = "CIR";
        }

        // gets cbw for the mail
        //////////////////////////////////////
        String cbtwForMail = selectedWorkoder.currentChannelBandwith + "MHz";

        if (Integer.valueOf(selectedWorkoder.currentChannelBandwith) >= 20) {
            cbtwForMail = "AUTO";
        }


        ProjectsModel connectedProject = ProjectsModel.getProjectWithId(selectedWorkoder.projectId);


        // get images
        /////////////////////////
        ArrayList<String> pathNames = new ArrayList<String>();
        try {
            pathNames.add("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_one.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pathNames.add("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_two.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pathNames.add("/sdcard/WINTouch/" + myWorkorder.workorderWifiSSID.toLowerCase() + "_three.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String TO_EMAIL = "";
        if (connectedProject.projectEmail != null) {
            TO_EMAIL = connectedProject.projectEmail;

        }

        // time and date collection
        ////////////////////////
        String latitude = String.valueOf(selectedWorkoder.orderLatitude);
        String longitude = String.valueOf(selectedWorkoder.orderLongitude);


        Log.d(TAG, "onClick: selectedWorkoder.workorderWifiSSID: " + selectedWorkoder.workorderWifiSSID);

        final String emailBody =
                "\n" +
                        "Project Name: ".concat(connectedProject.projectName) +
                        "\nCustomer Name: ".concat(selectedWorkoder.workingOrderName) +
                        (!selectedWorkoder.workingOrderAdress.equals("") ? "\nAddress: " + selectedWorkoder.workingOrderAdress : "") +
                        (!selectedWorkoder.workingOrderPhoneNumber.equals("") ? "\nPhone: " + selectedWorkoder.workingOrderPhoneNumber : "") +
                        "\nLast Update Time: " + selectedWorkoder.lastUpdateTime +
                        (selectedWorkoder.orderLatitude != 0 ? "\nLatitude: " + selectedWorkoder.orderLatitude : "") +
                        (selectedWorkoder.orderLongitude != 0 ? "\nLongitude: " + selectedWorkoder.orderLongitude : "") +
//                        (!latitude.equals("") ? "\nLatitude: " + latitude : "") +
//                        (!longitude.equals("") ? "\nLongitude: " + longitude : "") +
                        "\n" +
                        "\nBand: " + selectedWorkoder.CurrentSelectedBand +
                        "\nCBW: " + cbtwForMail +
                        "\nFrequency: " + selectedWorkoder.selectedFrequency + "MHz" +
                        "\nService Type: " + woBE +
                        "\n" +
                        (selectedWorkoder.workorderWifiSSID != null && !selectedWorkoder.workorderWifiSSID.equals("") && !selectedWorkoder.workorderWifiSSID.equals(" ") ? "\nSN: " + selectedWorkoder.workorderWifiSSID : "") +
                        (selectedWorkoder.macAddress != null && !selectedWorkoder.macAddress.equals("") ? "\nMAC Address: " + selectedWorkoder.macAddress : "") +
                        "\nRequired Up Throughput: " + String.valueOf(selectedWorkoder.truePutUp) + " Actual: " + ((selectedWorkoder.upLinkResult == null) ? "N.A" : String.valueOf(selectedWorkoder.upLinkResult)) +
                        "\nRequired Down Throughput: " + String.valueOf(selectedWorkoder.truePutDown) + " Actual: " + ((selectedWorkoder.downLinkResult == null) ? "N.A" : String.valueOf(selectedWorkoder.downLinkResult)) +
                        "\n" +
                        "\n" +
                        "\nSent From RADWIN WINTouch Application";

        email(ReportActivity.this, TO_EMAIL, "", "Work Order: " + selectedWorkoder.workingOrderName + " from Project: " + connectedProject.projectName, emailBody, pathNames);


    }

    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, List<String> filePaths) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);

        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths) {
            File fileIn = new File(file);
            if (fileIn.exists()) {
                Uri u = Uri.fromFile(fileIn);
                uris.add(u);
            }
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //checks for null data
        ////////////////////////
        if (data != null) {

            WorkingOrdersModel myWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

            // get images
            if (data.getExtras() != null) {
                Bundle extras = data.getExtras();
                bitmapFromCamera = (Bitmap) extras.get("data");

                if (requestCode == REQUEST_IMAGE_ONE && resultCode == RESULT_OK) {

                    createDirectoryAndSaveFile(bitmapFromCamera, myWorkorder.workorderWifiSSID.toLowerCase() + "_one.png");

                    cameraImageOne.setImageBitmap(bitmapFromCamera);

                } else if (requestCode == REQUEST_IMAGE_TWO && resultCode == RESULT_OK) {

                    createDirectoryAndSaveFile(bitmapFromCamera, myWorkorder.workorderWifiSSID.toLowerCase() + "_two.png");

                    cameraImageTwo.setImageBitmap(bitmapFromCamera);

                } else if (requestCode == REQUEST_IMAGE_THREE && resultCode == RESULT_OK) {

                    createDirectoryAndSaveFile(bitmapFromCamera, myWorkorder.workorderWifiSSID.toLowerCase() + "_three.png");

                    cameraImageThree.setImageBitmap(bitmapFromCamera);

                }

            }
        }
    }

    /**
     * saves an image from camera in a spacific place
     *
     * @param imageToSave
     * @param fileName
     */
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/WINTouch");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/WINTouch/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/WINTouch/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * encodes a picture to memory
     *
     * @param image
     * @param compressFormat
     * @param quality
     * @return
     */
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /**
     * decodes a picture from memory
     *
     * @param input
     * @return
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        startActivity(i);

    }

    public void onClickPhone(View view) {

        String phone = workorderPhone.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workorder__settings__report, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Report Screen");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

        try {
            selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Analytics
        /////////////////////////
//        int serviceTypeForAnalytics = 0;
        if ((selectedWorkoder.isBestEffort != null) && selectedWorkoder.isBestEffort) {
            analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Site Properties")
                    .setAction("Service Type")
                    .setLabel("BestEffort")
                    .setValue(1)
                    .build());

        } else {
            analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Site Properties")
                    .setAction("Service Type")
                    .setLabel("CIR")
                    .setValue(1)
                    .build());

        }

        String AnalyticsCBW = String.valueOf(Integer.parseInt(selectedWorkoder.currentChannelBandwith));
        if (Integer.valueOf(selectedWorkoder.currentChannelBandwith) >= 20) {
            AnalyticsCBW = "Auto";
        }


        try { // trys to upload frequency
            analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Site Properties")
                    .setAction("Frequency")
                    .setLabel(aligmentManagerClass.getWorkorderFrequency())
                    .setValue(1)
                    .build());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Site Properties")
                .setAction("Band")
                .setLabel(selectedWorkoder.CurrentSelectedBand)
                .setValue(1)
                .build());


        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Site Properties")
                .setAction("CBW")
                .setLabel(String.valueOf(AnalyticsCBW))
                .setValue(1)
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            new AlertDialog.Builder(ReportActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_reAlign_headline))
                    .setCancelable(true)
                    .setMessage(getResources().getString(R.string.dialog_reAlign_body))
                    .setPositiveButton(getResources().getString(R.string.dialog_reAlign_positive), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            //  updates the status
                            ////////////////////////////////////
                            selectedWorkoder.lastUpdateStatus = "inComplete";
                            selectedWorkoder.save();

                            // removes the dialog
                            /////////////////////////////////
                            dialog.cancel();

                            // moves to scanning barcode activity
                            ////////////////////////////////////
                            Intent i = new Intent(getApplication(), ScanningBarcodeActivityWithFragment.class); // moves to the fine aligment screen
                            i.putExtra("projectId", selectedProjectId);
                            i.putExtra("workorderId", selectedWorkoder.workordertId);
                            startActivity(i);

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_reAlign_negetive), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {

            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();
            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects

                wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard

                wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

        } else if (id == R.id.projects) {

            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {

            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.installationGuide) {
            Intent i = new Intent(getApplication(), InstallationGuideActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * callback interface for canceling the wifies
     */
    public interface actionCompleteCallbackInterface {

        void actionCompleteCallbackFunction(boolean result);
    }


    /**
     * tha callback is called once all the wifies's are forgotten
     * the callback will be called once we forgot all the networks
     */
    actionCompleteCallbackInterface callbackForactionCompleteCallback = new actionCompleteCallbackInterface() {

        @Override
        public void actionCompleteCallbackFunction(boolean result) {

            if (isActivityVisible()) {

                if (result) { //all wifi'es has been stopped

                    Boolean isRequired = false;
                    if (selectedWorkoder.radiusInstallConfirmationRequired.equals("true")) {
                        isRequired = true;
                    }

                    //updates set final data call
                    ///////////////////////


                    //gets date
                    ////////////////
                    String dateForInstallation = "";
                    String installationType = "cir";

                    // updates installation type
                    if ((selectedWorkoder.isBestEffort != null) && selectedWorkoder.isBestEffort) {
                        installationType = "be";
                    }

                    //updates date
                    try {
                        dateForInstallation = String.valueOf(updatedText.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //updates recent rss values
                    String rssDlToReport = "0";
                    String rssUlToReport = "0";

                    try {
                        rssDlToReport = selectedWorkoder.lateseDownlink;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        rssUlToReport = selectedWorkoder.latestUplink;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    aligmentManagerClass.setGeoLocation(selectedWorkoder.orderLatitude, selectedWorkoder.orderLongitude, isRequired, dateForInstallation, installationType, selectedWorkoder.CurrentSelectedBand, selectedWorkoder.currentChannelBandwith, selectedWorkoder.selectedFrequency, rssUlToReport, rssDlToReport, selectedWorkoder.upLinkResult, selectedWorkoder.downLinkResult, "123456");

                } else { //wifi is OFF SHOW DIALOG FOR WIFI IS OFF
                    Log.d("myLogs", "wifi is OFF");
                }
            }
        }
    };
}
