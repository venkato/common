package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ListComparator extends ObjectBaseComparator<List>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectBaseComparator elCOmparator = new ObjectBaseComparator()

    @Override
    int onDifferentClasses(List lhs, List rhs) {
        throw new IllegalStateException("${lhs} ${rhs}")
    }

    @Override
    int onSameClasses(List lhs, List rhs) {
        int size1=lhs.size()
        assert lhs.size()==rhs.size()
        int i=0;
        while (i<size1){
            int res = elCOmparator.compare(lhs[i],rhs[i])
            if(res!=0){
                return res
            }
            i++
        }
        return 0
    }
}
