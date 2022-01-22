package net.sf.jremoterun.utilities.nonjdk.eclipse;


import net.sf.jremoterun.utilities.JrrClassUtils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import java.util.logging.Logger;


public class JrrActivator extends AbstractUIPlugin{


	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static boolean inited = EclipseLunaClasspathAdd.init();

	public static JrrActivator instance;
	
	public IPreferenceStore store1;


	static {
		log.info("JrrActivator invoked");
	}
	
	
	public JrrActivator() {
		log.info("JrrActivator constructor called");
		instance = this;
	}
	
	

    @Override
    protected void initializeDefaultPreferences(IPreferenceStore store) {
    	store1 = store;
    	log.info("pref store inited");
    }

}
