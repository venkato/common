package timmoson.common;


public abstract class CallBackSession {

	public Object customProperties;

	public <T> T makeClient(Class<T> class1) throws Exception {
		return makeClient(class1, class1.getName());
	}

	public abstract <T> T makeClient(Class<T> class1, String serviceId)
			throws Exception;
}
