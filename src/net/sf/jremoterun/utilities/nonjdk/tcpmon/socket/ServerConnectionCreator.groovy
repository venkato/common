package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic

@CompileStatic
interface ServerConnectionCreator {

    ServerSocketI createServerSocket(int port);

}
