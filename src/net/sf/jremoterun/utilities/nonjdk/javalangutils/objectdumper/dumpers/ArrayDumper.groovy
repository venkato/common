package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import org.apache.commons.lang3.compare.ObjectToStringComparator

import java.lang.reflect.Array;
import java.util.logging.Logger;

@CompileStatic
class ArrayDumper implements ObjectDumperI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        int length1 = Array.getLength(obj);
        if(length1==0){
            return dumperProvider.emptyArray
        }
        List res = []
        for(int i=0;i<length1;i++) {
            Object get111 = Array.get(obj, i);
            try{
            res.add(dumperProvider.dumpObject(get111))
            } catch (Throwable e) {
                log.info "failed dump ${i}"
                throw e
            }
        }
        if(dumperProvider.sortList) {
            res.sort(new ObjectToStringComparator())
        }
        return res;
    }
}
