package net.sf.jremoterun.utilities.nonjdk.log.tojdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JdkCustomLayout extends JdkLogFormatter2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public List<String> additionalIgnore = []

    @Override
    boolean acceptStackTraceElement(StackTraceElement stackTraceElement) {
        boolean accept = super.acceptStackTraceElement(stackTraceElement)
        if(accept){
            String lcassName = stackTraceElement.getClassName();
            for (String ignore : additionalIgnore) {
                boolean res = lcassName.startsWith(ignore);
                if (res) {
                    return false;
                } else {

                }
            }
        }
        return accept
    }

}
