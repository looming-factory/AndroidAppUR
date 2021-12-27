package com.upc.EasyProduction.DataPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class manages the packages that contain joint data.
 * @author Enric Lamarca Ferrés
 */
public class JointData extends SubPackage {

    // base
    /**
     * Base position.
     */
    private double base_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Base intensity.
     */
    private float base_I_actual = -1; // 4 bytes
    /**
     * Base voltage.
     */
    private float base_V_actual = -1; // 4 bytes
    /**
     * Base temperature.
     */
    private float base_T_motor = -1; // 4 bytes
    /**
     * Base mode.
     */
    private int base_jointMode = -1; // 1 byte

    // shoulder
    /**
     * Shoulder position.
     */
    private double shoulder_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Shoulder intensity.
     */
    private float shoulder_I_actual = -1; // 4 bytes
    /**
     * Shoulder voltage.
     */
    private float shoulder_V_actual = -1; // 4 bytes
    /**
     * Shoulder temperature.
     */
    private float shoulder_T_motor = -1; // 4 bytes
    /**
     * Shoulder mode.
     */
    private int shoulder_jointMode = -1; // 1 byte

    // elbow
    /**
     * Elbow position.
     */
    private double elbow_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Elbow intensity.
     */
    private float elbow_I_actual = -1; // 4 bytes
    /**
     * Elbow voltage.
     */
    private float elbow_V_actual = -1; // 4 bytes
    /**
     * Elbow temperature.
     */
    private float elbow_T_motor = -1; // 4 bytes
    /**
     * Elbow mode.
     */
    private int elbow_jointMode = -1; // 1 byte

    // wrist1
    /**
     * Wrist1 position.
     */
    private double wrist1_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Wrist1 intensity.
     */
    private float wrist1_I_actual = -1; // 4 bytes
    /**
     * Wrist1 voltage.
     */
    private float wrist1_V_actual = -1; // 4 bytes
    /**
     * Wrist1 temperature.
     */
    private float wrist1_T_motor = -1; // 4 bytes
    /**
     * Wrist1 mode.
     */
    private int wrist1_jointMode = -1; // 1 byte

    // wrist2
    /**
     * Wrist2 position.
     */
    private double wrist2_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Wrist2 intensity.
     */
    private float wrist2_I_actual = -1; // 4 bytes
    /**
     * Wrist2 voltage.
     */
    private float wrist2_V_actual = -1; // 4 bytes
    /**
     * Wrist2 temperature.
     */
    private float wrist2_T_motor = -1; // 4 bytes
    /**
     * Wrist2 mode.
     */
    private int wrist2_jointMode = -1; // 1 byte

    // wrist3
    /**
     * Wrist3 position.
     */
    private double wrist3_q_actual = -1; // 64 bits -> 8 bytes
    /**
     * Wrist3 intensity.
     */
    private float wrist3_I_actual = -1; // 4 bytes
    /**
     * Wrist3 voltage.
     */
    private float wrist3_V_actual = -1; // 4 bytes
    /**
     * Wrist3 temperature.
     */
    private float wrist3_T_motor = -1; // 4 bytes
    /**
     * Wrist3 mode.
     */
    private int wrist3_jointMode = -1; // 1 byte

