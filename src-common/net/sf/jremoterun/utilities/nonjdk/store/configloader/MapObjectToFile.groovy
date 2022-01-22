package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
interface MapObjectToFile<T> {


    ToFileRef2 findMapping(T obj)

}
