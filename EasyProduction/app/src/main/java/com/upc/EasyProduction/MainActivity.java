package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class implements the MainActivity.
 * @author Enric Lamarca Ferrés.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Instance of the NetworkService.
     */
    private NetworkService networkService;
    /**
     * Boolean that indicates if this activity is bound to NetworkService.
     */
    private boolean bound = false; // bounded to service?
    /**
     * Robot IP.
     */
    private String robotIP;

    private Button connectButton;
    private Button disconnectButton;
    private Button stateButton;
    private Button varsButton;
    private Button msgButton;
    private Button aboutButton;
    private EditText ipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*(new Thread(){
            @Override
            public void run() {
                super.run();
                MediaPlayer player = MediaPlayer.create(MainActivity.this, Settings.System.DEFAULT_RINGTONE_URI);
                player.setLooping(true);
                player.start();
            }
        }).start();*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        connectButton = findViewById(R.id.connect_button);
        disconnectButton = findViewById(R.id.disconnect_button);
        stateButton = findViewById(R.id.state_button);
        varsButton = findViewById(R.id.global_vars);
        msgButton = findViewById(R.id.msg_button);
        aboutButton = findViewById(R.id.about);
        ipText = findViewById(R.id.ip_robot);

        if (!isMyServiceRunning(NetworkService.class)){
            ipText.setEnabled(true);
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            stateButton.setEnabled(false);
            varsButton.setEnabled(false);
            msgButton.setEnabled(false);
        }
        else{
            if (!bound) {
                doBindService();
            }
            (new Thread(){
                @Override
                public void run() {
                    while (networkService == null);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ipText.setText(networkService.getIP());
                            doUnbindService(); // important!!
                        }
                    });
                }
            }).start();

            ipText.setEnabled(false);
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            stateButton.setEnabled(true);
            varsButton.setEnabled(true);
            msgButton.setEnabled(true);
        }
    }


    public void onClickConnectButton(View v){

        robotIP = ipText.getText().toString();

        // start network service

        Intent i = new Intent(this, NetworkService.class);
        i.putExtra("ip", robotIP);

        if (!isMyServiceRunning(NetworkService.class)) {
            startService(i);
        }
        // until stop service, this service will be executed (if SO do not kill it)
        // do not mind if there are not any activity bound or if app is inactive

        if (!bound){
            doBindService(); // asynchronous!!!!
        }

        // wait until bound in another thread, avoiding blocking this one which has to execute the binding

        (new Thread(){
            @Override
            public void run() {

                // wait bind
                while (networkService == null);

                // wait socket try to connect
                while (!networkService.doWeTryToConnect());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // make sure that connection succeed
                        if (!networkService.isSocketConnected()){
                            //Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_LONG).show();
                            // The Classname. this syntax is used to refer to an outer class instance when you are using nested classes
                            // unbind and stop service!!
                            doUnbindService();
                            stopService(new Intent(MainActivity.this, NetworkService.class));
                        }
                        else{
                            ipText.setEnabled(false);
                            connectButton.setEnabled(false);
                            disconnectButton.setEnabled(true);
                            stateButton.setEnabled(true);
                            varsButton.setEnabled(true);
                            msgButton.setEnabled(true);
                        }
                        doUnbindService();
                    }
                });
            }
        }).start();
    }

    //https://stackoverflow.com/questions/22079909/android-java-lang-illegalargumentexception-service-not-registered

    /**
     * Bind the NetworkService.
     */
    public void doBindService() {
        bound = bindService(new Intent(this, NetworkService.class), connection, Context.BIND_AUTO_CREATE);
    }
    /**
     * Unbind the NetworkService.
     */
    public void doUnbindService() {
        if (bound) {
            unbindService(connection);
            bound = false;
            networkService = null;
        }
    }

    public void onClickStateButton(View v){
        Intent i = new Intent(this, RobotStateActivity.class);
        i.putExtra("ip", ipText.getText().toString());
        // our activity inherits from context
        startActivity(i);
    }

    public void onClickVarsButton(View v){
        Intent i = new Intent(this, GlobalVariablesActivity.class);
        i.putExtra("ip", ipText.getText().toString());
        // our activity inherits from context
        startActivity(i);
    }

    public void onClickDisconnectButton(View v){

        stopService(new Intent(this, NetworkService.class));

        ipText.setEnabled(true);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        stateButton.setEnabled(false);
        varsButton.setEnabled(false);
        msgButton.setEnabled(false);
    }

    public void onClickMsgButton(View v){
        Intent i = new Intent(this, MessageActivity.class);
        i.putExtra("ip", ipText.getText().toString());
        // our activity inherits from context
        startActivity(i);
    }

    public void onClickAboutButton(View v){
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkService.MyBinder binder = (NetworkService.MyBinder) service;
            networkService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // The Android system calls this when the connection to the service is unexpectedly lost,
            // such as when the service has crashed or has been killed. This is not called when the client unbinds.
            networkService = null;
        }
    };

    // https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android

    private boolean isMyServiceRunning(Class<?> serviceClass) { // eye!!
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}