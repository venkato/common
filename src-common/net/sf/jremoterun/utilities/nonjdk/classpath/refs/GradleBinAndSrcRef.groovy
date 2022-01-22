package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.BinaryWithSource3
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithChildPattern;

import java.util.logging.Logger;

@CompileStatic
class GradleBinAndSrcRef extends BinaryWithSource3 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GradleBinAndSrcRef(ChildFileLazy gradleBase, String childName) {
        super(gradleBase.childP(new StartWithChildPattern('gradle-')).childL('lib').childP(new StartWithChildPattern("gradle-${childName}-")), gradleBase.childP(new StartWithChildPattern('gradle-')).childL('src/' + childName))
    }
}
