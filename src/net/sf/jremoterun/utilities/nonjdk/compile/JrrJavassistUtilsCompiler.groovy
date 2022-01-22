package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.zeroturnaround.zip.NameMapper
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JrrJavassistUtilsCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public ClRef defaultRepositoryCacheManagerClRef = new ClRef('org.apache.ivy.core.cache.DefaultRepositoryCacheManager')

    JrrJavassistUtilsCompiler() {
        //new CopyOnUsage(JrrStarterJarRefs.jrrutilitiesOneJar).toString()
    }

    public static List mavenIds =  [
            LatestMavenIds.log4jOld
    ]

    void prepare() {
        params.needCustomJrrGroovyFieldsAccessors = false
        params.javaVersion = '1.7'
        params.annotationProcessorManagerDummy = true;
//        client.adder.addMavenPath DropshipClasspath.sisiFileBin
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        adder.addAll DropshipClasspath.allLibsWithGroovy
        adder.addAll mavenIds
        adder.add JrrStarterJarRefs2.java11base
        File toolsJar = mcu.getToolsJarFile()
        if(toolsJar.exists()) {
            client.adder.add toolsJar
        }

    }

    void updateDefaultRepositoryCacheManager(File srcDir ){
        String suffix = defaultRepositoryCacheManagerClRef.getClassPath()+ ClassNameSuffixes.dotjava.customName
        File childF = srcDir.child(suffix)
        childF.delete()
        assert !childF.exists()
        childF.parentFile.mkdirs()
        assert childF.parentFile.exists()
        MavenCommonUtils mcu = new MavenCommonUtils();
        mcu.fileType = MavenFileType2.source.fileSuffix
        File f =mcu.findMavenOrGradle(DropshipClasspath.ivyMavenId.m)
        log.info "makeing class ${defaultRepositoryCacheManagerClRef} public from ${f}"
        assert f!=null : DropshipClasspath.ivyMavenId.toString()
        NameMapper nameMapper = new NameMapper() {
            @Override
            String map(String name) {
                if(name==suffix){
                    return name
                }
                return null
            }
        }
        ZipUtil.unpack(f,srcDir,nameMapper)
        assert childF.exists()
        childF.text = childF.text.replace('private','public')
        addInDir(srcDir)
    }

    void zip(File destJar ){
        File tmpJar = params.outputDir.parentFile.child('jrrassist.jar')
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        archiver.dateOfAllFiles = GroovyCustomCompiler. defaultFileTime
        archiver.createSimple(tmpJar,params.outputDir)
        tmpJar.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
//        ZipUtil.pack(params.outputDir, tmpJar)
        FileUtilsJrr.copyFile(tmpJar,destJar)
        log.info "copied to ${destJar}"
    }

}
