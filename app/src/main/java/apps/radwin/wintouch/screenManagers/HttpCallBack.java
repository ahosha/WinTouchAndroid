package apps.radwin.wintouch.screenManagers;

public interface HttpCallBack<T>{
    void invocator(HttpResponse<T> response);
}
