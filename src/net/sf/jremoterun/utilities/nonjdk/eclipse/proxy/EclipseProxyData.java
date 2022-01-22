package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy;

import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfo;

import java.net.URI;

public interface EclipseProxyData {

	String getPassword();
	String getUser();

	@Deprecated
	String getProxyHost();

	@Deprecated
	int getProxyPort();
	



	boolean useProxyForHost(String host);


	boolean useProxy(URI uri);

	ProxyHostInfo getProxyHostInfo();

}
