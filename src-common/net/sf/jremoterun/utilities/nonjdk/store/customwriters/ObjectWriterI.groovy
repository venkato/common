package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import org.codehaus.groovy.runtime.MethodClosure

@CompileStatic
interface ObjectWriterI {

    String writeObject(Writer3Import writer3, Object obj)

    String writeConstructorWithArgs(Writer3Import writer3, Class clazz, List objects)

    String writeStringConstructor(Writer3Import writer3, Class clazz, String objectAsStr)

    String writeMethodClosure(Writer3Import writer3, Closure ref, List args)

    String writeEnum(Writer3Import writer3, Enum ee)

    String writeString(String s)

    void failedWriteCountedEl(Object el, int countt, Throwable e)
}
