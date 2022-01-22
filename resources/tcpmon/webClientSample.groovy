import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.ConnectionRequest;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.WebClient;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.requestbuilders.HttpRequest;
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders;

@CompileStatic
class webClientSample extends RstaScriptHelper {

    void r() {
        WebClient adminPage2 = (WebClient) runner;
        ConnectionRequest cr = new ConnectionRequest();
        cr.host = 'repo1.maven.org';
//        cr.host = '127.0.0.1';
//        cr.host = 'ya.ru';
        cr.port = 443;
        cr.isSsl = true;
        //cr.sslConnectionInspect.jsseProvider = JsseProviders.jdkInternal;
        HttpRequest httpRequest = new HttpRequest();
//        httpRequest.sep = '\n'
        httpRequest.addUserStuff(0,adminPage2.getInputTextSep())
        httpRequest.addHost(cr);
        httpRequest.addChromeHeaders();

        adminPage2.createNewConnection(cr)
//        log.info httpRequest.buildRequest()
        log.info2 123
//        adminPage2.httpClient.sendMsg( adminPage2.getInputText().replace('|','\u0001'))
        adminPage2.httpClient.sendMsg( httpRequest.buildRequest() )
    }


}
