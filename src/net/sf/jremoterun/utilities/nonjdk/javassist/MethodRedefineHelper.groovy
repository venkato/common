package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.CtBehavior;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class MethodRedefineHelper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public CtBehavior ctBehavior;

    public static String dumpStackTrace = 'Thread.dumpStack();'

    void replaceBodyWithReturn(String customReturn){
        ctBehavior.setBody("{return ${customReturn};}");
    }

    void setEmptyBody(){
        ctBehavior.setBody("{}");
    }



}
