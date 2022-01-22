package net.sf.jremoterun.utilities.nonjdk.net.ssl.bc

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.BouncyCastleMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds;

import java.util.logging.Logger;

@CompileStatic
class SslBcMavenId {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? extends MavenIdContains> mavenIds = [
            BouncyCastleMavenIds.bcutil_jdk15to18,
            BouncyCastleMavenIds.bctls_jdk15on,
            BouncyCastleMavenIds.bcprov_jdk15on,
            LatestMavenIds.log4jOld,
            LatestMavenIds.junit,
            LatestMavenIds.commonsCodec,
    ]

}
