package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ReflectionElCommonCompartor implements Comparator<ReflectionElCommon>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    int compare(ReflectionElCommon o1, ReflectionElCommon o2) {
        if(o1==null){
            if(o2==null){
                return 0
            }
            return 1
        }
        if(o2==null){
            return -1
        }
        int r = o1.getClRef().getClRef().compareTo(o2.getClRef().getClRef())
        if(r==0){
            return o1.lineNumber.compareTo(o2.lineNumber)
        }
        return r
    }
}
