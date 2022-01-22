package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.additioalmethodwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.StoreComplex
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI

import java.lang.reflect.Method
import java.util.logging.Logger

@CompileStatic
abstract class AddiotionalInfoMethodWriterBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public StoreComplex storeComplex;
    public ObjectWriterI objectWriter
    public Writer3Import writer3;
    public boolean writeOverrides = true
    public boolean addBefore = false


    AddiotionalInfoMethodWriterBase(StoreComplex storeComplex) {
        this.storeComplex = storeComplex
        objectWriter = storeComplex.objectWriter
        writer3 = storeComplex
    }

    List<? extends LineInfoI> addBase(StoreComplex storeComplex, Method method1) {
        List<? extends LineInfoI> res = []
        storeComplex.implementedInterfaces.add(method1.getDeclaringClass())
        if (writeOverrides) {
            res.add(new LineInfo(Brakets.methodAnnotaion, Writer3.overrideS))
        }
        res.add(new LineInfo(Brakets.openBacket, "${storeComplex.addImportWithName(method1.getReturnType())} ${method1.getName()}() {"));
        return res;
    }


    void check(List<Method> methods) {
        Method method1 = methods[0]
        Class<?> declaringClass = method1.getDeclaringClass()
        if (!declaringClass.isInterface()) {
            throw new Exception("not interface : ${declaringClass.getName()}")
        }
        if (storeComplex.implementedInterfaces.contains(declaringClass)) {
            throw new Exception("already contains ${declaringClass.getName()}")
        }
    }

    void writeAdditionalMethods() {
        List<Method> methods = getBaseMethods()
        check(methods)
        methods.each {
            try {
                List<? extends LineInfoI> res = createMethodContent(it)
                if (addBefore) {
                    storeComplex.beforeAddtionalMethods.addAll(res)
                } else {
                    storeComplex.afterAddtionalMethods.addAll(res)
                }
            } catch (Throwable e) {
                log.info "failed write ${it.getName()} : ${e}"
                throw e
            }
        }
    }

    List<? extends LineInfoI> createMethodContent(Method method1) {
        List<? extends LineInfoI> res = addBase(storeComplex, method1)
        res.add(new LineInfo(Brakets.return1, coreMethod(method1)));
        res.add(new LineInfo(Brakets.closeBacket, '}'))
        return res
    }


    abstract List<Method> getBaseMethods()


    String coreMethod(Method method) {
        return 'return ' + storeComplex.objectWriter.writeObject(storeComplex, getObjectToWriteForMethod(method)) + ' ;'
    }

//    @Deprecated
//    abstract Object coreMethod2()

    abstract Object getObjectToWriteForMethod(Method method)


}
