package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum JavaCmdOptions11 implements EnumNameProvider{

    add_opens,
    add_reads,
    illegal_access,
    add_modules,
    ;

    String customName;

    JavaCmdOptions11() {
        customName = '--'+name().replace('_','-')
    }
}
