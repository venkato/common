package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import com.pty4j.util.PtyUtil
import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

/**
 *
 * @see net.sf.jremoterun.utilities.nonjdk.downloadutils.WinptyDownloader#setPtyLibFolder
 */
@CompileStatic
class PtyUtilsRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PtyUtilsRed() {
        super(PtyUtil)
    }


    void redefine(File f) throws Exception {
        assert f.exists()
        assert f.isFile()
        String unixFilee = f.getAbsolutePathUnix()
        Class clazz = PtyUtil
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "resolveNativeLibrary", 0);
        method1.setBody(""" 
return new java.io.File("${unixFilee}");
""");

        doRedefine();
    }

}
