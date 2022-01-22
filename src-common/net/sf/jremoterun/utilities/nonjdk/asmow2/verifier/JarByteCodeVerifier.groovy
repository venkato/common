package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import org.apache.commons.io.IOUtils
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry;

@CompileStatic
class JarByteCodeVerifier implements ZipEntryCallback {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AsmUtils asmUtils

    AsmByteCodeVerifier asmByteCodeVerifier

    public boolean foundFiled = false




    @Deprecated
    JarByteCodeVerifier() {
        this(JrrClassUtils.getCurrentClassLoader())
//        asmByteCodeVerifier = new AsmByteCodeVerifier(JrrClassUtils.getCurrentClassLoader())
    }


    JarByteCodeVerifier(ClassLoader classLoader1) {
        super()
        asmUtils= new AsmUtils(classLoader1)
        asmByteCodeVerifier = new AsmByteCodeVerifier(classLoader1)
    }



    @Override
    void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
        if (needVerify(zipEntry)) {
            byte[] array = IOUtils.toByteArray(inputStream)
            String name1 = zipEntry.getName()
            try {
                asmByteCodeVerifier.verifyByteCode(array)
                foundFiled = true
            } catch (Exception e) {
                log.info "failed verify : ${name1} : ${e}"
                throw e
            }
        }
    }

    boolean needVerify(ZipEntry zipEntry) {
        return zipEntry.getName().endsWith(ClassNameSuffixes.dotclass.customName)
    }
}
