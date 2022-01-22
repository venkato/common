import groovy.transform.CompileStatic;
import net.infonode.docking.TabWindow;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.AdminPage2;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.Listener;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.SlowLinkSimulator
import net.sf.jremoterun.utilities.nonjdk.tcpmon.TcpMonSettings
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.OutboundConnectionCreatorSimple
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerConnectionCreatorSimple;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

@CompileStatic
class Sam extends RstaScriptHelper {

    void r() {
        AdminPage2 adminPage2 = (AdminPage2) runner;
        TabWindow tw = (TabWindow) adminPage2.defaultView.getWindowParent();
        TcpMonSettings tcpMonSettings = new TcpMonSettings();
        OutboundConnectionCreatorSimple sslTarget = new OutboundConnectionCreatorSimple()
        ServerConnectionCreatorSimple sslInput = new ServerConnectionCreatorSimple();
        sslTarget.isSsl = false
        sslInput.isSsl = false
        sslTarget.initPort = 22;
        sslTarget.initHost = 'b';
        sslInput.initPort = 22;
        tcpMonSettings.isProxy = false;
        Listener l = new Listener(tw, tcpMonSettings ,sslTarget,sslInput);
    }



}
