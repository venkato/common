package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure

import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@CompileStatic
class JrrCoreCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            LatestMavenIds.swtWin,
            LatestMavenIds.eclipseWorkbench,
    ]

    File baseDir

    void prepare() {
        compilerPure.annotationProcessorManagerDummy = true
        compilerPure.javaVersion = '1.5'
        compilerPure.adder.addAll mavenIds
        compilerPure.addInDir new File(baseDir, 'src')
        compilerPure.outputDir = new File(baseDir, 'build/classes2')
        compilerPure.outputDir.mkdirs()
    }


    File zipp() {
        FileUtilsJrr.copyDirectoryToDirectory(new File(baseDir,"src/META-INF"),compilerPure.outputDir);
        File destJartmp = new File(baseDir, 'build/jremoterun_try.jar');

        assert destJartmp.parentFile.exists()
//        ZipUtil.pack(compilerPure.outputDir, destJartmp)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        archiver.dateOfAllFiles = GroovyCustomCompiler. defaultFileTime
        try {
            archiver.step1(destJartmp, false)
            archiver.step2(compilerPure.outputDir, [], [])
            archiver.stepFinish()
        }finally{
            archiver.stepFinalAlways()
        }
        destJartmp.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        File dest2Jar = new File(baseDir, 'build/jremoterun.jar');
        FileUtilsJrr.copyFile(destJartmp,dest2Jar)
        return dest2Jar
    }


}
