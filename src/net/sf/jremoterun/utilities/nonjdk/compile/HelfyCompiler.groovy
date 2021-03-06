package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2

import java.util.logging.Logger

@CompileStatic
class HelfyCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = client.ifDir
        }
        params.printWarning = false
        params.outputDir = baseDir.child('build/helfyUtils')
        params.outputDir.mkdirs()

        client.adder.add(JrrClassLocationRefs.JavaVMClient1)
        client.adder.add(JrrClassLocationRefs.JdkLogFormatter1)
        client.adder.add mcu.getToolsJarFile()
        addInDir GitReferences.helfySrc
        addInDir GitReferences.helfyTest
        addInDir baseDir.child(IfFrameworkSrcDirs.src_helfyutils.dirName)
        params.javaVersion = '1.8'
    }


}
