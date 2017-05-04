package apps.radwin.wintouch.screenManagers;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import apps.radwin.wintouch.models.BandsModel;
import apps.radwin.wintouch.models.BestPositionListModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.models.pojoModels.pojoBestPosition.BestPositionPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetAllBands.GetBandsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoSetBandCallback.PojoSetBand;

public class PojoParserHelper {

    public static Boolean getSetAlignmentCommandsResponse(PojoSetBand pojo) {
        try {
            return pojo.getData().getMessage().equals("Done");
        } catch (Exception e) {
            return false;
        }
    }

    public static ArrayList<BestPositionListModel> CreateBestHbsList(BestPositionPojo data) {

        ArrayList<BestPositionListModel> bestHbsList = new ArrayList<BestPositionListModel>();

        try {
            for (int i = 0; i < data.getData().getHBS().size(); i++) {

                BestPositionListModel bestHsuModel = new BestPositionListModel(
                        data.getData().getHBS().get(i).getSectorType(),
                        data.getData().getHBS().get(i).getSectorDirection(),
                        data.getData().getHBS().get(i).getAntennaBeamwidth(),
                        data.getData().getHBS().get(i).getChannel(),
                        data.getData().getHBS().get(i).getChannelBw(),
                        data.getData().getHBS().get(i).getBestRSS(),
                        data.getData().getHBS().get(i).getSectorID(),
                        data.getData().getHBS().get(i).getAvailableResourcesDL(),
                        data.getData().getHBS().get(i).getAvailableResourcesUL(),
                        data.getData().getHBS().get(i).getBestEffortEnabled(),
                        data.getData().getHBS().get(i).getLatitude(),
                        data.getData().getHBS().get(i).getLongitude(),
                        data.getData().getHBS().get(i).getSectorIdMatched(),
                        data.getData().getHBS().get(i).getCursorLocation().getCellNumber(),
                        data.getData().getHBS().get(i).getCursorLocation().getElevationCell(),
                        data.getData().getHBS().get(i).getCursorLocation().getElevation(),
                        data.getData().getHBS().get(i).getCursorLocation().getHorizontal()
                );

                bestHbsList.add(bestHsuModel);
            }
        }
        catch (Exception ex){
            Log.d("PojoParserHelper", ex.getMessage());
            return null;
        }
        return bestHbsList;
    }

    /**
     * aranges and priorites best hbs by the rules indicated in the std - SORT THE LIST
     * the function will hold the algorythem to calculate the best position and will return an orginiazed base points by the ruling described by the system
     * the algorythem for now has 2 rules, first one, is:
     * //1.1 - ID
     * RULE ONE EVERY HBS HAS TO HAVE THE SAME NAME AS WE GOT IN THE PROJECT
     * WE CHEAT and choose a default id to later change to the id in the work order
     * //1.2 - freeResurces
     * RULE TWO EVERY HBS FOUND HAS TO HAVE FREE RESURCES
     * WE DONT KNOW IF WE ARE BEST EFFORT OR NOT SO WE ASSUME WE ARE NOT
     * THIS IS ONLY A CHEAT
     * the function will return the data to the best position loop and maybe a diffrent architechture is in order
     * for now this function happens every time the best position loop happens that is roughly every second
     *
     * @param arrayOfBaseStationsAlignmentManager
     * @param selectedWorkorderId
     * @return
     */
    public static ArrayList<BestPositionListModel> filterBestHbsFromServer(ArrayList<BestPositionListModel> arrayOfBaseStationsAlignmentManager, String selectedWorkorderId) {

        if (arrayOfBaseStationsAlignmentManager == null)
            return null;

        //declers an empty sorted list
        ArrayList<BestPositionListModel> sortedList = new ArrayList<BestPositionListModel>();
        //declers an empty sorted list
        ArrayList<BestPositionListModel> sortedListAfterFirstRule = new ArrayList<BestPositionListModel>();

        //gets if project is best effort or not
        WorkingOrdersModel selectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

        //RULE ONE
        //1.1 - sector id true
        // checks if the sector id is matched
        for (int i = 0; i < arrayOfBaseStationsAlignmentManager.size(); i++) {

            if (arrayOfBaseStationsAlignmentManager.get(i).sectorIdMatched) {

                sortedList.add(arrayOfBaseStationsAlignmentManager.get(i)); // adds the position if the minimum avilable resurces
            }
        }

        //copys the sorted list
        for (int i = 0; i < sortedList.size(); i++) {
            sortedListAfterFirstRule.add(sortedList.get(i));
        }

        //1.2 - freeResurces
        //RULE TWO EVERY HBS FOUND HAS TO HAVE FREE RESURCES
        //WE DONT KNOW IF WE ARE BEST EFFORT OR NOT SO WE ASSUME WE ARE NOT
        //THIS IS ONLY A CHEAT

        int MIN_AVILABLE_RESURCES_REQUIRED = 10;

        for (int i = 0; i < sortedListAfterFirstRule.size(); i++) {

            if (selectedWorkorder.isBestEffort == false) {

                if ((sortedListAfterFirstRule.get(i).availableResourcesDL < MIN_AVILABLE_RESURCES_REQUIRED) || (sortedListAfterFirstRule.get(i).availableResourcesUL < MIN_AVILABLE_RESURCES_REQUIRED)) {
                    sortedList.remove(i);
                }

            } else { // the working order is best effort

                if (sortedListAfterFirstRule.get(i).bestEffortEnabled == false) {

                    sortedList.remove(i);
                }
            }
        }

        // rule 1.3 - position
        // RULE THREE WE MUST BE WITHIN 45 ANGLES FROM THE OTHER POINT

        // gets device position
        ///////////////////////////////
        WorkingOrdersModel selectedWorkingOrder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);

