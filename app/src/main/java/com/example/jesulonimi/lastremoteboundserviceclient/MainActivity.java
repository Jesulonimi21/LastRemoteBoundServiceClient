package com.example.jesulonimi.lastremoteboundserviceclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
Messenger  recieveMesssenger, sendMessenger;
Boolean bindStatus;
int theRandomNumber;
Intent remoteIntent;
final int GETFLAG=0;
ServiceConnection serviceConnection;
TextView notInitialised;


            public class theBinder extends Handler {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    theRandomNumber=0;
                    switch(msg.what){
                        case GETFLAG:theRandomNumber=msg.arg1;
                        notInitialised.setText("the random number is "+theRandomNumber);

                    }

                }
            }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notInitialised=(TextView) findViewById(R.id.randomText);
        remoteIntent=new Intent();
        remoteIntent.setComponent(new ComponentName("com.example.jesulonimi.lastremoteboundserver","com.example.jesulonimi.lastremoteboundserver.RemoteServices"));

    }

  public void bindService(View v){

                serviceConnection=new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        sendMessenger=new Messenger(service);
                        recieveMesssenger=new Messenger(new theBinder());
                        bindStatus=true;
                        Toast.makeText(MainActivity.this,"service bounded",Toast.LENGTH_SHORT).show();
                        Log.i("execution","program executed at connection");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.i("execution","program executed at disconnection");
sendMessenger=null;
recieveMesssenger=null;
                    }
                };
                bindService(remoteIntent,serviceConnection,BIND_AUTO_CREATE);

  }
  public void unbindService(View v){
                unbindService(serviceConnection);
                bindStatus=false;
  }
  public void fetchRandomNumber(View v){
                Message requestMessage=Message.obtain(null,GETFLAG);
                requestMessage.replyTo=recieveMesssenger;

      try {
          sendMessenger.send(requestMessage);
      } catch (RemoteException e) {
          e.printStackTrace();
      }
  }
}
