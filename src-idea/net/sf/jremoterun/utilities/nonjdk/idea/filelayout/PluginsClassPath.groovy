package net.sf.jremoterun.utilities.nonjdk.idea.filelayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider


@CompileStatic
enum PluginsClassPath implements ChildRedirect, EnumNameProvider {

    Groovy__s__lib__s__Groovy, 
    java__s__lib__s__java__t__impl, 
    java__s__lib__s__jb__t__jdi, 
    java__s__lib__s__jps__t__launcher, 
    java__s__lib__s__modules__s__intellij__d__java__d__compiler,
    java__s__lib__s__modules__s__intellij__d__java__d__debugger__d__impl,
    java__s__lib__s__modules__s__intellij__d__java__d__debugger,
    java__s__lib__s__modules__s__intellij__d__java__d__execution__d__impl,
    java__s__lib__s__modules__s__intellij__d__java__d__execution,
    java__s__lib__s__modules__s__intellij__d__java__d__impl,
    java__s__lib__s__modules__s__intellij__d__platform__d__jps__d__build,
    Kotlin__s__lib__s__javax__t__inject, 
    Kotlin__s__lib__s__jps__s__kotlin__t__jps__t__plugin, 
    Kotlin__s__lib__s__kotlinc__d__kotlin__t__compiler__t__common,
    

    ;

    String customName;

    ExactChildPattern ref;

    PluginsClassPath() {
        this.customName = name().replace('__s__','/').replace('__d__','.').replace('__t__','-')+'.jar'
        ref = new ExactChildPattern('plugins/'+customName)

    }


}
