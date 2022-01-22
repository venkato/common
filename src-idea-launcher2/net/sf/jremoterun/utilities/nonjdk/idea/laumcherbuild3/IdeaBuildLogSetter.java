package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild3;

import com.intellij.openapi.diagnostic.JulLogger;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.jetbrains.jps.api.GlobalOptions;

import java.util.logging.Logger;

@CompileStatic
class IdeaBuildLogSetter {
    private static final Logger log = Logger.getLogger(IdeaBuildLogSetter.class.getName());


    public static void doAll(){
        disableIdeaLogger();
        setJdkLogger();
    }

    public static void disableIdeaLogger(){
        System.setProperty(GlobalOptions.USE_DEFAULT_FILE_LOGGING_OPTION, "false");
    }

    /**
     * @see org.jetbrains.jps.cmdline.LogSetup
     */
    public static void setJdkLogger() {
        com.intellij.openapi.diagnostic.Logger.Factory factoryBefore = com.intellij.openapi.diagnostic.Logger.getFactory();
        log.info("factory before : " + factoryBefore);
        com.intellij.openapi.diagnostic.Logger.setFactory(category -> new JulLogger(java.util.logging.Logger.getLogger(category)));
    }

}
