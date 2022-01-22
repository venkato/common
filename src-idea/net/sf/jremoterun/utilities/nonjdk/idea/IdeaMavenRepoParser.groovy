package net.sf.jremoterun.utilities.nonjdk.idea;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import net.sf.jremoterun.utilities.nonjdk.idea.srcdownloader.IdeaMavenRepoParser2
import net.sf.jremoterun.utilities.nonjdk.ivy.IvyDepResolver3
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl
import org.apache.ivy.core.report.ResolveReport
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements;

import java.util.logging.Logger;

@CompileStatic
class IdeaMavenRepoParser {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static URL ideaRepo = new URL(MavenRepositoriesEnum.jetbrainsIdea.url)

    public String searchAllWithSources = 'a[href$=-sources.jar]'
    public String searchAllPoms = 'a[href$=.pom]'
    public String defaultSearchPattern = searchAllPoms
    public String pageText;
    public List<MavenId> foundMavenIds;

    IdeaMavenRepoParser() {
        this(ideaRepo.text)
    }

    IdeaMavenRepoParser(String pageText) {
        this.pageText = pageText
    }

    @Deprecated
    static MavenIdAndRepo buildIdeaSourcesMavenId(String ideaVersion) {
        return IdeaMavenRepoParser2.buildIdeaSourcesMavenId(ideaVersion)
    }

    List<MavenId> findCurrentVersions() {
        String extended = CurrentIdeaVersionUtils.findCurrentIdeaVersionExtended()
        return foundMavenIds.findAll { it.version == extended }
    }

    /**
     * @param ideaVersion like 2020.1
     */
    static File downloadIdeaSource(String ideaVersion) {
        if (ideaVersion == null) {
            ideaVersion = CurrentIdeaVersionUtils.findCurrentIdeaVersion()
        }
        return IdeaMavenRepoParser2.downloadIdeaSource(ideaVersion)
    }

    Elements findEls(Document doc1) {
        return doc1.select(defaultSearchPattern)
    }

    void findMavenIds() {
        Document doc = Jsoup.parse(pageText);
        Elements els = findEls(doc)
        String repoS = ideaRepo.toString();
        if (!repoS.endsWith('/')) {
            repoS += '/'
        }
        List<String> ll = els.collect { it.attr('href') }.collect { it.replace(repoS, '') }
        foundMavenIds = ll.collect { buildId(it) }
    }

    MavenId buildId(String s) {
        List<String> tokenize1 = s.tokenize('/')
        tokenize1.remove(tokenize1.size() - 1);
        String version = tokenize1.remove(tokenize1.size() - 1);
        String artifact = tokenize1.remove(tokenize1.size() - 1);
        String group = tokenize1.join('.');
        return new MavenId(group, artifact, version)
    }

    List<MavenIdAndRepo> convertMavenIdsToRepo(List<MavenId> mavenIds) {
        List<MavenIdAndRepo> mavenIdAndRepos = mavenIds.collect {
            new MavenIdAndRepo(it, MavenRepositoriesEnum.jetbrainsIdea)
        }
        return mavenIdAndRepos
    }

}
