package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum K8sMavenIds implements MavenIdContains, EnumNameProvider {



    client_java_admission_review,
    client_java_api_fluent,
    client_java_api,
    client_java_spring_integration,
    client_java_proto,
    //client_java_examples_parent,
    client_java_examples_release_15,
    //client_java_parent,
    client_java_extended,
    client_java,
    //client_java_cert_manager_models,
    //client_java_prometheus_operator_models,
    //client_java_examples,
    //client_java_util,
    //e2e_tests
    ;


    MavenId m;

    K8sMavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId('io.kubernetes', artifactId, '16.0.1');
    }

    public static List<K8sMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
