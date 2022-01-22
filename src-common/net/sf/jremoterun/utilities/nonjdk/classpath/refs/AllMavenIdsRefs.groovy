package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath;

import java.util.logging.Logger;

@CompileStatic
class AllMavenIdsRefs {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<Class> refsEnumsDefault =(List)[
            LatestMavenIds, AntMavenIds, AsmOw, BouncyCastleMavenIds, CustObjMavenIds, DerbyMavenIds, GitMavenIds, Gradle5MavenIds,
            GroovyMavenIds, KafkaMavenIds, KotlinMavenIds, Log4j2MavenIds, MaryDependentMavenIds, MaryTtsMavenIds, MavenMavenIds, MigLayoutMavenIds,
            NettyMavenIds, NexusSearchMavenIds, Pi4j, ProguardMavenIds, SquirrelSqlMavenIds, SshdMavenIds, TwelvemonkeysImageioMavenIds,
            Okhttp3MavenIds, DockerMavenIds, HttpClient5ApacheMavenIds, HttpClient5CoreApacheMavenIds,
            DropshipClasspath, K8sMavenIds, NetbeansMavenIds, JungGraphSwing, NettySslNativeMavenIds,
            EclipsePlatformMavenIds,MavenIverseMavenIds,
            // need add  TomcatMavenIds ?
    ]

    public final List<Class> refsEnums1;
    /**
     * List constsis of 2 objects
     * MavenIdContains
     * MavenIdAndRepoContains
     */
    public List<ToFileRef2> refsEnumsList;

    AllMavenIdsRefs() {
        this(refsEnumsDefault)
    }

    AllMavenIdsRefs(List<Class> refsEnums1) {
        this.refsEnums1 = refsEnums1
    }

    List<ToFileRef2>  buildEnumList(){
        if(refsEnumsList==null) {
            List<Object[]> collect1 = (List) refsEnums1.collect { JrrClassUtils.getEnumValues(it) };
            List list1 = [];
            collect1.each {  addObjArrayToList(list1,it) }
//            collect1.each { list1.addAll((List)it.toList()) }
            refsEnumsList =(List) list1;
        }
        return refsEnumsList
    }

    private static void addObjArrayToList(List list1,Object[] arr){
        list1.addAll arr.toList()
    }

    Map<MavenId,Object> buildMapWithVersion(){
        Map<MavenId,Object> res = [:]
        buildEnumList().each {res.put(convertToMavenId(it), it)}
        return res;
    }


    Map<String,Object> buildMapWithoutVersion(){
        Map<String,Object> res = [:]
        Map<MavenId, Object> version = buildMapWithVersion()
        version.entrySet().each {
            res.put(convertMavenId(it.key),it.value)
        }
        return res;
    }

    MavenId convertToMavenId(Object object){
        if (object instanceof MavenIdContains) {
             return object.m;
        }
        if (object instanceof MavenIdAndRepoContains) {
            return object.mavenIdAndRepo.m;
        }
        throw new UnsupportedOperationException("${object}")
    }


    String convertMavenId(MavenId m){
        return m.groupId+':'+m.artifactId;
    }


}
