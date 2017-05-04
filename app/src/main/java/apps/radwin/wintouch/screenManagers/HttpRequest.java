package apps.radwin.wintouch.screenManagers;

import retrofit2.Call;

public class HttpRequest<T>{

    HttpInterrupts Interrupt;
    Call<T> CallRequest;
    HttpCallBack<T> CallBackInvocker;

    public HttpRequest(HttpInterrupts interrupt, HttpCallBack<T> callback){
        CallBackInvocker = callback;
        Interrupt = interrupt;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof HttpRequest)
        {
            sameSame = this.Interrupt.equals(((HttpRequest)object).Interrupt);
        }

        return sameSame;
    }
}
