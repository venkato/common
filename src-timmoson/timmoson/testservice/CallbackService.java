package timmoson.testservice;


import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

public class CallbackService {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public void callback() {
		log.info("callback ok");
	}
}
