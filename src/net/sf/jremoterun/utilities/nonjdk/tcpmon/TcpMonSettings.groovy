package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.util.logging.Logger;

@CompileStatic
class TcpMonSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String name;
    public  boolean isProxy = false
    public SlowLinkSimulator slowLink =new SlowLinkSimulator(0, 0)
    public boolean changeHost = false
    public ShowMsgData showMsgData
    public String inputStyle = SyntaxConstants.SYNTAX_STYLE_NONE;
    public String outputStyle = SyntaxConstants.SYNTAX_STYLE_NONE;
    public boolean doStart = true;
    public String HTTPProxyHost;
    public int HTTPProxyPort;
    public File customTranslatorFile;
    public CustomDataTralator customDataTralator;

    TcpMonSettings() {

    }

    TcpMonSettings(String name, boolean isProxy, SlowLinkSimulator slowLink, boolean changeHost, ShowMsgData showMsgData, String inputStyle, String outputStyle, boolean doStart) {
        this.name = name
        this.isProxy = isProxy
        if(slowLink!=null) {
            this.slowLink = slowLink
        }
        this.changeHost = changeHost
        this.showMsgData = showMsgData
        this.inputStyle = inputStyle
        this.outputStyle = outputStyle
        this.doStart = doStart
    }

    public String createStringFromBytes(SocketRR socketRR, byte[] buffer,int offset,int len) throws UnsupportedEncodingException {
        return new String(buffer, offset, len, socketRR.encoding.getText());
    }

}
