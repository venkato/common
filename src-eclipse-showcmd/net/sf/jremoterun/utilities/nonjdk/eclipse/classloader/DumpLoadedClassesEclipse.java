package net.sf.jremoterun.utilities.nonjdk.eclipse.classloader;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class DumpLoadedClassesEclipse extends net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.DumpLoadedClassesIde{


	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	

	public DumpLoadedClassesEclipse(File file, long periodInSec) throws Exception{
		super(file, periodInSec);		
	}


	@Override
	public ClassLoader findClassLoaderForPlugin(String pluginId) {
		return EclipseClassLoaderUtilsJrr.findClassLoaderForPluginGeneric(pluginId);
	}

	@Override
	public Collection<String> getPlugins() {		
		return EclipseClassLoaderUtilsJrr.getPlugins();
	}

}
