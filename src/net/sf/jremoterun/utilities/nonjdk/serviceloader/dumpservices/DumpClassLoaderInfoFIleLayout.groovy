package net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum DumpClassLoaderInfoFIleLayout implements ChildRedirect, EnumNameProvider {

    servicesRaw,
    servicesSummaryGolden,
    servicesSummaryLatest,
    mutireleaseJarLatest,
    mutireleaseJarGolden,

    ;

    String customName;

    ExactChildPattern ref;

    DumpClassLoaderInfoFIleLayout() {
        this.customName = name() + ClassNameSuffixes.dotgroovy.customName
        ref = new ExactChildPattern(customName)
    }


}
