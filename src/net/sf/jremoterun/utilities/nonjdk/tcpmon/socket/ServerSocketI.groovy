package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ServerSocketI extends AutoCloseable{

    SocketI accept()  throws IOException


}
