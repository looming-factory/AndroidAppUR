package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.upc.EasyProduction.DataPackages.GVarsData;

/**
 * This class implements the GlobalVariablesActivity.
 * @author Enric Lamarca Ferrés.
 */
public class GlobalVariablesActivity extends AppCompatActivity {

    /**
     * Instance of the NetworkService.
     */
    private NetworkService networkService;
    /**
     * Boolean that indicates if this activity is bound to NetworkService.
     */
    private boolean bound = false;

    /**
     * Thread that updates the values.
     */
    private Thread updatingValuesThread;

    private Button addButton;
    private Button delButton;
    private EditText varName;
    private TextView vars;
    private TextView varsByUser;
    private Button allVars;

    /**
     * Boolean that indicates if the user wants to see all global variables.
     */
    private boolean showAllVars = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_variables);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to NetworkService
        if (!bound) { // pre: service is started!! it is not possible to reach this activity without starting service
            doBindService(); // asynchronous!!
        }

        vars = findViewById(R.id.vars);
        vars.setMovementMethod(new ScrollingMovementMethod());

        varsByUser = findViewById(R.id.vars_by_user);
        varsByUser.setMovementMethod(new ScrollingMovementMethod());

        addButton = findViewById(R.id.add);
        delButton = findViewById(R.id.delete);
        varName = findViewById(R.id.var_name);
        allVars = findViewById(R.id.all_vars);


        (new Thread(){
            @Override
            public void run() {
                while (networkService == null);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(networkService.getVarNamesSize() == 0){
                            delButton.setEnabled(false);
                        }
                        else{
                            delButton.setEnabled(true);
                        }

                        showAllVars = networkService.getShowAllVars();

                        if (showAllVars){
                            allVars.setText("SHOW MY VARIABLES");
                        }
                        else{
                            allVars.setText("SHOW ALL VARIABLES");
                        }
                    }
                });
            }
        }).start();

        startUpdatingValues();

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

    public void onClickAddButton(View v){
        // to be sure not blocking anything... new thread
        if (varName.getText().toString().length() > 0) {
            (new Thread() {
                @Override
                public void run() {
                    while (networkService == null) ; // just to make sure

                    networkService.addVarName(varName.getText().toString());

                }
            }).start();

            varName.setText("");
            delButton.setEnabled(true);
            varsByUser.scrollTo(0, 0);
        }

    }
    public void onClickDelButton(View v){

        // to be sure not blocking anything... new thread
        (new Thread(){
            @Override
            public void run() {
                while (networkService == null);
                networkService.delVarName(varName.getText().toString()); // just to make sure

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (networkService.getVarNamesSize() == 0){
                            delButton.setEnabled(false);
                        }
                    }
                });

            }
        }).start();

        varName.setText("");
        varsByUser.scrollTo(0, 0);

    }

    public void onClickAllVars(View v){

        showAllVars =! showAllVars;

        if (showAllVars){
            allVars.setText("SHOW MY VARIABLES");
        }
        else{
            allVars.setText("SHOW ALL VARIABLES");
        }

        (new Thread() {
            @Override
            public void run() {
                while (networkService == null) ; // just to make sure

                networkService.setShowAllVars(showAllVars);

            }
        }).start();

        vars.scrollTo(0, 0);
    }

    /**
     * Starts updating values.
     */
    private void startUpdatingValues() {
        // make sure that we are bound

        updatingValuesThread = (new MyThread() {
            @Override
            public void run() {
                super.run();

                while (networkService == null) ; // just to make sure

                GVarsData gvData = networkService.getGlobalVariablesData();

                while (bound) { // if stop activity then we do unbind

                    String[] names = gvData.getVarsNames();
                    String[] values = gvData.getVarsValues();
                    String[] names_by_user = networkService.getVarNamesByUser();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String aux = "";

                            for (int i = 0; i< names_by_user.length; i++){
                                aux += names_by_user[i];
                                if (i != names_by_user.length - 1){
                                    aux += ", ";
                                }
                            }

                            varsByUser.setText("Your variables: " + aux);


                            if (names.length > 0 && values.length > 0) {
                                aux = "";
                                int l;
                                if (names.length > values.length) l = values.length;
                                else l = names.length;

                                for (int i = 0; i < l; i++) {

                                    aux += names[i] + " = " + values[i] + "\n";

                                }
                                vars.setText(aux);
                            }
                            else{
                                vars.setText("");
                            }
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
}