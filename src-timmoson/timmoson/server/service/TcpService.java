package timmoson.server.service;


import net.sf.jremoterun.utilities.JrrClassUtils;
import timmoson.client.ClientSendRequest;
import timmoson.common.CallInfoServer;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;

import java.util.logging.Logger;

public class TcpService {
	public static String serviceId=TcpService.class.getName();

	public static TcpService tcpServiceLocal=new TcpService();
	// set ClientSendRequest.getClientParams().overrideTcpSession before invokaction
	public static TcpService tcpServiceRemote=ClientSendRequest.makeProxy(TcpService.class,null,serviceId);

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public void removeLocalSessionObject(String objectId) {
		CallInfoServer callInfoServer=RemoteService.callsInfos.get();
		TcpSession tcpSession=(TcpSession) callInfoServer.getSession();
		Object obj=tcpSession.serviceObjectsServer.remove(objectId);
		if(obj==null) {
			log.warning("object with id not found "+objectId);
		}else {
			log.info("object is removed "+objectId);
		}
	}
}
