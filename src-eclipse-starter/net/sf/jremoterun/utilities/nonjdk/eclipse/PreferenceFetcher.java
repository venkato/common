package net.sf.jremoterun.utilities.nonjdk.eclipse;

import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class PreferenceFetcher {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static PreferenceFetcher preferenceFetcher = new PreferenceFetcher();

	public IPreferenceStore getPreferenceStore2() {
		if (JrrActivator.instance == null) {
			throw new NullPointerException("instance is null");
		}
		return JrrActivator.instance.getPreferenceStore();
	}

}
