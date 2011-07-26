package com.sb.jeannie;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.Misc;
import org.stringtemplate.v4.misc.STMessage;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

/** Used during tests to track all errors */
public class ErrorBuffer implements STErrorListener {
	private static final Logger LOG = LoggerFactory.getLogger(ErrorBuffer.class);
    public List<STMessage> errors = new ArrayList<STMessage>();
    
    public void compileTimeError(STMessage msg) {
        errors.add(msg);
    }

    public void runTimeError(STMessage msg) {
    	STNoSuchPropertyException e = (STNoSuchPropertyException)msg.cause;
    	LOG.error("error retrieving property: {}", e.propertyName);
    }

    public void IOError(STMessage msg) {
        errors.add(msg);
    }

    public void internalError(STMessage msg) {
        errors.add(msg);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (STMessage m : errors) {
            buf.append(m.toString()+Misc.newline);
        }
        return buf.toString();
    }
}
