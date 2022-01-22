package net.sf.jremoterun.utilities.nonjdk.eclipse.classloader;

import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;

import net.sf.jremoterun.ClassLoaderNotFoundException;
import net.sf.jremoterun.FindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils;

public class EclipseFindParentOnlyClassLoader implements net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.IdePluginsClassloaderInfo {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	@Override
	public ClassLoader findClassLoader(Serializable classLoaderId) throws ClassLoaderNotFoundException {

		return EclipseClassLoaderUtilsJrr.findClassLoaderForPluginGeneric((String) classLoaderId);

	}

	@Override
	public Collection<String> getPlugins() throws Exception {
		return EclipseClassLoaderUtilsJrr.getPlugins();
	}

}
