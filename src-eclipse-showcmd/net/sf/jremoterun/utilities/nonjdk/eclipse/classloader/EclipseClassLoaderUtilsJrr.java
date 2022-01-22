package net.sf.jremoterun.utilities.nonjdk.eclipse.classloader;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class EclipseClassLoaderUtilsJrr {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	

	public static ClassLoader findClassLoaderForPluginNoCasting(String pluginId) {
		org.eclipse.osgi.internal.framework.EquinoxBundle bundle = (org.eclipse.osgi.internal.framework.EquinoxBundle) Platform
				.getBundle(pluginId);
		if(bundle==null) {
			return null;
		}
		return bundle.getModule().getCurrentRevision().getWiring().getClassLoader();
	}
	

	
	public static EquinoxClassLoader findClassLoaderForPlugin(String pluginId) {
		return (EquinoxClassLoader) findClassLoaderForPluginNoCasting(pluginId);
	}
	
	public static ClassLoader findClassLoaderForPluginGeneric(String pluginId) {
		if("org.eclipse.osgi".equals(pluginId)) {
			return EclipseClassLoaderUtilsJrr.findClassLoaderForPluginNoCasting(pluginId);	
		}
		return EclipseClassLoaderUtilsJrr.findClassLoaderForPlugin(pluginId);
	}

	
	public static List<String> getPlugins() {		
		return Arrays.asList(Platform.getExtensionRegistry().getNamespaces());
	}
}
