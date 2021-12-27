package com.upc.EasyProduction.DataPackages;

import android.util.Log;
import android.util.Pair;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * This class manages the packages of the global variables of the program that is being executed by the robot.
 * @author Enric Lamarca Ferrés
 */
public class GVarsData {
    /**
     * List of all global variables names.
     */
    private LinkedList<String> all_names = new LinkedList<String>();
    /**
     * List of all global variables values.
     */
    private LinkedList<String> all_values = new LinkedList<String>();
    /**
     * List of the global variables names entered by the user.
     */
    private LinkedList<String> names_by_user = new LinkedList<String>();
    /**
     * Auxiliary List, internal use.
     */
    private LinkedList<String> aux_values = new LinkedList<String>();
    /**
     * boolean that indicates if the user wants to see all that variables or only ones introduced.
     */
    private boolean showAllVars = false;

    /**
     * Decodes the package that contains the global variable names.
     * @param body body of the package to decode.
     * @param startIndex used when all global variables names can not be sent with only one package.
     */
    public void updateDataNames(byte[] body, int startIndex){
        // we have to be sure that all names entered by user are valid variables names

        // i suppose that the first package sent is with startIndex = 0
        if (startIndex == 0) {
            all_names = new LinkedList(Arrays.asList((new String(body)).split("\n")));
        }
        else { // if not, means that it is another message with the rest of variables!!
            all_names.addAll(Arrays.asList((new String(body)).split("\n")));
        }

        //Log.d("StartIndexNames", String.valueOf(startIndex)); // test!!
    }

