package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class JarManifestRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JarManifestRed() {
        super(sun.security.util.ManifestEntryVerifier)
    }

    void disableVerifier() throws Exception {
        Class clazz = sun.security.util.ManifestEntryVerifier
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "verify", 2);
        method1.setBody("{return null;}");
        doRedefine()
    }

}
