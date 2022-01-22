package net.sf.jremoterun.utilities.nonjdk.linux

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum LinuxProcessFileLayout implements ChildRedirect, EnumNameProvider{
    cmdline,
    exe,
    /**
     * working dir
     */
    cwd,
    /**
     * memory mapping
     */
    maps,
    mountinfo,
    io,
    stat,
    statm,
    status,
    ;

    ExactChildPattern ref;
    String customName;


    LinuxProcessFileLayout() {
        customName=name()
        ref=new ExactChildPattern(customName)
    }



}
