package com.sb.jeannie;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogConfiguration {
	final static Logger LOG = LoggerFactory.getLogger(LogConfiguration.class);
	
	private static final String PRODUCTION = "/logback.xml";
	private static final String VERBOSE = "/logback-verbose.xml";
	private static final String DEBUG = "/logback-debug.xml";
	
	public enum LogConfig {
		info,
		verbose,
		debug
	}
	
	public static void configure(LogConfig cfg) {
	    String profile = PRODUCTION;
		try {
		    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		    JoranConfigurator configurator = new JoranConfigurator();
		    configurator.setContext(lc);
		    InputStream cfgstr;
		    Class<LogConfiguration> clazz = LogConfiguration.class;
			if (LogConfig.verbose.equals(cfg)) {
				profile = VERBOSE;
		    }
		    if (LogConfig.debug.equals(cfg)) {
				profile = DEBUG;
		    }
		    else {
				profile = PRODUCTION;
		    }
		    cfgstr = clazz.getResourceAsStream(profile);
		    lc.reset();
		    configurator.doConfigure(cfgstr);
		}
		catch (JoranException e) {
			LOG.error("exception caught", e);
		}
		finally {
			LOG.info("tracing profile: {}", profile);
		}
	}
}
