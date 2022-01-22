package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout

import java.util.logging.Logger

@CompileStatic
class SftpLoaderCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.sshjHierynomus,
            CustObjMavenIds.slf4jApi,
            CustObjMavenIds.slf4jJdkLogger,
            CustObjMavenIds.commonsIo,
            LatestMavenIds.sshjEddsa,

    ]



    SftpLoaderCompiler() {
    }


    void prepare() {
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.groovy_custom.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)


//        client.adder.add mcu.getToolsJarFile()


        params.printWarning = false;
//        params.addInDir new File(baseDir, "src")
        params.outputDir = new File(client.ifDir, 'build/sftp_loader')
        params.outputDir.mkdirs()
        params.javaVersion = '1.8'

        addInDir(client.ifDir.child(IfFrameworkSrcDirs.src_sftp_base_files_copy.dirName))

    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        adder.addAll DropshipClasspath.allLibsWithoutGroovy
        adder.addAll mavenIds
    }

    void zipp(File destJar) {
        JrrJarArchiver jarArchiver = new JrrJarArchiver(true)
        jarArchiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        try {
            jarArchiver.step1(destJar, false)
            jarArchiver.step2(params.outputDir)
            jarArchiver.stepFinish()
        }finally{
            jarArchiver.stepFinalAlways()
        }
    }
}

