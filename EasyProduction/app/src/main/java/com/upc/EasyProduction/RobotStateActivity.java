package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.upc.EasyProduction.Communication.DashBoardConnection;
import com.upc.EasyProduction.DataPackages.JointData;
import com.upc.EasyProduction.DataPackages.MasterBoardData;
import com.upc.EasyProduction.DataPackages.RobotModeData;
import com.upc.EasyProduction.DataPackages.ToolData;

/**
 * This class implements RobotStateActivity.
 * @author Enric Lamarca Ferrés.
 */
public class RobotStateActivity extends AppCompatActivity {

    /**
     * Instance of the NetworkService.
     */
    private NetworkService networkService;
    /**
     * Boolean that indicates if this activity is bound to NetworkService.
     */
    private boolean bound = false; // bounded to service?

    /**
     * Instance of DashBoardConnection.
     */
    private DashBoardConnection db;
    /**
     * Robot IP.
     */
    private String robotIP;

    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button guideButton;

    private TextView programState;
    private TextView emergProtStop;
    private ColorStateList oldColors;

    private TextView robotMode;
    private TextView controlMode;

    private TextView base;
    private TextView shoulder;
    private TextView elbow;
    private TextView wrist1;
    private TextView wrist2;
    private TextView wrist3;
    private TextView tool;
    private TextView master_board;

    /**
     * Thread that updates the values.
     */
    private Thread updatingValuesThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_state);

        /*Intent i = getIntent();
        robotIP = i.getStringExtra("ip");
        db = new DashBoardConnection(robotIP);*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        // bind to NetworkService
        if (!bound) { // pre: service is started!! it is not possible to reach this activity without starting service
            doBindService(); // asynchronous!!
        }
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);
        guideButton = findViewById(R.id.guide);

        programState = findViewById(R.id.program_state);
        emergProtStop = findViewById(R.id.stopped);
        oldColors = emergProtStop.getTextColors();

        robotMode = findViewById(R.id.robot_mode);
        controlMode = findViewById(R.id.control_mode);

        base = findViewById(R.id.base);
        shoulder = findViewById(R.id.shoulder);
        elbow = findViewById(R.id.elbow);
        wrist1 = findViewById(R.id.wirst1);
        wrist2 = findViewById(R.id.wirst2);
        wrist3 = findViewById(R.id.wirst3);
        tool = findViewById(R.id.tool);
        master_board = findViewById(R.id.master_board);

        startUpdatingValues();

        (new Thread() {
            @Override
            public void run() {
                while (networkService == null) ;

                db = new DashBoardConnection(networkService.getIP());
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
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

    public void onClickPlayButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.play();
                }
                db.close();
            }
        }).start();

    }

    public void onClickPauseButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.pause();
                }
                db.close();
            }
        }).start();

    }

    public void onClickStopButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.stop();
                }
                db.close();
            }
        }).start();

    }

    public void onClickGuideButton(View v){
        Intent i = new Intent(this, GuideActivity.class);
        startActivity(i);
    }

    /**
     * Starts updating values.
     */
    private void startUpdatingValues(){
        // make sure that we are bound

        updatingValuesThread = (new MyThread(){
            @Override
            public void run() {
                super.run();

                while (networkService == null);

                while (bound){ // if stop activity then we do unbind

                    RobotModeData rmData = networkService.getRobotModeData();
                    JointData jData = networkService.getJointData();
                    ToolData tData = networkService.getToolData();
                    MasterBoardData mbData = networkService.getMasterBoardData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // ROBOT MODE DATA
                            if (rmData.getIsProgramRunning()) {
                                programState.setText("programState: RUNNING");
                            }
                            else if (rmData.getIsProgramPaused()){
                                programState.setText("programState: PAUSED");
                            }
                            else {
                                programState.setText("programState: STOPPED");
                            }

                            if (rmData.getIsEmergencyStopped()){ // test in real robot
                                emergProtStop.setText("EMERGENCY STOP");
                                emergProtStop.setTextColor(Color.RED);
                            }
                            else if (rmData.getIsProtectiveStopped()){ // test in real robot
                                emergProtStop.setText("PROTECTIVE STOP");
                                emergProtStop.setTextColor(Color.RED);
                            }
                            else{
                                emergProtStop.setText("");
                                emergProtStop.setTextColor(oldColors);
                            }

                            robotMode.setText("robotMode: " + rmData.getRobotModeStr());
                            controlMode.setText("controlMode: " + rmData.getControlModeStr());

                            // JOINT DATA

                            String aux = jData.getBaseQactualStr() +"º\n" + jData.getBaseVactualStr() + "V\n"
                                    + jData.getBaseIactualStr() + "A\n" + jData.getBaseTmotorStr() + "ºC\n" + jData.getBaseJointModeStr();
                            base.setText(aux);

                            aux = jData.getShoulderQactualStr() +"º\n" + jData.getShoulderVactualStr() + "V\n"
                                    + jData.getShoulderIactualStr() + "A\n" + jData.getShoulderTmotorStr() + "ºC\n" + jData.getShoulderJointModeStr();
                            shoulder.setText(aux);

                            aux = jData.getElbowQactualStr() +"º\n" + jData.getElbowVactualStr() + "V\n"
                                    + jData.getElbowIactualStr() + "A\n" + jData.getElbowTmotorStr() + "ºC\n" + jData.getElbowJointModeStr();
                            elbow.setText(aux);

                            aux = jData.getWirst1QactualStr() +"º\n" + jData.getWirst1VactualStr() + "V\n"
                                    + jData.getWirst1IactualStr() + "A\n" + jData.getWirst1TmotorStr() + "ºC\n" + jData.getWirst1JointModeStr();
                            wrist1.setText(aux);

                            aux = jData.getWirst2QactualStr() +"º\n" + jData.getWirst2VactualStr() + "V\n"
                                    + jData.getWirst2IactualStr() + "A\n" + jData.getWirst2TmotorStr() + "ºC\n" + jData.getWirst2JointModeStr();
                            wrist2.setText(aux);

                            aux = jData.getWirst3QactualStr() +"º\n" + jData.getWirst3VactualStr() + "V\n"
                                    + jData.getWirst3IactualStr() + "A\n" + jData.getWirst3TmotorStr() + "ºC\n" + jData.getWirst3JointModeStr();
                            wrist3.setText(aux);

                            // TOOL DATA

                            aux = tData.getToolVoltageStr() + "V\n" + tData.getToolCurrentStr() + "A\n" + tData.getToolTemperatureStr() + "ºC\n"
                                    + tData.getToolModeStr();
                            tool.setText(aux);

                            // MASTER BOARD DATA

                            aux = mbData.getMasterBoardTemperatureStr() + "ºC\n" + mbData.getRobotVoltageStr() + "V\n" + mbData.getRobotCurrentStr() + "A";
                            master_board.setText(aux);

                        }
                    });
                    try {
                        this.sleep(500);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        updatingValuesThread.start();
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

    // NOTES:
    // If your client is still bound to a service when your app destroys the client, destruction causes the client to unbind.
    // It is better practice to unbind the client as soon as it is done interacting with the service.
    // Doing so allows the idle service to shut down.

}