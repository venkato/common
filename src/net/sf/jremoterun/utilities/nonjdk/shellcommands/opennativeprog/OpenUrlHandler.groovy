package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand;

import java.util.logging.Logger;

/**
 * @see com.intellij.ide.browsers.BrowserLauncherAppless
 */
@CompileStatic
public interface  OpenUrlHandler {



    abstract NativeCommand openUrl(URL url)

    abstract NativeCommand openUrl(String url)

}
