package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class GroovyCompilerReply implements Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Collection<String> usedClasses;

    HashSet<String> lookupedByGroovyClasses
    List<String> groovyMethodsAddedTo
    List<String> groovyMethodsSkipped
}
