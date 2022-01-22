package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum ApacheArrowMavenIds  implements MavenIdContains, EnumNameProvider {

    flight_sql_jdbc_driver,
    flight_sql_jdbc_core,
    flight_sql,
    flight_integration_tests,
    flight_core,
    arrow_vector,
    arrow_tools,
    arrow_performance,
    arrow_memory_unsafe,
    arrow_memory_netty_buffer_patch,
    arrow_memory_netty,
    arrow_memory_core,
    arrow_jdbc,
    arrow_format,
    arrow_dataset,
    arrow_compression,
    arrow_c_data,
    arrow_avro,
    arrow_algorithm,

    ;

    String  customName;
    MavenId m;

    ApacheArrowMavenIds() {
        customName = name().replace('_','-')
        m = new MavenId('org.apache.arrow',customName,'18.3.0')
    }


}
