package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ObjectStringComparator extends ObjectBaseComparator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public boolean compareClassNameFirst = true

//    @Override
//    int compare(Object lhs, Object rhs) {
//        if (lhs == null) {
//            if (rhs == null) {
//                return 0;
//            }
//            return -1;
//        }
//        if (rhs == null) {
//            return 1
//        }
//        if (compareClassNameFirst) {
//            String lhsClass = lhs.getClass().getName()
//            String rhsClass = rhs.getClass().getName()
//            boolean classEquals = (lhsClass.equals(rhsClass))
//            log.debug("${lhsClass} ${rhsClass} : ${classEquals}")
//            if (!classEquals) {
//                return lhsClass.compareToIgnoreCase(rhsClass)
//            }
//        }
//        String stringLeft = lhs.toString()
//        String stringRight = rhs.toString()
//        int result =  stringLeft.compareToIgnoreCase(stringRight)
//        log.debug "${stringLeft} ${stringRight} : ${result}"
//        return result
//    }

    @Override
    int onSameClasses(Object lhs, Object rhs) {
        String stringLeft = lhs.toString()
        String stringRight = rhs.toString()
        int result =  stringLeft.compareToIgnoreCase(stringRight)
        log.debug "${stringLeft} ${stringRight} : ${result}"
        return result
    }
}
