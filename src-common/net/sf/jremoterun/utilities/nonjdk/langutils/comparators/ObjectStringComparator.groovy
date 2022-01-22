package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ObjectStringComparator extends ObjectBaseComparator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    int onSameClasses(Object lhs, Object rhs) {
        String stringLeft = lhs.toString()
        String stringRight = rhs.toString()
        int result = stringLeft.compareToIgnoreCase(stringRight)
        log.debug "${stringLeft} ${stringRight} : ${result}"
        return result
    }
}
