package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import java.net.URI;
import java.util.logging.Logger;

import org.eclipse.core.internal.net.AbstractProxyProvider;
import org.eclipse.core.internal.net.ProxyData;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import net.sf.jremoterun.utilities.nonjdk.eclipse.proxy.EclipseProxyDataType;
import net.sf.jremoterun.utilities.JrrClassUtils;

public class EclipseProxyAdopt {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static EclipseProxyAdopt proxyAdopt;

//	public ProxyData proxyDataHttp;
//	public ProxyData proxyDataHttpS;

	public AbstractProxyProvider nativeProxyProvider;
	public JrrAbstractProxyProvider jrrAbstractProxyProvider;
	public ProxyManager defaultProxyService;
	public ProxyWrapper proxyWrapper;

	public IProxyData[] noProxy = new IProxyData[0];
//	public IProxyData[] proxyHttp = new IProxyData[1];
//	public IProxyData[] proxyHttps = new IProxyData[1];
//	public IProxyData[] proxyBoth = new IProxyData[2];
	public EclipseProxyData eclipseProxyData;

	public boolean useJrrProxy = true;

	public EclipseProxyAdopt(EclipseProxyData eclipseProxyData) throws Exception {
		this.eclipseProxyData = eclipseProxyData;
		proxyAdopt = this;
		init();
	}
	
	public IProxyData[] createProxyDataArray(EclipseProxyDataType type) {
		IProxyData[] array1 = new IProxyData[1];
		array1[0]=createProxyData(type);
		return array1;
	}
	
	public ProxyData createProxyData(EclipseProxyDataType type) {
		ProxyData proxyDataHttp = new ProxyData(type.getCustomName(), eclipseProxyData.getProxyHostInfo().getProxyHost(), eclipseProxyData.getProxyHostInfo().getProxyPort(), true,
				"jrr"+type.getCustomName());
		proxyDataHttp.setUserid(eclipseProxyData.getUser());
		proxyDataHttp.setPassword(eclipseProxyData.getPassword());

		return proxyDataHttp;
	}

	public void init() throws Exception {
//		proxyDataHttp = new ProxyData("HTTP", eclipseProxyData.getProxyHostInfo().getProxyHost(), eclipseProxyData.getProxyHostInfo().getProxyPort(), true,
//				"jrrHttp");
//		proxyDataHttpS = new ProxyData("HTTPS", eclipseProxyData.getProxyHostInfo().getProxyHost(), eclipseProxyData.getProxyHostInfo().getProxyPort(), true,
//				"jrrHttps");
		defaultProxyService = (ProxyManager) ProxyManager.getProxyManager();
		proxyWrapper = new ProxyWrapper(defaultProxyService, this);
		JrrClassUtils.setFieldValue(ProxyManager.class, "proxyManager", proxyWrapper);
//		proxyDataHttp.setUserid(eclipseProxyData.getUser());
//		proxyDataHttpS.setUserid(eclipseProxyData.getUser());
//		proxyDataHttp.setPassword(eclipseProxyData.getPassword());
//		proxyDataHttpS.setPassword(eclipseProxyData.getPassword());

//		proxyHttp[0] = proxyDataHttp;
//		proxyHttps[0] = proxyDataHttpS;
//		proxyBoth[0] = proxyDataHttp;
//		proxyBoth[1] = proxyDataHttpS;
		nativeProxyProvider = (AbstractProxyProvider) JrrClassUtils.getFieldValue(defaultProxyService,
				"nativeProxyProvider");
		jrrAbstractProxyProvider = new JrrAbstractProxyProvider(nativeProxyProvider, this);
		JrrClassUtils.setFieldValue(defaultProxyService, "nativeProxyProvider", jrrAbstractProxyProvider);
	}
	
	public IProxyData[] getProxyDataBoth() {
		IProxyData[] array1 = new IProxyData[2];
		array1[0]=createProxyData(EclipseProxyDataType.HTTP);
		array1[1]=createProxyData(EclipseProxyDataType.HTTPS);
		return array1;

	}

	public IProxyData getProxyDataForHost(String host, String type) {
		if (type == null) {
			return null;
		}
		boolean useProxy = eclipseProxyData.useProxyForHost(host);
		if (!useProxy) {
			return null;
		}
		type = type.toLowerCase();
		if ("http".equals(type)) {
			return createProxyData(EclipseProxyDataType.HTTP);
		}
		if ("https".equals(type)) {
			return createProxyData(EclipseProxyDataType.HTTPS);
		}
		log.info("strange type " + type + " " + host);
		return null;
	}

	public IProxyData[] selectImpl(URI uri) {
		boolean useProxy = eclipseProxyData.useProxy(uri);
		if (!useProxy) {
			return noProxy;
		}
		String schema = uri.getScheme().toLowerCase();
		if ("http".equals(schema)) {
			return createProxyDataArray(EclipseProxyDataType.HTTP);
		}
		if ("https".equals(schema)) {
			return createProxyDataArray(EclipseProxyDataType.HTTPS);
		}
		log.info("strange schema for " + uri);
		return createProxyDataArray(EclipseProxyDataType.HTTP);
	}

	public IProxyData[] getProxyDataForHost(String host) {
		boolean useProxy = eclipseProxyData.useProxyForHost(host);
		if (!useProxy) {
			return noProxy;
		}
		return getProxyDataBoth();
	}

}
