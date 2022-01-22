package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Java11ModuleStructureFile implements EnumNameProvider {
    bin,
    /**
     * Clash with
     * @see Class#getClasses
     */
    classes3,
    conf,
    include,
    legal,
    lib,

    ;


    String customName;

    Java11ModuleStructureFile() {
        customName = name().replace('3','')
    }


}
