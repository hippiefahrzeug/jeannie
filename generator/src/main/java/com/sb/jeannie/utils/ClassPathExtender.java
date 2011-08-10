package com.sb.jeannie.utils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * extends a given classloader by an URL. This is used to dynamically
 * extend the scope of loaded classes by newly dynamically created 
 * classes. (currently: scriptlets)
 * 
 * @author alvi
 */
public class ClassPathExtender {
	private static final Logger LOG = LoggerFactory.getLogger(ClassPathExtender.class);
/*
	private static URLClassLoader origClassloader = null;
	
	public static void prepare(URLClassLoader classloader) {
		origClassloader = classloader;
	}
	public static void resetClassloader() {
		if (origClassloader != null) {
			Thread.currentThread().setContextClassLoader(origClassloader);
		}
	}
*/
	
	public static void addURL(URLClassLoader classloader, URL u) {
		try {
			List<URL> urls = Arrays.asList(classloader.getURLs());
			HashSet<URL> urlSet = new HashSet<URL>(urls);
			if (urlSet.contains(u)) {
				LOG.debug("already have {}", u);
				return;
			}
			LOG.debug("adding to classpath {}", u);
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(classloader, new Object[]{ u });
		}
		catch (Throwable t) {
			LOG.error("exception caught:", t);
		}
	}
}
