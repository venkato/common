package net.sf.jremoterun.utilities.nonjdk.maven

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.nonjdk.maven.MavenUrlSuffixes;

import java.util.logging.Logger;

@CompileStatic
class MavenPomUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    static String buildPomSuffix(MavenId mavenId) {
        String childPath = mavenId.groupId.replace('.', '/') + '/' + mavenId.artifactId + '/' + mavenId.version + '/' + mavenId.artifactId + '-' + mavenId.version + MavenUrlSuffixes.pom.customName;
        return childPath
    }


    static String buildMetadataSuffix(MavenId mavenId) {
        //URL url = new URL('https://repo1.maven.org/maven2/ch/qos/logback/logback-core/maven-metadata.xml');
        return "${mavenId.groupId.replace('.', '/')}/${mavenId.artifactId}/${MavenUrlSuffixes.metadata.customName}"
    }


    static URL buildPomURL(MavenIdAndRepoContains mavenIdAndRepo){
        return contactURLs( mavenIdAndRepo.mavenIdAndRepo.repo.url,buildPomSuffix(mavenIdAndRepo.mavenIdAndRepo.m))
        //return new URL(s.replace('//','/'))
    }

    static URL buildMetadataURL(MavenIdAndRepoContains mavenIdAndRepo){
        return   contactURLs(mavenIdAndRepo.mavenIdAndRepo.repo.url,buildMetadataSuffix(mavenIdAndRepo.mavenIdAndRepo.m))
        //return new URL(s.replace('//','/'))
    }


    static URL contactURLs(String part1,String part2){
        part2= part2.replace('//','/');
        if(part2.startsWith('/')){
            part2=part2.substring(1);
        }
        String v
        if(part1.endsWith('/')){
            v = part1+part2
        }else{
            v = part1+'/'+part2
        }
        return new URL(v)

    }

}