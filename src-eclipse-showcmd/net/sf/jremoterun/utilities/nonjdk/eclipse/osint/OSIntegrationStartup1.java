package net.sf.jremoterun.utilities.nonjdk.eclipse.osint;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.SharedObjectsUtils;


public class OSIntegrationStartup1  {

	private static final Logger log = Logger.getLogger(OSIntegrationStartup1.class.getName());

	public static OSIntegrationStartup1 instance;
	
	
//	public static final IWorkbench workbench = PlatformUI.getWorkbench();

//	public static final Display display = workbench.getDisplay();

	public static String txtEditorId=PlatformUI.getWorkbench().getEditorRegistry()
			.getDefaultEditor("a.txt").getId();

	public static IWorkspace workspace=ResourcesPlugin.getWorkspace();

	public static final String istyOSIntegrationClassLoaderId = "istyOSIntegrationClassLoaderId";

	public static volatile boolean initialized = false;

	
	
	public OSIntegrationStartup1() {
		if(instance==null) {
			instance=this;
		}else {
			log.severe("OSIntegrationStartup already initialized");
		}
	}

	public static void workBenchToFront() {
		final Shell shell = getWorkbenchWindow().getShell();
		shell.forceActive();
	}

	public static OSIntegrationStartup1 getInstance() {
		if(instance==null) {
			throw new IllegalStateException("OSIntegrationStartup not initialized");
		}
		return instance;
	}
	
	public static IWorkbenchWindow getWorkbenchWindow() {
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0];
	}

	
	
	
	public static IWorkbenchPage getWorkbenchPage() {
		return getWorkbenchWindow().getActivePage();
	}


	public static Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}
	



}
