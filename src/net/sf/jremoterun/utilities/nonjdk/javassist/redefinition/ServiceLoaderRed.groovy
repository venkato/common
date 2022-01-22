package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger

@CompileStatic
class ServiceLoaderRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ServiceLoaderRed() {
        super(ServiceLoader)
    }

    void redefineServiceLoader(String jrrServiceLoaderFactoryS) throws Exception {
        Class clazz = ServiceLoader
        CtBehavior iteratorMethod = JrrJavassistUtils.findMethodByCount(clazz, cc, 'iterator', 0)
        assert iteratorMethod != null
        iteratorMethod.insertBefore """
{
${JrrJavassistUtils.createGlobalServicesMapVar}
java.util.Map jrrServiceLoaderFactory =  (java.util.Map) ${JrrJavassistUtils.mapVarName}.get( "${jrrServiceLoaderFactoryS}" );
java.util.Iterator jrrresult = (java.util.Iterator)jrrServiceLoaderFactory.get(this);
if(jrrresult!=null){
    return jrrresult;
}
}
"""
        doRedefine();
    }
}
