package com.sb.jeannie.utils;

public class Stopwatch {
    private long start;

    public Stopwatch() {
        init();
    }

    public void init() {
        start = System.currentTimeMillis();
    }

    public long getElapsedTimeMillis() {
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        return elapsedTimeMillis;
    }

    public String getElapsedTimeString() {
        return toString();
    }
    
    public String toString() {
    	int sec = (int)getElapsedTimeMillis()/1000;
    	StringBuilder buffer = new StringBuilder();
    	if (sec  > 0) {
    		for (int i = 0; i < sec; i++) {
    			buffer.append("*");
    		}
    	}
    	int rest = (int)getElapsedTimeMillis() - sec*1000;
    	int ds = rest/100;
    	for (int i = 0; i < ds; i++) {
    		buffer.append(".");
    	}
    	
		return "time elapsed (" + buffer.toString() + "): " + getElapsedTimeMillis() + "ms";

    }
}
