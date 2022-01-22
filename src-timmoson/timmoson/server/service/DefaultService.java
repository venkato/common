package timmoson.server.service;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import timmoson.client.ClientSendRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class DefaultService {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public static DefaultService tcpServiceLocal = new DefaultService();
	public static DefaultService tcpServiceRemote = ClientSendRequest
			.makeProxy(DefaultService.class, null,
					DefaultService.class.getName());

	public Process runCommand(String[] command, String[] envp, File dir)
			throws IOException {
		log.info("running " + Arrays.toString(command));
		Process exec = Runtime.getRuntime().exec(command);
		log.info("done");
		return exec;
	}

	public static final byte[] emptyByteBuf = new byte[0];

	public byte[] readAllBytesFromInpustStream(InputStream in)
			throws IOException {
		byte[] readAllBytesFromInpustStream = JrrUtils
				.readAllBytesFromInpustStream(in);
		return readAllBytesFromInpustStream;
	}

}
