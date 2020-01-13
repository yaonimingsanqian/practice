package com.debug.ipcserver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;

public class IPCService extends Service {

    public static final String TAG = "IPCService";
    public IPCService() {
    }

    private Messenger messenger = new Messenger(new RequestHandler(this));

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"onCreate");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG,"onBind:"+intent.getStringExtra("from"));
       return messenger.getBinder();
    }
    @Override
    public boolean onUnbind(Intent intent){
        Log.d(TAG,"onUnbind:"+intent.getStringExtra("from"));
        return false;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    public static class RequestHandler extends Handler{
        private WeakReference<IPCService> ipcService = null;

        public final int REQUEST_PING = 0x10;

        public RequestHandler(IPCService ipcService){
            this.ipcService = new WeakReference<>(ipcService);
        }
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case REQUEST_PING:{
                    Bundle data = message.getData();
                    String param = data.getString("param");
                    Log.d(TAG,"receive param:"+param);

                    Messenger replyTo = message.replyTo;

                    Message reponse = Message.obtain();
                    reponse.what = 0x10;

                    Bundle reponse_data = new Bundle();
                    reponse_data.putString("param","哈哈");

                    reponse.setData(reponse_data);
                    try {
                        replyTo.send(reponse);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                default:
                    break;
            }
        }
    }
}
