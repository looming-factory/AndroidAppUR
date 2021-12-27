package com.upc.EasyProduction.DataPackages;

/**
 * This class manages the packages that contain data about the robot mode.
 * @author Enric Lamarca Ferrés
 */
public class RobotModeData extends SubPackage {
    /**
     * Boolean that indicates if there is an emergency stop.
     */
    private boolean isEmergencyStopped = false;
    /**
     * Boolean that indicates if there is a protective stop.
     */
    private boolean isProtectiveStopped = false;

    /**
     * Boolean that indicates if the program is running.
     */
    private boolean isProgramRunning = false;
    /**
     * Boolean that indicates if the program is paused.
     */
    private boolean isProgramPaused = false;

    /**
     * Robot mode.
     */
    private int robotMode = 0;
    /**
     * Control mode.
     */
    private int controlMode = 0;

    /**
     * String representation of robot mode.
     */
    private String robotModeStr = "Unknown";
    /**
     * String representation of control mode.
     */
    private String controlModeStr = "Unknown";


    // redundant with robot Mode?!
    //private boolean isRealRobotConnected;
    //private boolean isRealRobotEnabled;
    //private boolean isRobotPowerOn;

    /**
     * Constructor.
     */
    public RobotModeData(){
        this.type = 0;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);
        // jump 8 bytes of timestamp
        isEmergencyStopped = body[11] != 0;
        isProtectiveStopped = body[12] != 0;

        isProgramRunning = body[13] != 0;
        isProgramPaused = body[14] != 0;

        robotMode = body[15] & 0xff;
        controlMode = body[16] & 0xff;

        updateStrings();
    }

    /**
     * Updates the robot mode string and control mode string.
     */
    public void updateStrings(){

        if (robotMode == -1) robotModeStr = "NO_CONTROLLER";
        else if (robotMode == 0) robotModeStr = "DISCONNECTED";
        else if (robotMode == 1) robotModeStr = "CONFIRM_SAFETY";
        else if (robotMode == 2) robotModeStr = "BOOTING";
        else if (robotMode == 3) robotModeStr = "POWER_OFF";
        else if (robotMode == 4) robotModeStr = "POWER_ON";
        else if (robotMode == 5) robotModeStr = "IDLE";
        else if (robotMode == 6) robotModeStr = "BACKDRIVE";
        else if (robotMode == 7) robotModeStr = "RUNNING";
        else if (robotMode == 8) robotModeStr = "UPDATING_FIRMWARE";
        else robotModeStr = "Unknown";

        if (controlMode == 0) controlModeStr = "POSITION";
        else if (controlMode == 1) controlModeStr = "TEACH";
        else if (controlMode == 2) controlModeStr = "FORCE";
        else if (controlMode == 3) controlModeStr = "TORQUE";
        else controlModeStr = "Unknown";

    }

    /**
     * Getter of the boolean that indicates if there is an emergency stop.
     * @return boolean that indicates if there is an emergency stop.
     */
    public boolean getIsEmergencyStopped(){
        return isEmergencyStopped;
    }
    /**
     * Getter of the boolean that indicates if there is a protective stop.
     * @return boolean that indicates if there is a protective stop.
     */
    public boolean getIsProtectiveStopped(){
        return isProtectiveStopped;
    }

    /**
     * Getter of the boolean that indicates if the program is running.
     * @return boolean that indicates if the program is running.
     */
    public boolean getIsProgramRunning(){
        return isProgramRunning;
    }

    /**
     * Getter of the boolean that indicates if the program is paused.
     * @return boolean that indicates if the program is paused.
     */
    public boolean getIsProgramPaused(){
        return isProgramPaused;
    }

    /**
     * Getter of the string representation of the robot mode.
     * @return the string representation of the robot mode.
     */
    public String getRobotModeStr(){
        return robotModeStr;
    }
    /**
     * Getter of the string representation of the control mode.
     * @return the string representation of the control mode.
     */
    public String getControlModeStr(){
        return controlModeStr;
    }
}
