package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class ResourceBundleRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ResourceBundleRed() {
        super(ResourceBundle)
    }

    void redefineServiceLoader(String jrrServiceLoaderFactoryS) throws Exception {
        Class clazz = ResourceBundle
        redefineServiceLoader2(jrrServiceLoaderFactoryS, JrrJavassistUtils.findMethodByCount(clazz, cc, 'getBundleImpl', 4))
//        redefineServiceLoader2(jrrServiceLoaderFactoryS, JrrJavassistUtils.findMethodByCount(clazz, cc, 'getBundle', 2))
        doRedefine();

    }


    void redefineServiceLoader2(String jrrServiceLoaderFactoryS, CtBehavior iteratorMethod) throws Exception {
        assert iteratorMethod != null
        int length = iteratorMethod.getParameterTypes().length + 1
        String addddBefore = ""
        String addddAfter = ""
        for (i in 1..<length) {
            addddBefore += "jrrlistBefore.add(\$${i}); \n"
            addddAfter += "jrrlistAfter.add(\$${i}); \n"
        }


        if(true) {
            iteratorMethod.insertBefore """
{
${JrrJavassistUtils.createGlobalServicesMapVar}
java.util.Map jrrServiceLoaderFactory =  (java.util.Map) ${JrrJavassistUtils.mapVarName}.get( "${jrrServiceLoaderFactoryS}" );
java.util.ArrayList jrrlistBefore = new java.util.ArrayList();
jrrlistBefore.add("${net.sf.jremoterun.utilities.nonjdk.resourcebundle.RBCallerType.before.name()}");
${addddBefore}
java.util.ResourceBundle jrrresultBefore = (java.util.ResourceBundle)jrrServiceLoaderFactory.get(jrrlistBefore);
if(jrrresultBefore!=null){
    return jrrresultBefore;
}
}
""";
        }
if(true ) {
    iteratorMethod.insertAfter """

{
${JrrJavassistUtils.createGlobalServicesMapVar}
java.util.Map jrrServiceLoaderFactory =  (java.util.Map) ${JrrJavassistUtils.mapVarName}.get( "${jrrServiceLoaderFactoryS}" );
java.util.ArrayList jrrlistAfter = new java.util.ArrayList();
jrrlistAfter.add("${net.sf.jremoterun.utilities.nonjdk.resourcebundle.RBCallerType.after.name()}");
${addddAfter}
jrrlistAfter.add(\$_);
java.util.ResourceBundle jrrresultAfter = (java.util.ResourceBundle)jrrServiceLoaderFactory.get(jrrlistAfter);
return jrrresultAfter;
}

"""
}
    }
}
