package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
public enum CustObjMavenIds implements MavenIdContains {

    commnonsLang('commons-lang:commons-lang:2.6')
    , git(GitMavenIds.jgit.getM().toString())
    , compressAbstraction('org.rauschig:jarchivelib:1.2.0')
    , zeroTurnaroundZipUtil('org.zeroturnaround:zt-zip:1.17')
    , junrar1('com.github.junrar:junrar:7.5.5')
    , slf4jApi('org.slf4j:slf4j-api:2.0.16')
    , slf4jJdkLogger('org.slf4j:slf4j-jdk14:2.0.16')
    , commonsLoggingMavenId('commons-logging:commons-logging:1.3.4')
    // https://en.wikipedia.org/wiki/Java_backporting_tools
    // 3.27.0 need java 9+
    , eclipseJavaCompiler('org.eclipse.jdt:ecj:3.26.0')
    , eclipseJavaAstParser('org.eclipse.jdt:org.eclipse.jdt.core:3.26.0')
    // TODO use eclipseJavaAstParser
    /**
     * 2.5 supports jdk 6+.
     * 2.6 supports 7+.
     * in 2.8.0 FileUtils.Copy changed. Sometime throw error when override file
     */
    , commonsIo('commons-io:commons-io:2.17.0')
    // svn deps
    ,
     @Deprecated
    antlrRuntime('org.antlr:antlr-runtime:3.5.3')
    ,
    ;


    MavenId m;

    CustObjMavenIds(String m2) {
        this.m = new MavenId(m2)
    }


    @Deprecated
    public static List<CustObjMavenIds> all = values().toList()

}