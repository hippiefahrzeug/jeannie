package com.sb.jeannie;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.beans.JeannieProperties;
import com.sb.jeannie.utils.Utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogConfiguration {
	final static Logger LOG = LoggerFactory.getLogger(LogConfiguration.class);
	
	private static final String VERSION = "version";

	private static final String PRODUCTION_CFG = "logback.xml";
	private static final String VERBOSE_CFG = "logback-verbose.xml";
	private static final String DEBUG_CFG = "logback-debug.xml";
	
	private static LogConfig currentCfg = null;
	
	public enum LogConfig {
		PROD,
		VERBOSE,
		DEBUG
	}
	
	public static void configure() {
		boolean debug = Boolean.parseBoolean(JeannieProperties.getGlobalDebug());
		boolean verbose = Boolean.parseBoolean(JeannieProperties.getGlobalVerbose());

		if (debug) {
			configure(LogConfig.DEBUG);
		}
		else if (verbose) {
			configure(LogConfig.VERBOSE);
		}
		else {
			configure(LogConfig.PROD);
		}
	}

	public static void configure(LogConfig cfg) {
	    String profile = PRODUCTION_CFG;
		try {
			if (cfg.equals(currentCfg)) {
				return;
			}
			currentCfg = cfg;
		    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		    JoranConfigurator configurator = new JoranConfigurator();
		    configurator.setContext(lc);
		    InputStream cfgstr;
		    Class<LogConfiguration> clazz = LogConfiguration.class;
			if (LogConfig.DEBUG.equals(cfg)) {
				profile = DEBUG_CFG;
		    }
			else if (LogConfig.VERBOSE.equals(cfg)) {
				profile = VERBOSE_CFG;
		    }
		    else {
				profile = PRODUCTION_CFG;
		    }
		    cfgstr = clazz.getResourceAsStream(File.separatorChar + profile);
		    lc.reset();
		    lc.putProperty(VERSION, Utils.version());
		    configurator.doConfigure(cfgstr);
		}
		catch (JoranException e) {
			LOG.error("exception caught", e);
		}
		finally {
			LOG.debug("tracing profile: {}", profile);
		}
	}
}
