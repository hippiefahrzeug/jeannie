package com.sb.jeannie.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

/** Used during tests to track all errors */
public class STErrors implements STErrorListener {
	private static final Logger LOG = LoggerFactory.getLogger(STErrors.class);
    public List<STMessage> errors = new ArrayList<STMessage>();
    
    public void compileTimeError(STMessage msg) {
        errors.add(msg);
    }

    public void runTimeError(STMessage msg) {
    	STNoSuchPropertyException e = (STNoSuchPropertyException)msg.cause;
    	LOG.error("error retrieving property: {}", msg);
    	if (e != null) {
        	LOG.error("exception caught", e.propertyName);
    	}
    }

    public void IOError(STMessage msg) {
        errors.add(msg);
    }

    public void internalError(STMessage msg) {
        errors.add(msg);
    }
    
    public void log() {
        for (STMessage m : errors) {
            LOG.error(m.toString());
        }
    }
}
