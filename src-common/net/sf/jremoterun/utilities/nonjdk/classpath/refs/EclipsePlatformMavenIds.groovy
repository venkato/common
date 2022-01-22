package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
enum EclipsePlatformMavenIds implements MavenIdContains {


    coreResource('org.eclipse.platform:org.eclipse.core.resources:3.14.0'),
    coreJobs('org.eclipse.platform:org.eclipse.core.jobs:3.11.0'),
    coreRuntime('org.eclipse.platform:org.eclipse.core.runtime:3.22.0'),
    equinoxPreference('org.eclipse.platform:org.eclipse.equinox.preferences:3.9.100'),
    coreContentType('org.eclipse.platform:org.eclipse.core.contenttype:3.7.1000'),
    osgi('org.eclipse.platform:org.eclipse.osgi:3.18.600'),



    text('org.eclipse.platform:org.eclipse.text:3.11.0'),
    draw2d('org.eclipse:draw2d:3.2.100-v20070529'),
    equinoxCommon('org.eclipse.platform:org.eclipse.equinox.common:3.14.100'),

    ;


    MavenId m;

    EclipsePlatformMavenIds(String mid) {
        m = new MavenId(mid);
    }

    public static List<EclipsePlatformMavenIds> all = values().toList()


}
