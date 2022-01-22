package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.diagnostic.AsyncLog
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.JustStackTrace3;

import java.util.logging.Logger;

@CompileStatic
class IdeaAsnyLoggerDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String syncLogProp= "intellij.platform.log.sync"



     static void  checkLogSync(){
         if(! isLogSync())         {
             JrrUtilitiesShowE.showException("Anync logger", new JustStackTrace3())
             new ClRef('com.intellij.openapi.diagnostic.LogEvent')
         }
     }

     static boolean isLogSync(){
        return java.lang.Boolean.getBoolean(syncLogProp)
    }


}
