package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.additioalmethodwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.StoreComplex
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.CreatedAtInfoI
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import org.codehaus.groovy.runtime.MethodClosure

import java.lang.reflect.Method;
import java.util.logging.Logger;

@CompileStatic
class CreatedAtMethodWriter extends AddiotionalInfoMethodWriterBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CreatedAtMethodWriter(StoreComplex storeComplex) {
        super(storeComplex)
    //    storeComplex.writeCreatedAt = false
    }

    @Override
    List<Method> getBaseMethods() {
        return [JrrClassUtils.findMethodByCount(CreatedAtInfoI, 'getCreatedDate', 0)]
    }


//    @Override
//    Object coreMethod2() {
//        return DateWithFormat.createCurrentDate()
//    }

    @Override
    Object getObjectToWriteForMethod(Method method) {
        return DateWithFormat.createCurrentDate()
    }
}
