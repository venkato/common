package idea.plugins.thirdparty.filecompletion.jrr.a.maven

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class IsMavenToken {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static boolean isMavenToken(String value4) {
        int count1 = value4.tokenize(':').size()
        if (count1 in [3, 4]) {
            return true
        }
        return false
    }

}