        //declers an empty sorted list
        ArrayList<BestPositionListModel> sortedListBeforePosition =  new ArrayList<BestPositionListModel>();;

        //copys the sorted list
        for (int i = 0; i < sortedList.size(); i++) {
            sortedListBeforePosition.add(sortedList.get(i));
        }

        //runs on all sorted list and verify the angle if it's in range
        ///////////////////////////////
        for (int i = 0; i < sortedListBeforePosition.size(); i++) {

            if (ifAngleInBearing(selectedWorkingOrder.orderLatitude, selectedWorkingOrder.orderLongitude, sortedListBeforePosition.get(i).latitude, sortedListBeforePosition.get(i).longitude, sortedListBeforePosition.get(i).sectorDirection, sortedListBeforePosition.get(i).antennaBeamwidth) == false) {
                try {
                    sortedList.remove(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("PojoParser", "onClick: //////////////////////////////////");
        Log.d("PojoParser", "onClick: selectedWorkingOrder.orderLatitude: "+selectedWorkingOrder.orderLatitude);
        Log.d("PojoParser", "onClick: selectedWorkingOrder.orderLongitude: "+selectedWorkingOrder.orderLongitude);
        Log.d("PojoParser", "onClick: //////////////////////////////////");

        return sortedList;
    }


    public void unitTestingAngleBearing () {

        ArrayList<Double> latTestings = new ArrayList<>();
        ArrayList<Double> longTestings = new ArrayList<>();
        ArrayList<Double> angleForHbs = new ArrayList<>();


        latTestings.add(32.092639);
        longTestings.add(34.775541);

        latTestings.add(32.099154);
        longTestings.add(34.779752);

        for (int i = 0; i < latTestings.size(); i++) {
            if (ifAngleInBearing(latTestings.get(i), longTestings.get(i), 32.086859, 34.789607, 360, 90)) {
                Log.d("Unitesting", "false !!!");
            } else {
                Log.d("Unitesting", "trtue !!!");
            }

        }
    }



    /**
     * calculates angle between two points and a diffrence between that angle and hsu angle
     * @param lat1 first hsu latidude
     * @param lon1 first hsu longitude
     * @param hbsLat2 target latidude
     * @param hbsLon2 target longitude
     * @param angleOfHbs target angle of hsu
     * @param antennaBeamwidth
     * @return returns true if were not in range
     */
    public static boolean ifAngleInBearing(double lat1, double lon1, double hbsLat2, double hbsLon2, double angleOfHbs, Integer antennaBeamwidth) {

        double ALLOWED_RANGE = antennaBeamwidth/2;

        // returns true if something is 0
        /////////////////////
        if (((lat1 == 0) && (lon1 == 0)) || (hbsLat2 == 0) || (hbsLon2 == 0) || (angleOfHbs == 0)) {
            return true;
        }

        // calculate angles
        /////////////////////
        double distanceLon = (hbsLon2-lon1) * Math.PI / 180;
        double Lat1 = lat1 * Math.PI / 180;
        double Lat2 = hbsLat2 * Math.PI / 180;
        double y = Math.sin(distanceLon) * Math.cos(Lat2);
        double x = Math.cos(Lat1)*Math.sin(Lat2) - Math.sin(Lat1)*Math.cos(Lat2)*Math.cos(distanceLon);

        double facingAngle = Math.round ((Math.atan2(y, x) * 180 / Math.PI + 360) % 360);

        // calculate diffrence in angle
        /////////////////////
        //return to degrees >
        double anglediff = (facingAngle - angleOfHbs + 180 + 360) % 360;

        // returns true if were not in range
        /////////////////////
        Log.d("UniTestings", "ifAngleInBearing: anglediff: "+anglediff);
        Log.d("UniTestings", "ifAngleInBearing: ALLOWED_RANGE: "+ALLOWED_RANGE);

        if (anglediff < ALLOWED_RANGE) {
            return false;
        } else {
            if ((360 - anglediff) < ALLOWED_RANGE) {
                return false;
            } else {
                return true;
            }
        }

    }

    public static ArrayList<BandsModel> CreateBandsList(GetBandsPojo response, String selectedProjectId, boolean saveToDb) {

        ArrayList<BandsModel> supportedDeviceBands = new ArrayList<BandsModel>();

        for (int i = 0; i < response.getData().getBandsList().size(); i++) {

            String bandWidthString = "";


            BandsModel bandsModel = new BandsModel(); // projects model saving to the sql

            bandsModel.connectedProjectId = selectedProjectId;
            bandsModel.bandId = response.getData().getBandsList().get(i).getBandId();
            bandsModel.bandName = response.getData().getBandsList().get(i).getBandDescription();


            try {
                bandsModel.frequency5List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW5Freq());
                if(bandsModel.frequency5List.length() > 0) {
                    bandWidthString = bandWidthString + "5, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bandsModel.frequency7List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW7Freq());
                if(bandsModel.frequency7List.length() > 0) {
                    bandWidthString = bandWidthString + "7, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bandsModel.frequency10List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW10Freq());
                if(bandsModel.frequency10List.length() > 0) {
                    bandWidthString = bandWidthString + "10, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bandsModel.frequency14List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW14Freq());
                if(bandsModel.frequency14List.length() > 0) {
                    bandWidthString = bandWidthString + "14, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bandsModel.frequency20List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW20Freq());
                if(bandsModel.frequency20List.length() > 0) {
                    bandWidthString = bandWidthString + "20, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                bandsModel.frequency40List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW40Freq());
                if(bandsModel.frequency40List.length() > 0) {
                    bandWidthString = bandWidthString + "40, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                bandsModel.frequency80List = TextUtils.join(", ", response.getData().getBandsList().get(i).getChannelBW80Freq());
                if(bandsModel.frequency80List.length() > 0) {
                    bandWidthString = bandWidthString+"80, ";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bandWidthString.length() > 0) {
                bandWidthString = bandWidthString.substring(0, bandWidthString.length() - 2);
            }

            Log.d("PojoParserHelper", "CreateBandsList: response.getData().getBandsList().size(): "+response.getData().getBandsList().size());
            Log.d("PojoParserHelper", "run: bandWidthString: " + bandWidthString);

            bandsModel.bandwithList = TextUtils.join(", ", new String[]{bandWidthString}); // will transform the list into text8


            //add to device bands
            supportedDeviceBands.add(bandsModel);
            //add to DB if required
            if(saveToDb) {
                bandsModel.save();
            }

        }

        return supportedDeviceBands;
    }
}
