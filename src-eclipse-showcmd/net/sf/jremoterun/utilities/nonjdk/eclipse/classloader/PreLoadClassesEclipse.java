package net.sf.jremoterun.utilities.nonjdk.eclipse.classloader;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class PreLoadClassesEclipse extends net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.PreLoadClassesIde{

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	public PreLoadClassesEclipse() throws Exception{
		//skipPlugins.add("org.eclipse.osgi");
	}
	
	
	@Override
	public ClassLoader findClassLoaderForPlugin(String pluginId) {
		return EclipseClassLoaderUtilsJrr.findClassLoaderForPluginGeneric(pluginId);
	}


}
