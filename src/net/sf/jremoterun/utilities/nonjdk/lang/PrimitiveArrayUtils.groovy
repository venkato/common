package net.sf.jremoterun.utilities.nonjdk.lang

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Array;
import java.util.logging.Logger;

@CompileStatic
class PrimitiveArrayUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List convertFromPrimitiveArray(Object primitiveArray){
        List res = []
        int length1 = Array.getLength(primitiveArray)
        for (i in 0..<length1) {
            res.add(Array.get(primitiveArray,i))
        }
        return res
    }

    static Object convertToPrimitiveArray(Class primitiveClass, List arrayWrapper){
        Object primitiveArray = Array.newInstance(primitiveClass, arrayWrapper.size())
        for (i in 0..<arrayWrapper.size()) {
            Array.set(primitiveArray,i,arrayWrapper.get(i))
        }
        return primitiveArray
    }


}
