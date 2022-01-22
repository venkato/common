package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import org.apache.commons.lang3.compare.ObjectToStringComparator

import java.lang.reflect.Array;
import java.util.logging.Logger;

@CompileStatic
class CollectionDumper implements ObjectDumperI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        Collection aa = obj as Collection
        List res = aa.collect { dumperProvider.dumpObject(it) }
        if(res.size()==0){
            return dumperProvider.emptyArray
        }
        if (dumperProvider.sortList) {
            res.sort(new ObjectToStringComparator())
        }
        return res;
    }

}
