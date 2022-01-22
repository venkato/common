package timmoson.common.transferedobjects;


import java.io.Serializable;

/**
 * Object stored in server session store
 *
 */
public class RemoteObjectServer implements Serializable{
	private static final long serialVersionUID = 2664186260569165749L;

	public ServiceId objectId;
//	public boolean inSession;
	
}
