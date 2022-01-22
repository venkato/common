/*
 * Copyright (c) 2020 Thomas Wolf <thomas.wolf@paranor.ch>
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0 which is available at
 * https://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package net.sf.jremoterun.utilities.nonjdk.net.ssl;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslCiphers

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory
import java.util.logging.Logger;

/**
 * An {@link SSLSocketFactory} that delegates to another factory and allows
 * configuring the created socket via {@link #configure(SSLSocket)} before it is
 * returned.
 * Copied from
 * @see org.eclipse.jgit.internal.transport.http.DelegatingSSLSocketFactory
 */

@CompileStatic
public class DelegatingSSLSocketFactory extends SSLSocketFactory {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public final SSLSocketFactory delegate;
	public SSLSocket sslSocketCaught;
	public List<SslCiphers> enabledCiphers = [];


//	void dumpParams(){
//		log.info2 "${sslSocket.getSupportedProtocols()}";
//		log.info2 "${sslSocket.getEnabledProtocols()}"
//		log.info2 "${sslSocket.getApplicationProtocol()}";
//		log.info2 "${sslSocket.getEnabledCipherSuites()}"
//		log.info2 "${sslSocket.getSupportedCipherSuites()}"
//
//		SSLSession session = sslSocket.getSession()
//		log.info2 "${session.getProtocol()}"
//		log.info2 "${session.getCipherSuite()}"
//		if (session instanceof ExtendedSSLSession) {
//			ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession) session;
//			log.info2 "${extendedSSLSession.getLocalSupportedSignatureAlgorithms()}"
////			log.info2 "${extendedSSLSession.getPeerSupportedSignatureAlgorithms()}"
//		}
//	}

	public Object fetchHandshaker() throws NoSuchFieldException, IllegalAccessException {
		ClRef clRef1 = new ClRef("sun.security.ssl.ClientHandshaker");
		ClRef clRef3 = new ClRef("sun.security.ssl.SSLSocketImpl");
		FieldRef handshaker = new FieldRef(clRef3, "handshaker");
		Object handShaker = JrrClassUtils.getFieldValue(sslSocketCaught, handshaker.fieldName);
		return handShaker;
	}


	/**
	 * Creates a new {@link DelegatingSSLSocketFactory} based on the given
	 * delegate.
	 *
	 * @param delegate
	 *            {@link SSLSocketFactory} to delegate to
	 */
	public DelegatingSSLSocketFactory(SSLSocketFactory delegate) {
		this.delegate = delegate;
	}

	@Override
	public SSLSocket createSocket() throws IOException {
		return prepare(delegate.createSocket());
	}

	@Override
	public SSLSocket createSocket(String host, int port) throws IOException {
		return prepare(delegate.createSocket(host, port));
	}

	@Override
	public SSLSocket createSocket(String host, int port,
			InetAddress localAddress, int localPort) throws IOException {
		return prepare(
				delegate.createSocket(host, port, localAddress, localPort));
	}

	@Override
	public SSLSocket createSocket(InetAddress host, int port)
			throws IOException {
		return prepare(delegate.createSocket(host, port));
	}

	@Override
	public SSLSocket createSocket(InetAddress host, int port,
			InetAddress localAddress, int localPort) throws IOException {
		return prepare(
				delegate.createSocket(host, port, localAddress, localPort));
	}

	@Override
	public SSLSocket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException {
		return prepare(delegate.createSocket(socket, host, port, autoClose));
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	private SSLSocket prepare(Socket socket) throws IOException {
		SSLSocket sslSocket = (SSLSocket) socket;
		configure(sslSocket);
		return sslSocket;
	}

	/**
	 * Configure the newly created socket.
	 *
	 * @param socket
	 *            to configure
	 * @throws IOException
	 *             if the socket cannot be configured
	 */
	protected void configure(SSLSocket socket) throws IOException{
		this.sslSocketCaught = socket;
		if(enabledCiphers!=null && enabledCiphers.size()>0){
			String[] ssss = enabledCiphers.collect { it.name() }.toArray(new String[0])
			socket.setEnabledCipherSuites(ssss)
		}
	}

}
