package net.sf.jremoterun.utilities.nonjdk.langutils.cloner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ListComparator extends ObjectBaseComparator<List> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectBaseComparator elCOmparator = new ObjectBaseComparator()


    @Override
    void onSameClasses(List lhs, List rhs) {
        int size1=lhs.size()
        assert lhs.size()==rhs.size()
        int i=0;
        while (i<size1){
            elCOmparator.clone1(lhs[i],rhs[i])
            i++
        }
    }
}
