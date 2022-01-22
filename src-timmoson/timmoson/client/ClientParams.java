package timmoson.client;


import net.sf.jremoterun.utilities.JrrClassUtils;
import timmoson.common.sertcp.TcpSession;

public class ClientParams implements Cloneable{


	public boolean retryIfIOException=true;
	public Boolean waitResult;

	public volatile TcpSession overrideTcpSession;

	public static ClientSendRequest clientSendRequestDefault=new ClientSendRequest();

	public ClientSendRequest clientSendRequest=clientSendRequestDefault;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
