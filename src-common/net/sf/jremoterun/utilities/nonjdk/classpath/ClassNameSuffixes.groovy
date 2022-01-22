package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum ClassNameSuffixes implements EnumNameProvider{

    dotclass,
    dotgroovy,
    dotjava,
    dotjar,
    ;

    String customName;

    ClassNameSuffixes() {
        customName = name().replace('dot','.')
    }

    @Override
    String toString() {
        return customName
    }
}
