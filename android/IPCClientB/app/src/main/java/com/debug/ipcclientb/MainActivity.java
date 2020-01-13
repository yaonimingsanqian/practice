package com.debug.ipcclientb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public Messenger messenger = new Messenger(new ResponseHandler(this));
    public Messenger server = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.debug.ipc");
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                ComponentName componentName = new ComponentName("com.debug.ipcserver","com.debug.ipcserver.IPCService");
                intent.setComponent(componentName);
                bindService(intent,serviceConnection,BIND_AUTO_CREATE);
            }
        });

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Message message = Message.obtain();
                    message.what = 0x10;
                    Bundle bundle = new Bundle();
                    bundle.putString("param","ClientB来了");
                    message.setData(bundle);
                    message.replyTo = messenger;
                    server.send(message);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public static class ResponseHandler extends Handler {
        private WeakReference<MainActivity> ipcMain = null;

        public final int REQUEST_PING = 0x10;

        public ResponseHandler(MainActivity ipcMain){
            this.ipcMain = new WeakReference<>(ipcMain);
        }
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case REQUEST_PING:{
                    Bundle data = message.getData();
                    String param = data.getString("param");
                    Log.d(TAG,"receive param :"+param);

                }
                default:
                    break;
            }
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"onServiceConnected");
            server = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(serviceConnection);
    }
}

