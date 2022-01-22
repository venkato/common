package net.sf.jremoterun.utilities.nonjdk.eclipse.classloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.container.Module;
import org.eclipse.osgi.container.ModuleContainer;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.FrameworkWiring;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;

public class EclipseClassLoaderUtilsJrr {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static String osgiClassLoaderId = "org.eclipse.osgi";

	public static ClassLoader findClassLoaderForPluginNoCasting(String pluginId) {
		org.eclipse.osgi.internal.framework.EquinoxBundle bundle = (org.eclipse.osgi.internal.framework.EquinoxBundle) Platform
				.getBundle(pluginId);
		if (bundle == null) {
			return null;
		}
		return bundle.getModule().getCurrentRevision().getWiring().getClassLoader();
	}

	public static EquinoxClassLoader findClassLoaderForPlugin(String pluginId) {
		return (EquinoxClassLoader) findClassLoaderForPluginNoCasting(pluginId);
	}

	public static ClassLoader findClassLoaderForPluginGeneric(String pluginId) {
		if (osgiClassLoaderId.equals(pluginId)) {
			return EclipseClassLoaderUtilsJrr.findClassLoaderForPluginNoCasting(pluginId);
		}
		return findClassLoaderForPlugin(pluginId);
	}

	public static int stateContainsIgnore = Bundle.INSTALLED | Bundle.UNINSTALLED;
	public static int stateContainsGood = 0;
	// public static int goodBandle = Bundle.INSTALLED | Bundle.UNINSTALLED;

	public static Collection<String> getPlugins() throws Exception {
		// return Arrays.asList(Platform.getExtensionRegistry().getNamespaces());
		FrameworkWiring frameworkWiring = (FrameworkWiring) JrrClassUtils.getFieldValue(InternalPlatform.getDefault(), "fwkWiring");
		ModuleContainer moduleContainer = (ModuleContainer) JrrClassUtils.getFieldValueR(new ClRef("org.eclipse.osgi.container.ModuleContainer$ContainerWiring"),frameworkWiring, "this$0");
		List<Module> modules = moduleContainer.getModules();
		List<String> ids = new ArrayList<String>();
		for (Module module : modules) {
			boolean isgood = true;
			Bundle bundle = module.getBundle();
			int state = bundle.getState();
			if (stateContainsIgnore > 0) {
				if ((state & stateContainsIgnore) > 0) {
					isgood = false;
				}
			}
			if (stateContainsGood > 0) {
				if ((state & stateContainsGood) > 0) {
					
				}else {
					isgood = false;
				}
			}
			if (isgood) {
				ids.add(bundle.getSymbolicName());
			}
		}
		if (ids.size() == 0) {
			throw new Exception("not found from " + modules.size());
		}

		return ids;
	}
}
