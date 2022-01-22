package net.sf.jremoterun.utilities.nonjdk.compile.statdumper

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.langutils.comparators.ObjectBaseComparator;

import java.util.logging.Logger;

@CompileStatic
class AnalizeDumpComparator extends ObjectBaseComparator{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    int onSameClasses(Object lhs, Object rhs) {
        if(lhs instanceof MavenId){
            return compareToString(lhs,rhs)
        }
        return super.onSameClasses(lhs, rhs)
    }

}
