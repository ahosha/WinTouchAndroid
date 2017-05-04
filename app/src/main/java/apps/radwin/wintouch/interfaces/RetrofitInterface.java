package apps.radwin.wintouch.interfaces;

import java.io.IOException;

import apps.radwin.wintouch.models.CommunicationModels.AligmentActionPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.AuthenticationPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.FinalDataPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.SetBandAligmentModel;
import apps.radwin.wintouch.models.pojoModels.gpsDataPojo.GpsDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoAligmentData.AligmentDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoAuthentication.AuthenticationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoBestPosition.BestPositionPojo;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoFineAligment.FineAligmentPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetAllBands.GetBandsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetEvaluationData.EvaluationResultsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoInit.PojoInit;
import apps.radwin.wintouch.models.pojoModels.pojoLinkState.pojoLinkState;
import apps.radwin.wintouch.models.pojoModels.pojoModule.AligmentScanPojo;
import apps.radwin.wintouch.models.pojoModels.pojoSetBandCallback.PojoSetBand;
import apps.radwin.wintouch.screenManagers.HttpCommunicationService;
import apps.radwin.wintouch.screenManagers.TokenHolder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by shay_v on 10/05/2016.
 */
public interface RetrofitInterface {

    String baseUrl = "http://192.168.10.1/";

    //network calls

    @GET("api/v1/alignment/measuring")
    Call<AligmentDataPojo> getCallData();

    @GET("request")
    Call<GpsDataPojo> getGpsData();

    @GET("api/v1/alignment/best-position")
    Call<BestPositionPojo> getBestPositionCallData();

    @GET("api/v1/data/resetBoxAligment")
    Call<AligmentScanPojo> resetBoxAligmentCallData();

    @GET("api/v1/alignment/pointer-location")
    Call<CurserLocationPojo> curserLocationCallData();

    @GET("api/v1/alignment/init-values")
    Call<PojoInit> initCallData();

    @GET("api/v1/alignment/fine-alignment")
    Call<FineAligmentPojo> fineAligmentCallData();

    @GET("api/v1/alignment/get-bands")
    Call<GetBandsPojo> getAllBandsCallData();

    @GET("api/v1/alignment/link-data")
    Call<pojoLinkState> getLinkData();

    @POST("api/v1/alignment/set-band")
    Call<PojoSetBand> postAligmentSetBand(@Body SetBandAligmentModel setBandAligmentModel);

    @POST("api/v1/alignment/action/start")
    Call<PojoSetBand> postAligmentAction(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @POST("api/v1/alignment/action/startSync")
    Call<PojoSetBand> postAligmentActionstartSync(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @POST("api/v1/alignment/action/finish")
    Call<PojoSetBand> postAligmentActionFinish(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @POST("api/v1/alignment/action/complete")
    Call<PojoSetBand> postAligmentActionComplete(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @GET("api/v1/alignment/evaluation-results") //Just for mockuppurposeses we have unreal information that we send and simulate a return call
    Call<EvaluationResultsPojo> getEvaluationResults();

    @POST("api/v1/alignment/evaluation/start") //Just for mockuppurposeses we have unreal information that we send and simulate a return call
    Call<PojoSetBand> startEvaluationPost(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @POST("auth") //Just for mockuppurposeses we have unreal information that we send and simulate a return call
    Call<AuthenticationPojo> authenticationPost(@Body AuthenticationPayloadModel authenticationPayloadModel);

    @POST("api/v1/operation/deregister") //Just for mockuppurposeses we have unreal information that we send and simulate a return call
    Call<PojoSetBand> postDeregester(@Body AligmentActionPayloadModel aligmentActionPayloadModel);

    @POST("api/v1/alignment/action/set-final-data") //Just for mockuppurposeses we have unreal information that we send and simulate a return call
    Call<PojoSetBand> postFinalData(@Body FinalDataPayloadModel finalDataPayloadModel);

    class Factory {
        private static RetrofitInterface service;
        private static String ipAddress = "192.168.10.1";

        public static RetrofitInterface getInstance() {
            if (service == null) {
                OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("token", TokenHolder.getInstance().getJwtToken()).build();
                        return chain.proceed(request);
                    }
                });

                Retrofit retrofit = new Retrofit.Builder()
                        .client(builder.build())
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(RetrofitInterface.class);
                return service;
            } else {
                return service;
            }

        }

        public static RetrofitInterface getOneTimeInstance(String url) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                return retrofit.create(RetrofitInterface.class);
        }

        public static void changeApiBaseUrlWithtoken(String newIpAddress, String newToken) {

            if(newIpAddress != null && !ipAddress.equals(newIpAddress)) {
                ipAddress = newIpAddress;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ipAddress + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(RetrofitInterface.class);
            }
        }

        public static void changeApiBaseUrl(String newIpAddress) {

            if(newIpAddress != null && !ipAddress.equals(newIpAddress)) {
                ipAddress = newIpAddress;
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ipAddress + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(RetrofitInterface.class);
            }
        }
    }
}
