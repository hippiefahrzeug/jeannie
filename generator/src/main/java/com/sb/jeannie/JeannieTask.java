package com.sb.jeannie;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.resources.FileResource;

import com.sb.jeannie.utils.ClassPathExtender;

public class JeannieTask extends Task {
	private String modulePath;
	private String inputPath;
	private String outputPath;
	
	private Reference classpathref;
	private Path classpath;
	private List<Input> inputs;
	private List<Props> properties;
	
    // The method executing the task
    public void execute() throws BuildException {
    	try {
			List<File> allFiles = null;
    		if (inputs != null) {
    			allFiles = new ArrayList<File>();
    			for (Input input : inputs) {
    				List<FileSet> inputFiles = input.getInputFiles();
    				retrieveFiles(allFiles, inputFiles);
    			}
    		}
			
    		List<File> allProperties = null;
    		if (properties != null) {
    			allProperties = new ArrayList<File>();
    			for (Props i : properties) {
    				List<FileSet> inputFiles = i.getPropertiesFiles();
    				retrieveFiles(allProperties, inputFiles);
				}
    		}
    		
    		handleClassref();
    		
    		ModulesHandler mh = new ModulesHandler(
    				new File(modulePath), 
    				new File(inputPath), 
    				new File(outputPath), 
    				allProperties, 
    				null
    		);
    		mh.generateAll();
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
    }

	@SuppressWarnings("rawtypes")
	private void handleClassref() throws MalformedURLException {
		Path path = null;
		if (classpathref != null && classpathref.getReferencedObject() instanceof Path) {
			path = (Path)classpathref.getReferencedObject();
		}
		else if (classpath != null) {
			path = classpath;
		}
		if (path != null) {
			for (Iterator iterator = path.iterator(); iterator.hasNext();) {
				FileResource fileResource = (FileResource) iterator.next();
				ClassPathExtender.addURL((URLClassLoader)this.getClass().getClassLoader(), fileResource.getFile().toURI().toURL());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void retrieveFiles(List<File> allFiles, List<FileSet> inputFiles) {
		for (FileSet fileSet : inputFiles) {
			for (Iterator iterator = fileSet.iterator(); iterator.hasNext();) {
				FileResource file = (FileResource) iterator.next();
				allFiles.add(file.getFile());
			}
		}
	}
    
	public class Input {
		private List<FileSet> inputFiles = new ArrayList<FileSet>();

		public List<FileSet> getInputFiles() {
			return inputFiles;
		}
		
		public void add(FileSet fileSet) {
			inputFiles.add(fileSet);
		}
	}
	
	public class Props {
		private List<FileSet> propertiesFiles = new ArrayList<FileSet>();

		public List<FileSet> getPropertiesFiles() {
			return propertiesFiles;
		}
		
		public void add(FileSet fileSet) {
			propertiesFiles.add(fileSet);
		}
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public Reference getClasspathref() {
		return classpathref;
	}

	public void setClasspathref(Reference classpathref) {
		this.classpathref = classpathref;
	}

	public Path getClasspath() {
		return classpath;
	}

	public void setClasspath(Path classpath) {
		this.classpath = classpath;
	}

	public List<Input> getInputs() {
		return inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public List<Props> getProperties() {
		return properties;
	}

	public void setProperties(List<Props> properties) {
		this.properties = properties;
	}
}
