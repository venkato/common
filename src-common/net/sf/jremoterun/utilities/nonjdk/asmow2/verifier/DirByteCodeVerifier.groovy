package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import org.apache.commons.io.IOUtils
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
class DirByteCodeVerifier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AsmUtils asmUtils

    public AsmByteCodeVerifier asmByteCodeVerifier

    DirByteCodeVerifier() {
        this(JrrClassUtils.getCurrentClassLoader())
    }

    DirByteCodeVerifier(ClassLoader cl) {
        asmByteCodeVerifier = new AsmByteCodeVerifier(cl)
        asmUtils = new AsmUtils(cl)
    }

    void verifyDir(File dir) {
        assert dir.exists()
        assert dir.directory
        dir.listFiles().toList().each { handleSubFile(it) }
    }

    void handleSubFile(File f) {
        if (f.isFile()) {
            if (needCheckFile(f)) {
                try {
                    asmByteCodeVerifier.verifyByteCode(f.bytes)
                } catch (Exception e) {
                    log.info "failed verify ${f} : ${e}"
                    throw e
                }
            }
        } else if (f.isDirectory()) {
            if (needCheckDir(f)) {
                verifyDir(f)
            }
        }
    }

    boolean needCheckDir(File dir) {
        return true
    }

    boolean needCheckFile(File file) {
        return file.name.endsWith(ClassNameSuffixes.dotclass.customName)
    }


}
