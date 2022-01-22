package timmoson.common.transferedobjects;


import java.io.Serializable;

/**
 * Object stored in client session store
 *
 */
public class RemoteObjectClient implements Serializable{
	private static final long serialVersionUID = 2664186260569165749L;

	public ServiceId objectId;
	public String className;
	
}
