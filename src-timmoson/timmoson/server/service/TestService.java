package timmoson.server.service;

import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

public class TestService {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public void testCall(String param) {
		log.info(param);
//		return 2;
	}
}
