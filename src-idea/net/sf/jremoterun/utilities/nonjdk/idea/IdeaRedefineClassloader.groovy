package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.ide.plugins.cl.PluginClassLoader

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import org.codehaus.groovy.runtime.callsite.CallSiteArray
import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite

import java.lang.reflect.Method
import java.util.logging.Logger

@CompileStatic
public class IdeaRedefineClassloader implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String ideaPluginId;

    public static List<Integer> actionTried = []
    public static List<Integer> actionFinished = []

    public static void redifineClassloader3() throws Exception {
        PluginClassLoader pluginClassLoader = JrrClassUtils.getCurrentClassLoader() as PluginClassLoader
        String pluginId = pluginClassLoader.getPluginId().toString()
        log.info "detected plugin pluginId = ${pluginId} className=${pluginClassLoader.getClass().getName()}"
        redifineClassloader2(pluginId)
    }

    public static void redifineClassloader2(String pluginId) throws Exception {
        ideaPluginId = pluginId
        redifineClassloader()
    }

    public static void redifineClassloader() throws Exception {
        Class ccc = PluginClassLoader;
        final CtClass cc = redefineClassloaderImpl();
        JrrJavassistUtils.redefineClass(cc, ccc);
        log.info("PluginClassLoader redefine done");
    }

    public static CtClass redefineClassloaderImpl() throws Exception {
        if (ideaPluginId == null) {
            throw new Exception("ideaPluginId not defined")
        }
        log.info("PluginClassLoader try to redefine ... ");
        log.info("groovy1 file ${UrlCLassLoaderUtils.getClassLocation(CallSiteArray)}");
        log.info("groovy2 file ${UrlCLassLoaderUtils.getClassLocation(MetaClassConstructorSite)}");
        JrrJavassistUtils.init();
        final Class ccc = PluginClassLoader;
        log.info("PluginClassLoader class location : " + JrrUtils.getClassLocation(ccc));
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(ccc);
        final CtMethod method8 = JrrJavassistUtils.findMethodByCount(ccc, cc, "loadClass", 2);
        if(method8 == null){
            throw new NullPointerException("failed find loadClass method for ${ccc.getName()}")
        }
        //Method method1NotUsed;
        String customInsert;
        String values;
        try {
            try {
                actionTried.add(1)
                Method method1NotUsed = JrrClassUtils.findMethodByCount(PluginClassLoader, 'loadClassFromParents', 2) //NOFIELDCHECK
                values = """ loadClassFromParents(\$1, null) """
                assert method1NotUsed.getReturnType().equals(Class)
                actionFinished.add(1)
            } catch (NoSuchMethodException e) {
                try {
                    actionTried.add(2)
                    log.info "failed find loadClassFromParents from idea ce 2018 ${e}"
                    Method method1NotUsed = JrrClassUtils.findMethodByCount(PluginClassLoader, 'processResourcesInParents', 5) //NOFIELDCHECK
                    values = """ (Class) processResourcesInParents(\$1, this.loadClassInPluginCL, this.loadClassInCl,null, (Object)null) """
                    actionFinished.add(2)
                } catch (NoSuchMethodException e2) {
                    try {
                        actionTried.add(3)
                        Method method1NotUsed = JrrClassUtils.findMethodByCount(PluginClassLoader, 'processResourcesInParents', 6) //NOFIELDCHECK
                        values = """ (Class) processResourcesInParents(\$1, this.loadClassInPluginCL, this.loadClassInCl, null,  (Object)null, true) """
                        actionFinished.add(3)
                    } catch (NoSuchMethodException e3) {
                        // idea 2021.1
                        actionTried.add(20211)
                        Method method1NotUsed = JrrClassUtils.findMethodByCount(PluginClassLoader, 'getAllParents', 0)
                        assert ClassLoader[] == method1NotUsed.getReturnType()
                        customInsert = """ 
ClassLoader[] allParents123 = this.getAllParents();
for (int i=0 ; i<allParents123.length ; i++ ) {
  try {
            Class clazzz1 = allParents123[i].loadClass(\$1);
            if (clazzz1 != null) {
                return clazzz1; 
            }
  }  catch (ClassNotFoundException eee) {
  } 
}

 """
                        actionFinished.add(20211)
                    }
                }
            }

        } catch (NoSuchMethodException e) {
            actionTried.add(4)
            log.info "failed find processResourcesInParents from idea ce 2019 ${e}"
            Set anySetValue = new HashSet()
            Method method1NotUsed = JrrClassUtils.findMethodByParamTypes1(ccc, 'a', 'anyvalue', anySetValue) //NOFIELDCHECK
            values = """ a(\$1,null) """
            assert method1NotUsed.getReturnType().equals(Class)
//            method1NotUsed = JrrClassUtils.findMethodByParamTypes1(ccc,'b','anyvalue')
//            values = """ b(\$1) """
            actionFinished.add(4)
        }
        if(method8!=null){
            log.info("redefining method ${method8.getDeclaringClass().getName()} ${method8.getName()} ${method8.getLongName()} param count = ${method8.getParameterTypes().length}   customInsert?${customInsert!=null}")
        }
        if (customInsert == null) {
            method8.insertBefore """            
            if("${ideaPluginId}".equals(getPluginId().toString() ) ) {
                Class classLoadedByParent4 = ${values};
                if(classLoadedByParent4 != null){ 
                    return classLoadedByParent4; 
                };
            }
""";

        } else {
            if(method8 == null){
                throw new NullPointerException("method is null")
            }
            method8.insertBefore """            
            if("${ideaPluginId}".equals(getPluginId().toString() ) ) {
                ${customInsert}
            }
""";
        }

        log.info(method8.toString());
        return cc;
    }

    @Override
    void run() {
        redifineClassloader3();
    }
}