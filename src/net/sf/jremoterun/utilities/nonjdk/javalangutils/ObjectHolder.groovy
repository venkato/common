package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ObjectHolder<T> {

    T getObject();


}
