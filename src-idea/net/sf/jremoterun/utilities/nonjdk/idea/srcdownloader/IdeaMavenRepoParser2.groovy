package net.sf.jremoterun.utilities.nonjdk.idea.srcdownloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import net.sf.jremoterun.utilities.nonjdk.ivy.IvyDepResolver3
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl
import org.apache.ivy.core.report.ResolveReport

import java.util.logging.Logger

@CompileStatic
class IdeaMavenRepoParser2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public IvyDepResolver3 ivyRepo;

    void createIvyRepo(){
        ivyRepo = ManyReposDownloaderImpl.createRepo(MavenRepositoriesEnum.jetbrainsIdea) as IvyDepResolver3
    }


    static MavenIdAndRepo buildIdeaSourcesMavenId(String ideaVersion) {
        MavenId m = new MavenId('com.jetbrains.intellij.idea', 'ideaIC', ideaVersion);
        return new MavenIdAndRepo(m, MavenRepositoriesEnum.jetbrainsIdea)
    }

    /**
     * @param ideaVersion like 2020.1
     */
    static File downloadIdeaSource(String ideaVersion) {
        new IdeaMavenRepoParser2().downloadIdeaSource2(ideaVersion)
    }


    File downloadIdeaSource2(String ideaVersion) {
        MavenIdAndRepo ideaSourcesMavenId = buildIdeaSourcesMavenId(ideaVersion)
        createIvyRepo()
        ResolveReport rr = ivyRepo.downloadCustomPackage(ideaSourcesMavenId.m, IvyDepResolver3.sourcesS)
        List<File> files = ivyRepo.extractFilesFromReport(rr)
        if (files.size() == 0) {
            throw new FileNotFoundException("failed download source for ${ideaSourcesMavenId.m}")
        }
        return files[0];
    }

}
