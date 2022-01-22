package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommandBuilder

import java.util.logging.Logger

@CompileStatic
class OpenUrlHandlerInBrowser extends NativeCommandBuilder  implements OpenUrlHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int minUrlLength = 3

    OpenUrlHandlerInBrowser(ToFileRef2 browserPath) {
        super([browserPath.resolveToFile().getAbsolutePath()])
        waitSync = false
        consumeOutStreamSysoutF = true
    }

    OpenUrlHandlerInBrowser(File browserPath) {
        super([browserPath.getAbsolutePath()])
        waitSync = false
        consumeOutStreamSysoutF = true
    }

    @Override
    NativeCommand openUrl(URL url) {
        return openUrlImpl(url.toString())
    }

    @Override
    NativeCommand openUrl(String url) {
        return openUrlImpl(url)
    }

    NativeCommand openUrlImpl(String url){
        if(url.length()<minUrlLength){
            throw new IllegalArgumentException("URL too short : ${url}")
        }
        NativeCommand nativeCommand = buildCustomArgs2(url)
        return nativeCommand
    }
}
