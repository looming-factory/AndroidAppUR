package com.upc.EasyProduction.DataPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class manages the packages that contain tool data.
 * @author Enric Lamarca Ferrés.
 */
public class ToolData extends SubPackage {

    /**
     * Tool voltage.
     */
    private float tool_voltage_48V = -1;
    /**
     * Tool current.
     */
    private float tool_current = -1;
    /**
     * Tool temperature.
     */
    private float tool_temperature = -1;
    /**
     * Tool mode.
     */
    private int tool_mode = -1;

    /**
     * Constructor.
     */
    public ToolData(){
        this.type = 2;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        // 1 + 1 + 8 + 8

        tool_voltage_48V = ByteBuffer.wrap(Arrays.copyOfRange(body, 18, 18 + 4)).getFloat();
        tool_current = ByteBuffer.wrap(Arrays.copyOfRange(body, 23, 23 + 4)).getFloat();
        tool_temperature = ByteBuffer.wrap(Arrays.copyOfRange(body, 27, 27 + 4)).getFloat();
        tool_mode = 0xff & body[31];

    }

    /**
     * Getter of the tool voltage.
     * @return tool voltage.
     */
    public String getToolVoltageStr(){
        return String.format("%.2f", tool_voltage_48V);
    }
    /**
     * Getter of the tool current.
     * @return tool voltage.
     */
    public String getToolCurrentStr(){
        return String.format("%.2f", tool_current);
    }
    /**
     * Getter of the tool temperature.
     * @return tool voltage.
     */
    public String getToolTemperatureStr(){
        return String.format("%.2f", tool_temperature);
    }
    /**
     * Getter of the tool mode.
     * @return tool voltage.
     */
    public String getToolModeStr(){
        return String.valueOf(tool_mode);
    }
}