    /**
     * Constructor.
     */
    public JointData(){
        this.type = 1;
    }


    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        for (int i = 0; i < 6; i++){
            // for each joint
            // each joint has 8+8+8+4+4+4+4+1 = 41 bytes

            Double aux_q_actual = (ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41), (i * 41) + 8)).getDouble()) * 360/(2*Math.PI); // rad to º
            Float aux_I_actual = Math.abs(ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 24, (i * 41) + 24 + 4)).getFloat()); // eye abs
            Float aux_V_actual = Math.abs(ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 28, (i * 41) + 28 + 4)).getFloat()); // eye abs
            Float aux_T_motor = ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 32, (i * 41) + 32 + 4)).getFloat();
            int aux_jointMode = body[i*41 + 40] & 0xff;

            switch (i) {
                case 0:
                    base_q_actual = aux_q_actual;
                    base_I_actual = aux_I_actual;
                    base_V_actual = aux_V_actual;
                    base_T_motor = aux_T_motor;
                    base_jointMode = aux_jointMode;
                    break;
                case 1:
                    shoulder_q_actual = aux_q_actual;
                    shoulder_I_actual = aux_I_actual;
                    shoulder_V_actual = aux_V_actual;
                    shoulder_T_motor = aux_T_motor;
                    shoulder_jointMode = aux_jointMode;
                    break;
                case 2:
                    elbow_q_actual = aux_q_actual;
                    elbow_I_actual = aux_I_actual;
                    elbow_V_actual = aux_V_actual;
                    elbow_T_motor = aux_T_motor;
                    elbow_jointMode = aux_jointMode;
                    break;
                case 3:
                    wrist1_q_actual = aux_q_actual;
                    wrist1_I_actual = aux_I_actual;
                    wrist1_V_actual = aux_V_actual;
                    wrist1_T_motor = aux_T_motor;
                    wrist1_jointMode = aux_jointMode;
                    break;
                case 4:
                    wrist2_q_actual = aux_q_actual;
                    wrist2_I_actual = aux_I_actual;
                    wrist2_V_actual = aux_V_actual;
                    wrist2_T_motor = aux_T_motor;
                    wrist2_jointMode = aux_jointMode;
                    break;
                case 5:
                    wrist3_q_actual = aux_q_actual;
                    wrist3_I_actual = aux_I_actual;
                    wrist3_V_actual = aux_V_actual;
                    wrist3_T_motor = aux_T_motor;
                    wrist3_jointMode = aux_jointMode;
            }
        }
    }

    // getters

    // BASE

    /**
     * Getter of the base position.
     * @return base position.
     */
    public String getBaseQactualStr(){
        return String.format("%.2f", base_q_actual);
    }
    /**
     * Getter of the base intensity.
     * @return base intensity.
     */
    public String getBaseIactualStr(){
        return String.format("%.2f", base_I_actual);
    }
    /**
     * Getter of the base voltage.
     * @return base voltage.
     */
    public String getBaseVactualStr(){
        return String.format("%.2f", base_V_actual);
    }
    /**
     * Getter of the base temperature.
     * @return base temperature.
     */
    public String getBaseTmotorStr(){
        return String.format("%.2f", base_T_motor);
    }
    /**
     * Getter of the base mode.
     * @return base mode.
     */
    public String getBaseJointModeStr(){
        return String.valueOf(base_jointMode);
    }
    // SHOULDER
    /**
     * Getter of the shoulder position.
     * @return shoulder position.
     */
    public String getShoulderQactualStr(){
        return String.format("%.2f", shoulder_q_actual);
    }
    /**
     * Getter of the shoulder intensity.
     * @return shoulder intensity.
     */
    public String getShoulderIactualStr(){
        return String.format("%.2f", shoulder_I_actual);
    }
    /**
     * Getter of the shoulder voltage.
     * @return shoulder voltage.
     */
    public String getShoulderVactualStr(){
        return String.format("%.2f", shoulder_V_actual);
    }
    /**
     * Getter of the shoulder temperature.
     * @return shoulder temperature.
     */
    public String getShoulderTmotorStr(){
        return String.format("%.2f", shoulder_T_motor);
    }
    /**
     * Getter of the shoulder mode.
     * @return shoulder mode.
     */
    public String getShoulderJointModeStr(){
        return String.valueOf(shoulder_jointMode);
    }
    // ELBOW
    /**
     * Getter of the elbow position.
     * @return elbow position.
     */
    public String getElbowQactualStr(){
        return String.format("%.2f", elbow_q_actual);
    }
    /**
     * Getter of the elbow intensity.
     * @return elbow intensity.
     */
    public String getElbowIactualStr(){
        return String.format("%.2f", elbow_I_actual);
    }
    /**
     * Getter of the elbow voltage.
     * @return elbow voltage.
     */
    public String getElbowVactualStr(){
        return String.format("%.2f", elbow_V_actual);
    }
    /**
     * Getter of the elbow temperature.
     * @return elbow temperature.
     */
    public String getElbowTmotorStr(){
        return String.format("%.2f", elbow_T_motor);
    }
    /**
     * Getter of the elbow mode.
     * @return elbow mode.
     */
    public String getElbowJointModeStr(){
        return String.valueOf(elbow_jointMode);
    }
    // WRIST1
    /**
     * Getter of the wrist1 position.
     * @return wrist1 position.
     */
    public String getWirst1QactualStr(){
        return String.format("%.2f", wrist1_q_actual);
    }
    /**
     * Getter of the wrist1 intensity.
     * @return wrist1 intensity.
     */
    public String getWirst1IactualStr(){
        return String.format("%.2f", wrist1_I_actual);
    }
    /**
     * Getter of the wrist1 voltage.
     * @return wrist1 voltage.
     */
    public String getWirst1VactualStr(){
        return String.format("%.2f", wrist1_V_actual);
    }
    /**
     * Getter of the wrist1 temperature.
     * @return wrist1 temperature.
     */
    public String getWirst1TmotorStr(){
        return String.format("%.2f", wrist1_T_motor);
    }
    /**
     * Getter of the wrist1 mode.
     * @return wrist1 mode.
     */
    public String getWirst1JointModeStr(){
        return String.valueOf(wrist1_jointMode);
    }
    // WRIST2
    /**
     * Getter of the wrist2 position.
     * @return wrist2 position.
     */
    public String getWirst2QactualStr(){
        return String.format("%.2f", wrist2_q_actual);
    }
    /**
     * Getter of the wrist2 intensity.
     * @return wrist2 intensity.
     */
    public String getWirst2IactualStr(){
        return String.format("%.2f", wrist2_I_actual);
    }
    /**
     * Getter of the wrist2 voltage.
     * @return wrist2 voltage.
     */
    public String getWirst2VactualStr(){
        return String.format("%.2f", wrist2_V_actual);
    }
    /**
     * Getter of the wrist2 temperature.
     * @return wrist2 temperature.
     */
    public String getWirst2TmotorStr(){
        return String.format("%.2f", wrist2_T_motor);
    }
    /**
     * Getter of the wrist2 mode.
     * @return wrist2 mode.
     */
    public String getWirst2JointModeStr(){
        return String.valueOf(wrist2_jointMode);
    }
    // WRIST3
    /**
     * Getter of the wrist3 position.
     * @return wrist3 position.
     */
    public String getWirst3QactualStr(){
        return String.format("%.2f", wrist3_q_actual);
    }
    /**
     * Getter of the wrist3 intensity.
     * @return wrist3 intensity.
     */
    public String getWirst3IactualStr(){
        return String.format("%.2f", wrist3_I_actual);
    }
    /**
     * Getter of the wrist3 voltage.
     * @return wrist3 voltage.
     */
    public String getWirst3VactualStr(){
        return String.format("%.2f", wrist3_V_actual);
    }
    /**
     * Getter of the wrist3 temperature.
     * @return wrist3 temperature.
     */
    public String getWirst3TmotorStr(){
        return String.format("%.2f", wrist3_T_motor);
    }
    /**
     * Getter of the wrist3 mode.
     * @return wrist3 mode.
     */
    public String getWirst3JointModeStr(){
        return String.valueOf(wrist3_jointMode);
    }

}
