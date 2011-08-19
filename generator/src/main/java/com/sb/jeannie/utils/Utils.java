package com.sb.jeannie.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.beans.JeannieProperties;

public class Utils {
    private final static Logger LOG = LoggerFactory.getLogger(Utils.class);
    public static String NL = System.getProperty("line.separator");
    public static String DEFAULTCHARSET = "ISO-8859-1";

    public static final String PROPERTY_ENCODING = "encoding";

    public static Charset getEncoding() {
        Charset encoding = null;
		String charset = JeannieProperties.getGlobalEncoding();
    	try {
			encoding = Charset.forName(charset);
    	}
    	catch (Exception e) {
    		LOG.error("exception caught", e);
    	}
    	finally {
    		if (encoding == null) {
    			LOG.error("charset '" + charset + "' not supported, setting to: '" + DEFAULTCHARSET + "'");
    			encoding = Charset.forName(DEFAULTCHARSET);
    		}
    	}
    	return encoding;
    }

	public static List<File> allfiles(File path) {
		return allfiles(path, (String)null);
	}
	
	/**
	 * return a list of all files found inside 'path'. If
	 * 'extension' is not null, only returns files that end 
	 * with the extension-string.
	 * 
	 * If path is a plain file, a list with only that file 
	 * is returned.
	 * 
	 * @param path
	 * @param extension
	 * @return never returns null
	 */
	public static List<File> allfiles(File path, String extension) {
		ArrayList<File> files = new ArrayList<File>();
		allfiles(files, path, extension, null);
		return files;
	}
	
	public static List<File> allfiles(File path, Set<String> ignore) {
		ArrayList<File> files = new ArrayList<File>();
		allfiles(files, path, null, ignore);
		return files;
	}

	private static void allfiles(
			List<File> files, 
			File path, 
			String extension,
			Set<String> ignore
	) {
		if (!path.exists()) {
			return;
		}
		else if (ignore != null && ignore.contains(path.getName())) {
			return;
		}
		else if (path.isDirectory()) {
			File[] listFiles = path.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				allfiles(files, file, extension, ignore);
			}
		}
		else if (path.isFile()) {
			if (extension == null || path.getName().endsWith(extension)) {
				files.add(path);
			}
		}
	}
	
	/**
	 * deletes all plain files within path if it is a directory
	 * deletes file, if path is a plain file
	 * 
	 * @param path
	 */
	public static void deleteAll(File path) {
		List<File> files = allfiles(path);
		for (File file : files) {
			file.delete();
		}
	}
	
    public static String loadFileAsString(File file) {
    	List<String> cont = loadFile(file);
    	StringBuilder sb = new StringBuilder();
    	for (String line : cont) {
        	sb.append(line);
        	sb.append(NL);
		}
    	return sb.toString();
    }

    public static List<String> loadFile(File file) {
		BufferedReader br = null;
		try {
			if (!file.isFile()) {
				return null;
			}
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, getEncoding());
			br = new BufferedReader(isr);
			String s = null;
			List<String> lines = new ArrayList<String>();
			while ((s = br.readLine()) != null) {
				lines.add(s);
			}
			return lines;
		}
		catch (IOException e) {
			LOG.error("exception caught", e);
			return new ArrayList<String>();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					LOG.error("exception caught", e);
				}
			}
		}
    }
    
    public static String fileExtension(File file) {
    	String name = file.getName();
    	int lastIndexOf = name.lastIndexOf('.');
    	if (lastIndexOf == -1 || lastIndexOf == name.length()) {
    		return "";
    	}
    	return name.substring(lastIndexOf+1);
    }

    public static <T> T nvl(T in, T def) {
    	if(in == null) {
    		return def;
    	}
    	return in;
    }
    
	public static boolean extract(File path, File dest) {
		LOG.debug("extracting {} to {}", path, dest);
		dest.mkdirs();
		int n = 0;
		try {
			JarFile jar = new JarFile(path);
			Enumeration<JarEntry> enumer = jar.entries();
			while (enumer.hasMoreElements()) {
				JarEntry file = (JarEntry) enumer.nextElement();
				File f = new File(dest.getAbsolutePath() + File.separator + file.getName());
				if (file.isDirectory()) {
					f.mkdir();
					continue;
				}
				
				n++;
				InputStream is = jar.getInputStream(file); // get the input stream
				BufferedInputStream in = new BufferedInputStream(is);
	            OutputStream fout= new FileOutputStream(f);
	            BufferedOutputStream bout= new BufferedOutputStream(fout);
				byte[] buf = new byte[1000];
				int s = in.read(buf);
				while (s > 0) {
					bout.write(buf, 0, s);
					s = in.read(buf);
				}
				bout.close();
				in.close();
				is.close();
			}
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * convert a duration measured in miliseconds to a human
	 * readable string
	 */
	public static String ms2time(long d) {
		long s = d/1000;
		long ms = d - s * 1000;
		long m = s/60;
		s = s - m * 60;
		long h = m / 60;
		m = m - h * 60;
		
		if (h > 0) {
			return String.format("%dh:%dm:%ds.%d", h, m, s, ms);
		}
		else if (m > 0) {
			return String.format("%dm:%ds.%d", m, s, ms);
		}
		else if (s > 0) {
			return String.format("%ds.%d", s, ms);
		}
		return String.format("%d", ms);
	}
	
	public static String sha1(String contents) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			md.update(contents.getBytes());
			byte[] hash = md.digest();
			return byteArray2Hex(hash);
		}
		catch (Exception e) {
			LOG.error("exception caught", e);
			return null;
		}
	}
	
	private static String byteArray2Hex(byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    return formatter.toString();
	}
	
	public static String version() {
		URL resource = Utils.class.getClassLoader().getResource("buildtime.properties");
		Properties p = new Properties();
		String version;
		try {
			p.load(resource.openStream());
			version = p.getProperty("version");
		}
		catch (IOException e) {
			version = "?";
		}
		return version;
	}
	
	/* kinda doesn't work
	public static String version() {
		Class<Utils> clazz = Utils.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
		  return "";
		}
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + 
		    "/META-INF/MANIFEST.MF";
		Manifest manifest;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		}
		catch (Exception e) {
			  return "";
		}
		Attributes attr = manifest.getMainAttributes();
		if (attr == null) {
			return "";
		}
		Object v = attr.get("Implementation-Version");
		return "" + v;
	}
	*/
}
