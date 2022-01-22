package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout

import java.util.logging.Logger

@CompileStatic
class IdeaInitPluginCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    IdeaInitPluginCompiler() {
    }

    @Override
    void prepare() {
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        //adder.add getJrrUtilsJar()
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)

    }

    void compile3(File ideaDir) {
        prepare(ideaDir)
        compile()
        postCompileStep()
    }

    @Override
    void compile() {
        log.info "compiling ..."
        super.compile()
    }

    static File getJrrUtilsJar() {
        JrrUtilsCompiler compiler = new JrrUtilsCompiler()
        compiler.all2()
        compiler.zipp()
        if(GroovyMethodRunnerParams.gmrp!=null) {
            if (GroovyMethodRunnerParams.gmrpn.grHome != null) {
                File child = new File(GroovyMethodRunnerParams.gmrpn.grHome, JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.customName);
                if (child.exists()) {
                    return child
                }
                log.info "file not found : ${child}"
            }
        }
        File f =  JrrStarterJarRefs.jrrutilitiesOneJar.resolveToFile()
        return f
    }

    void prepare(File ideaDir) {
        prepare2(ideaDir,client.adder)
    }

    void prepare2(File ideaDir,AddFilesToClassLoaderGroovy adder) {
        assert ideaDir.exists()
        adder.addAllJarsInDir IdeaFilesLayout.lib.ref.resolveChild(ideaDir);
        adder.addAllJarsInDir IdeaFilesLayout.groovyLib.ref.resolveChild(ideaDir);
        params.outputDir = new File(client.ifDir, 'build/ideainitpluginbuild')
        params.outputDir.mkdirs()
        params.javaVersion = '1.6'
        params.needCustomJrrGroovyFieldsAccessors = false
        params.addInDir IfFrameworkSrcDirs.src_idea.childL('net/sf/jremoterun/utilities/nonjdk/idea/init');
    }

    void testUpdateIdeaJar(File tmpJar, File metaInf, File targetJar) {
        assert targetJar.getParentFile().exists()
        testUpdateIdeaJar2(tmpJar, metaInf)
        assert targetJar.getParentFile().exists()
        FileUtilsJrr.copyFile(tmpJar, targetJar)
        assert FileCheckSumCalc.calcSha256ForFile(tmpJar) == FileCheckSumCalc.calcSha256ForFile(targetJar)
        tmpJar.delete()
        log.info "file updated : ${targetJar}"
    }

    void testUpdateIdeaJar2(File tmpJar, File metaInf) {
//        JdkLogFormatter.setLogFormatter()
        tmpJar.delete()
        assert !tmpJar.exists()

        assert metaInf.directory
        FileUtilsJrr.copyDirectoryToDirectory(metaInf, params.outputDir)
//        ZipUtil.pack(params.outputDir, tmpJar)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(tmpJar,params.outputDir)
        tmpJar.setLastModified(GroovyCustomCompiler. defaultDate.getTime())

    }

}

