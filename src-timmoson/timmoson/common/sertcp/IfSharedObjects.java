package timmoson.common.sertcp;

import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrUtilitiesMapBuilder;

import java.util.HashMap;

public class IfSharedObjects {

	public static String IfSharedObjectsS = "timmoson.IfSharedObjects";
	public static String callInfoS = "timmoson.callInfo";
	public static String servicesS = "timmoson.sevices";

	public static HashMap<String, Object> ifObjects = (HashMap) JrrUtilitiesMapBuilder
			.buildObjectNoEx(SharedObjectsUtils.getGlobalMap(),
					IfSharedObjectsS, JrrUtilitiesMapBuilder.constructorHashMap);

	public static HashMap<String, Object> services = (HashMap) JrrUtilitiesMapBuilder
			.buildObjectNoEx(ifObjects, servicesS,
					JrrUtilitiesMapBuilder.constructorHashMap);

	// <CallInfoServer>
	public static ThreadLocal getCallInfo() {
		ThreadLocal threadLocal = (ThreadLocal) ifObjects.get(callInfoS);
		if (threadLocal == null) {
			threadLocal = new ThreadLocal();
			ifObjects.put(callInfoS, threadLocal);
		}
		return threadLocal;
	}

}
