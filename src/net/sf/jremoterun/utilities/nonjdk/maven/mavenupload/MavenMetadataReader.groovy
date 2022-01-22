package net.sf.jremoterun.utilities.nonjdk.maven.mavenupload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.GeneralBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIds
import net.sf.jremoterun.utilities.nonjdk.maven.MavenPomUtils

import org.apache.maven.artifact.repository.metadata.Metadata
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class MavenMetadataReader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static class MavenDependencies {
        public static List<? extends MavenIdContains> mavenIds = [MavenMavenIds.resolver_provider, LatestMavenIds.plexus_utils,]
    }

    public boolean readStrict = true
    public MetadataXpp3Reader metadataXpp3Reader = new MetadataXpp3Reader()
    public static SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMddHHmmss')

    Metadata readMavenMetaData(IBiblioRepository repo, MavenId mavenId) {
        URL url = buildUrl(repo, mavenId)
        return readMavenMetaData(url)
    }

    URL buildUrl(IBiblioRepository repo, MavenId mavenId) {
        String repoPrefix
        if (repo == null) {
            repoPrefix = MavenDefaultSettings.mavenDefaultSettings.mavenServer
        } else {
            repoPrefix = repo.getUrl()
        }
        String suffix1 = MavenPomUtils.buildMetadataSuffix(mavenId);
        return new URL("${repoPrefix}/${suffix1}")
    }

    @Deprecated
    String buildSuffix(MavenId mavenId) {
        return MavenPomUtils.buildMetadataSuffix(mavenId)
    }

    Metadata readMavenMetaData(MavenIdAndRepoContains mavenIdAndRepo) {
        readMavenMetaData(MavenPomUtils.buildMetadataURL(mavenIdAndRepo))
    }

    String getLatestVersion(MavenIdContains mavenId) {
        GeneralBiblioRepository biblioRepository = new GeneralBiblioRepository(MavenDefaultSettings.mavenDefaultSettings.mavenCentralRepoName, MavenDefaultSettings.mavenDefaultSettings.mavenServer);
        MavenIdAndRepo mavenIdAndRepo1 = new MavenIdAndRepo(mavenId.m, biblioRepository);
        String versionLatest1 = getLatestVersion(mavenIdAndRepo1)
        return versionLatest1
    }

    String getLatestVersion(MavenIdAndRepoContains mavenIdAndRepo) {
        Metadata mavenMetaData = readMavenMetaData(MavenPomUtils.buildMetadataURL(mavenIdAndRepo))
        return getLatestVersion2(mavenMetaData)
    }

    String getLatestVersion2(Metadata mavenMetaData) {
        if (mavenMetaData.versioning.latest != null) {
            return mavenMetaData.versioning.latest
        }
        if (mavenMetaData.versioning.release != null) {
            return mavenMetaData.versioning.release
        }
        return mavenMetaData.version
    }

    Metadata readMavenMetaData(URL url) {
        String text = url.text
        try {
            return readMavenMetaData(text)
        } catch (Exception e) {
            log.info("failed read ${text} ${e}")
            throw e;
        }
    }

    Metadata readMavenMetaData(String text) {
        StringReader reader = new StringReader(text)
        Metadata metadata1 = metadataXpp3Reader.read(reader, readStrict);
        return metadata1
    }

    static Date readUpdateDate(Metadata metadata) {
        synchronized (sdf) {
            return sdf.parse(metadata.versioning.getLastUpdated())
        }
    }


    String checkUsedLatestVersion2(MavenIdAndRepoContains mavenIdAndRepo) {
        String latest = getLatestVersion(mavenIdAndRepo)
        MavenId mavenId1 = mavenIdAndRepo.mavenIdAndRepo.m
        if (mavenId1.version == latest) {
            return null
        }
        return latest
    }

    List<String> checkUsedLatestVersions1(List<? extends MavenIdContains> mavenIdAndRepo) {
        List<String> newVersions = []
        mavenIdAndRepo.each {
            try {
                String versionLatest1 = getLatestVersion(it)
                //if (versionLatest1 != it.m.version) {
                if (!isVersionEquals(it, it.m.version, versionLatest1)) {
                    newVersions.add("${it} latest = ${versionLatest1}".toString())
                }

            } catch (Throwable e) {
                log.info("failed check new version for ${it} : ${e}")
                String msg = onException(it, e)
                if (msg != null) {
                    newVersions.add("${it} error: ${msg}".toString())
                }
            }
        }
        return newVersions
    }

    boolean isVersionEquals(MavenIdContains mavenIdContains, String existed, String fromMetadata) {
        return existed == fromMetadata
    }
    //         log.info new net.sf.jremoterun.utilities.nonjdk.maven.mavenupload.MavenMetadataReader().checkUsedLatestVersions1(LatestMavenIds.values().toList()).join('\n')

    String onException(MavenIdAndRepoContains it, Throwable e) {
        onException(it.mavenIdAndRepo.m, e)
    }

    String onException(MavenIdContains it, Throwable e) {
        //log.info "onExc ${e}"
        if (e instanceof FileNotFoundException) {
            FileNotFoundException eee = (FileNotFoundException) e;
            return "${eee}"
        } else {
            //  log.info "rethrow ${e}"
            throw e
        }
    }

    List<String> checkUsedLatestVersions2(List<? extends MavenIdAndRepoContains> mavenIdAndRepo) {
        List<String> newVersions = []
        mavenIdAndRepo.each {
            try {
                String versionLatest1 = getLatestVersion(it)
                //if (versionLatest1 != it.getMavenIdAndRepo().m.version) {
                if (!isVersionEquals(it.getMavenIdAndRepo().m, it.getMavenIdAndRepo().m.version, versionLatest1)) {
                    newVersions.add("${it} latest = ${versionLatest1}".toString())
                }
            } catch (Throwable e) {
                log.info("failed check new version for ${it} : ${e}")
                String msg = onException(it, e)
                if (msg != null) {
                    newVersions.add("${it} error: ${msg}".toString())
                }

            }
        }
        return newVersions
    }

    void checkUsedLatestVersion(MavenIdAndRepoContains mavenIdAndRepo) {
        String latest = getLatestVersion(mavenIdAndRepo)
        MavenId mavenId1 = mavenIdAndRepo.mavenIdAndRepo.m
        assert mavenId1.version == latest
    }

}
