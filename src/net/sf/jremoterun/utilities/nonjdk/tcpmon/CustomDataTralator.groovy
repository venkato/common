package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface CustomDataTralator {

    String translate(TcpMonTextArea socketRR,String data);


}
