package apps.radwin.wintouch.screenManagers;

import android.content.Context;

public interface HttpCommunicationServiceInterface
{
    void Start();
    void Stop();
    void GetRequest(HttpInterrupts interrupt, int retryCount, HttpCallBack callback, Context context);
    void SetRequest(HttpInterrupts interrupt, Object setData, int retryCount, HttpCallBack callback);
}
