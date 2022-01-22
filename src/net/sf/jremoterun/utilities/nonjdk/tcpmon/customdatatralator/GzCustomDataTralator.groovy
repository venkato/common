package net.sf.jremoterun.utilities.nonjdk.tcpmon.customdatatralator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.tcpmon.CustomDataTralator
import net.sf.jremoterun.utilities.nonjdk.tcpmon.SocketRR
import net.sf.jremoterun.utilities.nonjdk.tcpmon.TcpMonTextArea
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger
import java.util.zip.GZIPInputStream;

@CompileStatic
class GzCustomDataTralator implements CustomDataTralator{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    String translate(TcpMonTextArea socketRR, String data) {
        String encoding = socketRR.getCharsetEncoding()
        byte[] bytes = data.getBytes(encoding)
        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes))
        byte[] byteArray = IOUtils.toByteArray(gzipInputStream)
        return new String(byteArray,encoding)
    }
}
