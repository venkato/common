		package timmoson.client;


        import net.sf.jremoterun.JrrUtils;
		import net.sf.jremoterun.utilities.JrrClassUtils;
		import timmoson.common.sertcp.Consts;
        import timmoson.common.sertcp.RemoteService;
        import timmoson.common.sertcp.TcpSession;
        import timmoson.common.transferedobjects.ReponseBean;

        import java.io.ByteArrayOutputStream;
		import java.util.logging.Logger;

		public class ClientStaticUtils {
private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	
	public static byte[] handleClientReponse(TcpSession tcpSession,
			ByteArrayOutputStream out, int i, String s) throws Exception {
		String s2 = s.substring(Consts.resultBegin.name().length() + 1, i - 1);
		log.fine(s2);
		tcpSession.updateLastAccessRead();
		byte[][] bb23 =RemoteService.defaultRemoteService. findBetween(Consts.resultBegin.name(),
				Consts.resultEnd.name(), tcpSession.inputStream, out);
		byte[] bb2 = bb23[0];
		ReponseBean reponseBean = (ReponseBean) JrrUtils.deserialize(bb2,
				RemoteService.class.getClassLoader());
		RequestInfoCleint cleint = tcpSession.requets
				.get(reponseBean.requestId);
		if (cleint == null) {
			log.fine("request id not found " + reponseBean.requestId);
		} else {
			cleint.reponseBean = reponseBean;
			synchronized (cleint.lock) {
				cleint.lock.notify();
			}
		}
		return bb23[1];
	}

}
