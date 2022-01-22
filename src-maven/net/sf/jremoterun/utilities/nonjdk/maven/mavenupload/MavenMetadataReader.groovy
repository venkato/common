package net.sf.jremoterun.utilities.nonjdk.maven.mavenupload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.MavenMavenIds
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
        String suffix1 = buildSuffix(mavenId);
        return new URL("${repoPrefix}/${suffix1}")
    }

    String buildSuffix(MavenId mavenId) {
        //URL url = new URL('https://repo1.maven.org/maven2/ch/qos/logback/logback-core/maven-metadata.xml');
        return "${mavenId.groupId.replace('.', '/')}/${mavenId.artifactId}/maven-metadata.xml"
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
        return sdf.parse(metadata.versioning.getLastUpdated())
    }


}
