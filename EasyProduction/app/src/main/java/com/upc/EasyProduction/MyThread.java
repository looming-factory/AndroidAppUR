package com.upc.EasyProduction;

// My Thread
// thread killed when run method has completed

/**
 * This class implements a subclass of thread that has an extra attribute to know when the thread has to stop.
 * @author Enric Lamarca Ferrés.
 */
public class MyThread extends Thread {
    private boolean stopped = false;

    public void myStop(){
        stopped = true;
    }

    public boolean isStopped(){
        return stopped;
    }
}
