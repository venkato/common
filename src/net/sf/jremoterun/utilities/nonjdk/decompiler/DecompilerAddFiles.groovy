package net.sf.jremoterun.utilities.nonjdk.decompiler;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.fileloayout.Java8Files
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.java.decompiler.struct.StructContext;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class DecompilerAddFiles extends AddFilesToClassLoaderGroovy {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    StructContext structContext

    DecompilerAddFiles(StructContext structContext) {
        this.structContext = structContext
    }

    @Override
    void addFileImpl(File file) throws Exception {
        structContext.addSpace(file, false)
    }


    void addRtJar() {
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File rtJar = javaHome.child(Java8Files.rt_jar.customName);
        add(rtJar);
    }

    void addJfrJarsIfExists() {
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File jfrJar = javaHome.child('lib/jfr.jar');
        if (jfrJar.exists()) {
            add(jfrJar);
        }
    }

    void addToolsJar() {
        File file = new MavenCommonUtils().getToolsJarFile()
        if (file != null) {
            add file
        }
    }


    void addAllJdkJars() {
        addRtJar()
        addJfrJarsIfExists()
        addToolsJar()
    }

    @Override
    void addFilesFromGmrp() {
        GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.addedFiles2.each {
            try {
                add it
            } catch (Throwable e) {
                log.warn("failed add : ${it} ", e)
            }
        }

    }
}
