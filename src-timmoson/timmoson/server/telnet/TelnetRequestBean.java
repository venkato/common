package timmoson.server.telnet;

import timmoson.common.telnet.TelnetSession;
import timmoson.server.ServiceSupport;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class TelnetRequestBean  {

	public ServiceSupport seriveObject;
	public String serviceId;
	public String methodName;
	public ArrayList<String> params=new ArrayList<String>();
	public Object[] paramsAsObject;
	
	public TelnetSession telnetSession;
	public Method method;

}
