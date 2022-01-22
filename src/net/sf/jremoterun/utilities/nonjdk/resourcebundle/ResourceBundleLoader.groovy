package net.sf.jremoterun.utilities.nonjdk.resourcebundle

import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions;

import java.util.logging.Logger;

@CompileStatic
class ResourceBundleLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String objectKey = "JrrBundleLoaderFactory"
    public static volatile inited = false

    public static ResourceBundleStorage instance = new ResourceBundleStorage();

    static boolean initIfCan() {
        if (net.sf.jremoterun.SimpleJvmTiAgent.instrumentation == null) {
            log.info "${net.sf.jremoterun.SimpleJvmTiAgent.getName()}.instrumentation is null"
            return false
        } else {
            init()
            return true
        }
    }

    static void init() {
        if (inited) {
        } else {
            inited = true
            SharedObjectsUtils.getGlobalMap().put(objectKey, instance.factory1);
            ClassRedefintions.redefineResourceBundle(objectKey)
        }

    }

}
