package com.axxx.dps.apv.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AxxxConfUtil {

    private Log log = LogFactory.getLog(this.getClass());

    private static AxxxConfUtil instance = null;
    
    private double precomptePartenaireWebServiceDelayFixed;
    private double precomptePartenaireWebServiceDelayRandom;

    public static AxxxConfUtil getInstance() {
        return instance;
    }

    public AxxxConfUtil() {
        synchronized(AxxxConfUtil.class) {
            if (AxxxConfUtil.instance == null) {
                AxxxConfUtil.instance = this;
            }
        }
    }

    public void sleep() {
        this.sleep(this.getPrecomptePartenaireWebServiceDelayFixed());
        this.sleepRandom(this.getPrecomptePartenaireWebServiceDelayRandom());
    }
    
    private void sleep(double s) {
        if (s == 0.0) {
            return;
        }
        log.warn("Sleeping fixed...");
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep((int) (s * 1000));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.warn("Slept fixed " + duration + " ms");
    }
    
    private void sleepRandom(double s) {
        if (s == 0.0) {
            return;
        }
        log.warn("Sleeping random...");
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep((int) (s * Math.random() * 1000));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.warn("Slept fixed " + duration + " ms");
    }

    
    public double getPrecomptePartenaireWebServiceDelayFixed() {
        return precomptePartenaireWebServiceDelayFixed;
    }

    public void setPrecomptePartenaireWebServiceDelayFixed(double precomptePartenaireWebServiceDelayFixed) {
        this.precomptePartenaireWebServiceDelayFixed = precomptePartenaireWebServiceDelayFixed;
    }

    public double getPrecomptePartenaireWebServiceDelayRandom() {
        return precomptePartenaireWebServiceDelayRandom;
    }

    public void setPrecomptePartenaireWebServiceDelayRandom(double precomptePartenaireWebServiceDelayRandom) {
        this.precomptePartenaireWebServiceDelayRandom = precomptePartenaireWebServiceDelayRandom;
    }
    
}
