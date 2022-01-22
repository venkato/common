package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.EnvOpenSettings
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.OpenUrlHandler

import javax.swing.event.HyperlinkEvent
import java.util.logging.Logger

@CompileStatic
class OpenInBrowserLinkGeneratorResult2 extends OpenInBrowserLinkGeneratorResult {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public OpenUrlHandler openUrlHandler;

    OpenInBrowserLinkGeneratorResult2(int sourceOffset, String url) {
        this(sourceOffset, url,EnvOpenSettings.defaultOpenUrlHandler)
    }

    OpenInBrowserLinkGeneratorResult2(int sourceOffset, String url, OpenUrlHandler openUrlHandler) {
        super(sourceOffset, url)
        this.openUrlHandler = openUrlHandler
        if(openUrlHandler==null){
            throw new NullPointerException("openUrlHandler is null")
        }
    }

    @Override
    HyperlinkEvent execute() {
        openUrlHandler.openUrl(url)
        return null
    }
}
