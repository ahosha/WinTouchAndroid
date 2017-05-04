package apps.radwin.wintouch.screenManagers;

import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import apps.radwin.wintouch.interfaces.RetrofitInterface;
import apps.radwin.wintouch.models.CommunicationModels.AligmentActionPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.AuthenticationPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.FinalDataPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.SetBandAligmentModel;
import apps.radwin.wintouch.models.pojoModels.gpsDataPojo.GpsDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoAligmentData.AligmentDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoBestPosition.BestPositionPojo;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoFineAligment.FineAligmentPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetAllBands.GetBandsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetEvaluationData.EvaluationResultsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoInit.PojoInit;
import apps.radwin.wintouch.models.pojoModels.pojoLinkState.pojoLinkState;
import apps.radwin.wintouch.models.pojoModels.pojoSetBandCallback.PojoSetBand;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HttpCommunicationService implements HttpCommunicationServiceInterface {

    private Timer communicationTimer;
    private BlockingQueue<HttpRequest> requestsQueue;
    String TAG = "httpCommunication";

    // C-TOR
    public HttpCommunicationService() {
        requestsQueue = new ArrayBlockingQueue<HttpRequest>(1024);
    }

    public void Start() {

        communicationTimer = new Timer();
        communicationTimer.schedule(timerTask, 0, 100);
    }

    public void Stop() {
        requestsQueue.clear();
        communicationTimer.cancel();
    }

    public void SetRequest(HttpInterrupts interrupt, Object setData, int retryCount, HttpCallBack callback) {

        final HttpRequest request = GenerateSetRequest(interrupt, setData, callback);

        if (request == null)
            return;

        request.CallRequest.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    request.CallBackInvocker.invocator(new HttpResponse(response.body()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                try {
                    request.CallBackInvocker.invocator(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private HttpRequest GenerateSetRequest(HttpInterrupts interrupt, Object setData, HttpCallBack callback) {

        HttpRequest request = null;

        switch (interrupt) {

            case SET_START_ALIGNMENT:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<AligmentActionPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().postAligmentAction((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_DEREGISTER:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<PojoSetBand>(interrupt, callback);
                    request.CallRequest = getRetrofit().postDeregester((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_FINISH_ALIGNMENT:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<AligmentActionPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().postAligmentActionFinish((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_FINAL_DATA:
                if (setData instanceof FinalDataPayloadModel) {
                    request = new HttpRequest<PojoSetBand>(interrupt, callback);
                    request.CallRequest = getRetrofit().postFinalData((FinalDataPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;


            case SET_COMPLETE:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<AligmentActionPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().postAligmentActionComplete((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_START_SYNC:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<AligmentActionPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().postAligmentActionstartSync((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_START_EVALUATION:
                if (setData instanceof AligmentActionPayloadModel) {
                    request = new HttpRequest<AligmentActionPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().startEvaluationPost((AligmentActionPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

            case SET_BAND:
                if (setData instanceof SetBandAligmentModel) {
                    request = new HttpRequest<PojoSetBand>(interrupt, callback);
                    request.CallRequest = getRetrofit().postAligmentSetBand((SetBandAligmentModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }

            case SET_AUTH:
                if (setData instanceof AuthenticationPayloadModel) {
                    request = new HttpRequest<AuthenticationPayloadModel>(interrupt, callback);
                    request.CallRequest = getRetrofit().authenticationPost((AuthenticationPayloadModel) setData);
                    request.CallBackInvocker = callback;
                    break;
                }
                break;

        }

        return request;
    }

    public void GetUrlRequest(String url, HttpCallBack callback) {
        HttpRequest<GpsDataPojo> gpsDataRequest = new HttpRequest<GpsDataPojo>(HttpInterrupts.GET_ADDRESS, callback);
        gpsDataRequest.CallRequest = GenerateSingleRequest(url).getGpsData();
        gpsDataRequest.CallBackInvocker = callback;
        AddToQueue(gpsDataRequest);
    }


    public void GetRequest(HttpInterrupts interrupt, int retryCount, HttpCallBack callback, Context context) {

        switch (interrupt) {
            case FINE_ALIGNMENT:
                HttpRequest<FineAligmentPojo> fineAlignment = new HttpRequest<FineAligmentPojo>(interrupt, callback);
                fineAlignment.CallRequest = getRetrofit().fineAligmentCallData();
                fineAlignment.CallBackInvocker = callback;
                AddToQueue(fineAlignment);
                break;

            case EVALUATION_RESULTS:

                HttpRequest<EvaluationResultsPojo> elevationReq = new HttpRequest<EvaluationResultsPojo>(interrupt, callback);
                elevationReq.CallRequest = getRetrofit().getEvaluationResults();
                elevationReq.CallBackInvocker = callback;
                AddToQueue(elevationReq);


                break;

            case ALIGNMENT_DATA:

                HttpRequest<AligmentDataPojo> alignmentData = new HttpRequest<AligmentDataPojo>(interrupt, callback);
                alignmentData.CallRequest = getRetrofit().getCallData();
                alignmentData.CallBackInvocker = callback;
                AddToQueue(alignmentData);


                break;

            case BEST_POSITION:
                HttpRequest<BestPositionPojo> bestPosition = new HttpRequest<BestPositionPojo>(interrupt, callback);
                bestPosition.CallRequest = getRetrofit().getBestPositionCallData();
                bestPosition.CallBackInvocker = callback;
                AddToQueue(bestPosition);
                break;

            case BANDS_LIST:
                HttpRequest<GetBandsPojo> bandsListReq = new HttpRequest<GetBandsPojo>(interrupt, callback);
                bandsListReq.CallRequest = getRetrofit().getAllBandsCallData();
                bandsListReq.CallBackInvocker = callback;
                AddToQueue(bandsListReq);
                break;

            case LINK_STATE:
                HttpRequest<pojoLinkState> linkStateReq = new HttpRequest<pojoLinkState>(interrupt, callback);
                linkStateReq.CallRequest = getRetrofit().getLinkData();
                linkStateReq.CallBackInvocker = callback;
                AddToQueue(linkStateReq);

            case COURSER_LOCATION:

                HttpRequest<CurserLocationPojo> courserReq = new HttpRequest<CurserLocationPojo>(interrupt, callback);
                courserReq.CallRequest = getRetrofit().curserLocationCallData();
                courserReq.CallBackInvocker = callback;
                AddToQueue(courserReq);


                break;

            case SET_INIT:
                HttpRequest<PojoInit> initReq = new HttpRequest<PojoInit>(interrupt, callback);
                initReq.CallRequest = getRetrofit().initCallData();
                initReq.CallBackInvocker = callback;
                AddToQueue(initReq);
                break;
        }
    }

    ;

    private RetrofitInterface GenerateSingleRequest(String url) {
        return RetrofitInterface.Factory.getOneTimeInstance(url);
    }

    private void AddToQueue(HttpRequest bandsListReq) {

        if (!requestsQueue.contains(bandsListReq)) {
            requestsQueue.add(bandsListReq);
        }
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

            if (requestsQueue.size() == 0)
                return;

            final HttpRequest request;
            request = requestsQueue.remove();


            request.CallRequest.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        request.CallBackInvocker.invocator(new HttpResponse(response.body()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    try {
                        Log.d("HttpCommunicationSrv", t.getMessage());
                        request.CallBackInvocker.invocator(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private RetrofitInterface getRetrofit() {
        return RetrofitInterface.Factory.getInstance();
    }
}


