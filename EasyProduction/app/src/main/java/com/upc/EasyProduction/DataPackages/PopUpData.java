package com.upc.EasyProduction.DataPackages;

import java.util.Arrays;

/**
 * This class manages the packages sent when the robot executes a popup command.
 * @author Enric Lamarca Ferrés
 */
public class PopUpData {
    /**
     * Boolean that indicates if there is a pending popup to notify.
     */
    private boolean pendingNotification = false;
    /**
     * Title of the popup.
     */
    private String title = "";
    /**
     * Message text of the popup.
     */
    private String message = "";

    /**
     * Boolean that indicates if the popup is a warning.
     */
    private boolean warning = false;
    /**
     * Boolean that indicates if the popup is an error.
     */
    private boolean error = false;
    /**
     * Boolean that indicates if the popup is blocking.
     */
    private boolean blocking = false;

    /**
     * Updates the data that contains this class about popup.
     * @param body data body.
     */
    public void updateData(byte[] body){

        pendingNotification = true;

        // jump 4 bytes of requestId
        // jump 4 byte of requestedType

        warning = (body[8] != 0);
        error = (body[9] != 0);
        blocking = (body[10] != 0);

        int popupMessageTitleSize = body[11] & 0xff;

        title = new String(Arrays.copyOfRange(body, 12, 12 + popupMessageTitleSize));

        message = new String(Arrays.copyOfRange(body,12 + popupMessageTitleSize, body.length));

    }

    /**
     * Getter of the title.
     * @return title.
     */
    public String getTitle(){
        String aux = "";

        if (blocking){
            aux += "Blocking ";
        }

        if (error){
            aux += "Error: ";
        }
        else if (warning){
            aux+= "Warning: ";
        }
        else{
            aux += "Message: ";
        }

        aux += title;
        return aux;
    }

    /**
     * Getter of the message.
     * @return text message.
     */
    public String getMessage(){
        return message;
    }

    /**
     * Getter of the boolean that indicates if the popup is a warning.
     * @return boolean that indicates if the popup is a warning.
     */
    public boolean getWarning(){
        return warning;
    }
    /**
     * Getter of the boolean that indicates if the popup is an error.
     * @return boolean that indicates if the popup is an error.
     */
    public boolean getError(){
        return error;
    }
    /**
     * Getter of the boolean that indicates if the popup is blocking.
     * @return boolean that indicates if the popup is blocking.
     */
    public boolean getBlocking(){
        return blocking;
    }

    /**
     * Getter of the boolean that indicates if there is a pending popup to notify.
     * @return boolean that indicates if there is a pending popup to notify.
     */
    public boolean getPendingNotification(){
        return pendingNotification;
    }
    /**
     * Setter of the boolean that indicates if there is a pending popup to notify.
     * @param pendingNotification boolean that indicates if there is a pending popup to notify.
     */
    public void setPendingNotification(boolean pendingNotification){
        this.pendingNotification = pendingNotification;
    }

}
