package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

import java.util.logging.Logger

@CompileStatic
enum Log4j2MavenIds implements MavenIdContains , EnumNameProvider{


    api,
    core,
    jcl,
    jul,
    slf4j_impl,
    to_jul,
    ;


    MavenId m;

    Log4j2MavenIds() {
        String artifactId = name().replace('_', '-')
        String version1;
                version1 = '2.21.1' // works, next version throws below error in idea
        //version1 = '2.25.0' // Exception java.lang.NoSuchMethodError: 'java.lang.Object org.apache.logging.log4j.util.LoaderUtil.newCheckedInstanceOfProperty(java.lang.String, java.lang.Class, java.util.function.Supplier)'     at org.apache.logging.log4j.core.LoggerContext.createInstanceFromFactoryProperty(LoggerContext.java:116)
//        version1 = '2.23.1' // Exception java.lang.NoSuchMethodError: 'java.lang.Object org.apache.logging.log4j.util.LoaderUtil.newCheckedInstanceOfProperty(java.lang.String, java.lang.Class, java.util.function.Supplier)'

        m = new MavenId("org.apache.logging.log4j:log4j-${artifactId}:${version1}");

    }

    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
