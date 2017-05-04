package apps.radwin.wintouch.screenManagers;

public final class HttpResponse<T> {

    private T _data;

    public HttpResponse(T data){
        _data = data;
    }
    public T Data(){
        return _data;
    }
}
