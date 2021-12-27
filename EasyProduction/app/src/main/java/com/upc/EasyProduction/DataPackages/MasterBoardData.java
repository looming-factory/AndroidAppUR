package com.upc.EasyProduction.DataPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class manages the packages that contain master board data.
 * @author Enric Lamarca Ferrés
 */
public class MasterBoardData extends SubPackage {
    /**
     * Master board temperature.
     */
    private float master_board_temperature;
    /**
     * Robot voltage.
     */
    private float robot_voltage_48V;
    /**
     * Robot current.
     */
    private float robot_current;

    /**
     * Constructor.
     */
    public MasterBoardData(){
        this.type = 3;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        // 4 + 4 + 1 + 1 + 8 + 8 + 1 + 1 + 8 + 8

        master_board_temperature = ByteBuffer.wrap(Arrays.copyOfRange(body, 44, 44 + 4)).getFloat();
        robot_voltage_48V = ByteBuffer.wrap(Arrays.copyOfRange(body, 48, 48 + 4)).getFloat();
        robot_current = ByteBuffer.wrap(Arrays.copyOfRange(body, 52, 52 + 4)).getFloat();

    }

    /**
     * Getter of master board temperature.
     * @return master board temperature.
     */
    public String getMasterBoardTemperatureStr(){
        return String.format("%.2f", master_board_temperature);
    }
    /**
     * Getter of robot voltage.
     * @return robot voltage.
     */
    public String getRobotVoltageStr(){
        return String.format("%.2f", robot_voltage_48V);
    }
    /**
     * Getter of robot current.
     * @return robot current.
     */
    public String getRobotCurrentStr(){
        return String.format("%.2f", robot_current);
    }
}