    /**
     * Decodes the package that contains the global variables values.
     * @param body body of the package to decode.
     * @param startIndex used when all global variables values can not be sent with only one package.
     */
    public void updateDataValues(byte[] body, int startIndex){

        //Log.d("StartIndexValues", String.valueOf(startIndex)); // test!!

        if (startIndex == 0) {
            aux_values.clear();
        }

        // Each variable value contains type byte, data, and terminating new line character (\n character).
        // Example - two variables of type BOOL(True), and STRING("aaaa"): 0c 01 0a 03 00 04 61 61 61 61 0a

        // types:
        // https://www.universal-robots.com/articles/ur/interface-communication/remote-control-via-tcpip/

        // NONE=0 CONST_STRING=3 VAR_STRING=4 POSE=12 BOOL=13 NUM=14 INT=15 FLOAT=16 LIST=17 MATRIX=18

        int i = 0;

        while (i < body.length){

            int type = 0xff & body[i];
            i += 1;

            Pair<Integer, String> aux;

            switch (type) {

                case 0: // NONE
                    aux_values.add("NONE");
                    i += 0; // no content
                    break;

                case 3: // CONST_STRING_VAL
                case 4: // VAR_STRING_VAL
                    aux = decodeString(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 12: // POSE_VAL
                    aux = decodePose(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 13: // BOOL_VAL
                    aux = decodeBool(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 14: // NUM_VAL
                case 16: // FLOAT_VAL
                    aux = decodeNumFloat(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 15: // INT_VAL
                    aux = decodeInt(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 17: // LIST_VAL
                    // all elements have the same type
                    // can not have string lists
                    aux = decodeLists(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                case 18: // MATRIX_VAL
                    aux = decodeMatrix(body, i);
                    i = aux.first;
                    aux_values.add(aux.second);
                    break;

                default:
                    Log.d("FAIL type", String.valueOf(type));
                    break;
            }
            if (body[i] != '\n'){
                Log.d("something_wrong", String.valueOf(i)); // something went wrong...
                break;
            }
            else{
                i += 1; // jump "\n"
            }
        }

        all_values = (LinkedList<String>) aux_values.clone();

        // aux_values to avoid this exception!!:
        /*
        E/AndroidRuntime: FATAL EXCEPTION: Thread-6
            Process: com.upc.EasyProduction, PID: 8215
            java.lang.ArrayIndexOutOfBoundsException: length=109; index=109
                at java.util.LinkedList.toArray(LinkedList.java:1103)
                at com.upc.EasyProduction.DataPackages.GVarsData.getVarsValues(GVarsData.java:268)
                at com.upc.EasyProduction.GlobalVariablesActivity$4.run(GlobalVariablesActivity.java:170)
         */
    }


    /**
     * Decode a global variable of the type List.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeLists(byte[] body, int i){

        // list can be of types: boolean, float, int, pose
        // no list of lists, no list of strings
        // This script converts a value of type Boolean, Integer, Float, Pose (or a list of those types) to a string.
        // https://forum.universal-robots.com/t/handling-list-of-strings/13768

        // just in case i check if it is a string, but it can not be... i think

        String result = "[";
        String elem = "";

        int list_len = ((body[i] << 8) & 0x0000ff00) | (body[i+1] & 0x000000ff);

        i += 2;

        // for each item, 1 byte for type, and then value
        int x = 0;
        while (x < list_len){

            int type = 0xff & body[i];
            i += 1;

            Pair<Integer, String> aux;

            switch (type) {

                case 0: // NONE
                    elem = "NONE";
                    i += 0; // no content, next byte
                    break;

                case 12: // POSE_VAL
                    aux = decodePose(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                case 13: // BOOL_VAL
                    aux = decodeBool(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                case 14: // NUM_VAL
                case 16: // FLOAT_VAL
                    aux = decodeNumFloat(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                case 15: // INT_VAL
                    aux = decodeInt(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                default:
                    Log.d("FAIL type", String.valueOf(type));
                    break;
            }

            result += elem;
            x += 1;
            if (x != list_len){
                result += ", ";
            }
        }

        result += "]";

        return new Pair<Integer, String>(i, result);
    }

    /**
     * Decode a global variable of the type Matrix.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeMatrix(byte[] body, int i){

        // it seems that matrices can be of types number-> int, float, num (at least, at the moment)

        String result = "[[";
        String elem = "";

        int rows = ((body[i] << 8) & 0x0000ff00) | (body[i+1] & 0x000000ff);
        i += 2;
        int cols = ((body[i] << 8) & 0x0000ff00) | (body[i+1] & 0x000000ff);
        i += 2;

        // for each item, 1 byte for type, and then value
        int x = 0;
        int count_elem_current_row = 0;
        while (x < rows * cols){

            int type = 0xff & body[i];
            i += 1;

            Pair<Integer, String> aux;

            switch (type) {

                case 0: // NONE
                    elem = "NONE";
                    i += 0; // no content, next byte
                    break;

                case 14: // NUM_VAL
                case 16: // FLOAT_VAL
                    aux = decodeNumFloat(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                case 15: // INT_VAL
                    aux = decodeInt(body, i);
                    i = aux.first;
                    elem = aux.second;
                    break;

                default:
                    Log.d("FAIL type", String.valueOf(type));
                    break;
            }

            result += elem;
            count_elem_current_row += 1;
            x += 1;

            if (count_elem_current_row != cols){
                result += ", ";
            }
            else{
                count_elem_current_row = 0;
                if (x != rows * cols){
                    result += "], [";
                }
            }
        }

        result += "]]";

        return new Pair<Integer, String>(i, result);

    }
    /**
     * Decode a global variable of the type String.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeString(byte[] body, int i){
        int len = ((body[i] << 8) & 0x0000ff00) | (body[i+1] & 0x000000ff);
        i += 2;
        String result = new String(Arrays.copyOfRange(body, i, i+len));
        i += len;
        return new Pair<Integer, String>(i, result);
    }
    /**
     * Decode a global variable of the type Pose.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodePose(byte[] body, int i){
        String X = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;
        String Y = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;
        String Z = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;
        String Rx = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;
        String Ry = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;
        String Rz = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;

        String result = "p[" + X + ", " + Y + ", " + Z + ", " + Rx + ", " + Ry + ", " + Rz + "]";

        return new Pair<Integer, String>(i, result);
    }
    /**
     * Decode a global variable of the type Bool.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeBool(byte[] body, int i){

        String result;

        if (body[i] == 0) result = "False";
        else result = "True";

        i += 1;
        return new Pair<Integer, String>(i, result);
    }
    /**
     * Decode a global variable of the type Num or Float.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeNumFloat(byte[] body, int i){

        String result = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getFloat());
        i += 4;

        return new Pair<Integer, String>(i, result);
    }
    /**
     * Decode a global variable of the type Int.
     * @param body body of the global variables values data.
     * @param i starting index.
     * @return a pair indicating in which index terminates and the string representation of the value decoded.
     */
    private Pair<Integer, String> decodeInt(byte[] body, int i){

        String result = String.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(body, i, i + 4)).getInt());
        i += 4;

        return new Pair<Integer, String>(i, result);
    }

    /**
     * Getter of the global variables names to show.
     * @return global variables names to show (depending if the user wants to see all of them or only the introduced ones).
     */
    public String[] getVarsNames(){
        if (!showAllVars) {
            // check if all the names exists
            // order them to match the values order
            LinkedList<String> aux = new LinkedList<String>();

            for (int i = 0; i < all_names.size(); i++){
                if (names_by_user.contains(all_names.get(i))){
                    aux.add(all_names.get(i));
                }
            }
            return aux.toArray(new String[aux.size()]);
        }

        return all_names.toArray(new String[all_names.size()]);
    }
    /**
     * Getter of the global variables values to show.
     * @return global variables values to show (depending if the user wants to see all of them or only the introduced ones).
     */
    public String[] getVarsValues(){
        if (!showAllVars) {

            LinkedList<String> aux = new LinkedList<String>();
            int l;
            if (all_names.size() > all_values.size()) l = all_values.size();
            else l = all_names.size();

            for (int i = 0; i < l; i++){
                if (names_by_user.contains(all_names.get(i))){
                    aux.add(all_values.get(i));
                }
            }
            return aux.toArray(new String[aux.size()]);

        }

        return all_values.toArray(new String[all_values.size()]);
    }

    /**
     * Setter of the boolean that indicates if the user wants to see all variables or only the introduced ones.
     * @param showAllVars boolean that indicates if the user wants to see all variables or only the introduced ones.
     */
    public void setShowAllVars(boolean showAllVars){
        this.showAllVars = showAllVars;
    }

    /**
     * Getter of the boolean that indicates if the user wants to see all variables or only the introduced ones.
     * @return boolean that indicates if the user wants to see all variables or only the introduced ones.
     */
    public boolean getShowAllVars(){
        return showAllVars;
    }

    /**
     * Getter of the global variables names introduced by the user.
     * @return global variables names introduced by the user.
     */
    public LinkedList<String> getVarNamesByUser(){
        return names_by_user;
    }

    /**
     * Setter of the global variables names introduced by the user.
     * @param names_by_user global variables names introduced by the user.
     */
    public void setVarNamesByUser(LinkedList<String> names_by_user){
        this.names_by_user = names_by_user;
    }
}
