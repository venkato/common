package timmoson.server;


import timmoson.common.transferedobjects.ServiceId;

import java.util.Date;

public class TcpServiceObject extends ServiceSupport{
	public final Date createDate=new Date();
	public Date lastAccess=createDate;
	public ServiceId serviceInSessionId;

}
