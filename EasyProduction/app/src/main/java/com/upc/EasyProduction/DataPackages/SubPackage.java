package com.upc.EasyProduction.DataPackages;

/**
 * This class is a superclass for robot sate subpackages.
 * @author Enric Lamarca Ferrés
 */
public class SubPackage {

    /**
     * Subpackage type.
     */
    protected int type;

    /**
     * Getter of the subpackage type.
     * @return subpackage type.
     */
    public int getType(){
        return type;
    }

    /**
     * Updates data of subpackage.
     * @param body data body.
     */
    public void updateData(byte[] body){}

}
