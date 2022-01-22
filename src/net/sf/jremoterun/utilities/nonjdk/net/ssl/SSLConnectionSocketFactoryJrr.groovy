package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.HttpHost
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.X509HostnameVerifier
import org.apache.http.protocol.HttpContext


import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Logger;

/**
 * https://www.cloudflare.com/en-gb/learning/ssl/what-happens-in-a-tls-handshake/
 */
@CompileStatic
class SSLConnectionSocketFactoryJrr extends SSLConnectionSocketFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SSLSocket sslSocket1;

    @Override
    Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
//        Thread.dumpStack()
        sslSocket1 = super.createLayeredSocket(socket, target, port, context) as SSLSocket
        return sslSocket1;
    }

    SSLSession fetchSession(){
        return sslSocket1.getSession()
    }

    String dumpNegotiatedParams(){
        SSLSession session1 = fetchSession()
        return "${session1.getProtocol()} ${session1.getCipherSuite()}"
    }


    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLContext sslContext) {
        super(sslContext)
    }

    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier)
    }

    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
        super(sslContext, supportedProtocols, supportedCipherSuites, hostnameVerifier)
    }

    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
        super(socketfactory, hostnameVerifier)
    }

    SSLConnectionSocketFactoryJrr(SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
        super(socketfactory, supportedProtocols, supportedCipherSuites, hostnameVerifier)
    }

    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier)
    }

    @Deprecated
    SSLConnectionSocketFactoryJrr(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        super(sslContext, supportedProtocols, supportedCipherSuites, hostnameVerifier)
    }

    SSLConnectionSocketFactoryJrr(SSLSocketFactory socketfactory, HostnameVerifier hostnameVerifier) {
        super(socketfactory, hostnameVerifier)
    }

    SSLConnectionSocketFactoryJrr(SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        super(socketfactory, supportedProtocols, supportedCipherSuites, hostnameVerifier)
    }

    @Override
    Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context)
    }
}
