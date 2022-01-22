		package timmoson.server;

		


		public class GetReponseHandler {

	
	public static GetReponseHandler reponseHandler=new GetReponseHandler();
	
	public ServiceCallServerInvoker defaultServiceCallServerInvoker = new ServiceCallServerInvoker();
	
	public ServiceCallServerInvoker getServiceCallServerInvoker(TcpCallInfoServer callInfo) {
		return defaultServiceCallServerInvoker;
	}
}
