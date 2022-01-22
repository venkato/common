package timmoson.testservice;

import net.sf.jremoterun.utilities.JrrClassUtils;

import timmoson.common.sertcp.MakeProxyDiffClassLoader;
import timmoson.common.sertcp.RemoteService;

import java.util.logging.Logger;

public class SampleService {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public static String strResturn = "7ffd723";

	public String test1() {
		return "ok22";
	}

	public int add2(int i) {
		return i + 2;
	}

	public int add2Array(int[] i) {
		return i[0] + 2;
	}

	public int add3Array(Integer[] i) {
		return i[0] + 2;
	}

	public String test2() {
		return strResturn;
	}

	public String testCallback() throws Exception {
		log.info("123");
		CallbackService callbackService = RemoteService.callsInfos.get()
				.getSession().makeClient(CallbackService.class);
		callbackService.callback();
		log.info("123 5");
		return strResturn;
	}

	public String testCallbackDiffClassloader() throws Exception {
		log.info("123");
		CallbackService callbackService = MakeProxyDiffClassLoader
				.makeClient(CallbackService.class);
		callbackService.callback();
		log.info("123 5");
		return strResturn;
	}

	public String testThrowExc() throws Exception {
		throw new Exception();
	}
}
