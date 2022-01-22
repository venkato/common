package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.GeneralBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenRetryDownloader
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl
import net.sf.jremoterun.utilities.nonjdk.ivy.MavenManyReposDownloader
import org.apache.ivy.plugins.resolver.IBiblioResolver

import java.util.logging.Logger

@CompileStatic
class DropshipDown3 implements ClassNameSynonym{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static IvyDepResolver2 findIvyDepResolver2(){
        return findIvyDepResolver3(MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver)
    }

    static IvyDepResolver2 findIvyDepResolver3(MavenDependenciesResolver ivyDepResolver2){
        if(ivyDepResolver2==null){
            throw new NullPointerException('ivyDepResolver2 arg is null')
        }
        if (ivyDepResolver2 instanceof IvyDepResolver2) {
            return  (IvyDepResolver2) ivyDepResolver2;
        }
        if (ivyDepResolver2 instanceof MavenRetryDownloader) {
            MavenRetryDownloader rr = ivyDepResolver2
            return findIvyDepResolver3( rr.impl)
        }
        if (ivyDepResolver2 instanceof MavenManyReposDownloader) {
            MavenManyReposDownloader rr     = (MavenManyReposDownloader) ivyDepResolver2;
            return findIvyDepResolver3(rr.getDefaultRepo())
        }
        throw new Exception("Strange class ${ivyDepResolver2.getClass().getName()}")
    }


    void downloadD(MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        MavenDependenciesResolver ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        setIvyLogLevel(info)
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies)
        log.info "${dependencies}"

    }



    void downloadD2(MavenRepositoriesEnum mavenRepo, MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        MavenDependenciesResolver ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        setIvyLogLevel(info)
//        ivyDepResolver2.addResolverAfterInit(mavenRepo)
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies,mavenRepo)
        log.info "${dependencies}"

    }

    void setIvyLogLevel(boolean info){
        IvyDepResolver2 ivyDepResolver2 = findIvyDepResolver2()
        ivyDepResolver2.setLogLevel(                info?                        org.apache.ivy.util.Message.MSG_INFO:org.apache.ivy.util.Message.MSG_DEBUG)
        if(!info) {
            ivyDepResolver2.setLogDebug()
        }
    }

    void downloadD(String mavenRepo, MavenId mavenId2, boolean downloadSource, boolean downloadDepenencies,boolean info) {
        MavenDependenciesResolver ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
        setIvyLogLevel(info)
        GeneralBiblioRepository repoCustom = new GeneralBiblioRepository('custom', mavenRepo)
//        IBiblioResolver custom = ivyDepResolver2.buildPublicIbiblioCustom('custom', mavenRepo)
//        ivyDepResolver2.addResolverAfterInit(custom)
        List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(mavenId2, downloadSource, downloadDepenencies,repoCustom)
        log.info "${dependencies}"
    }

    /**
     * Fine maven id by file
     * @See net.sf.jremoterun.utilities.nonjdk.classpath.FindMavenIdsAndDownload#findMavenIdsAndDownload7
     */
    private void doNothing1(){}

}
