package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.BinaryWithSource3
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithThenNumberChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef;

import java.util.logging.Logger;

@CompileStatic
class GradleBinAndSrcRef extends BinaryWithSource3 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GradleBinAndSrcRef(ChildFileLazy gradleBase, String childName) {
        super(buildRefToLibDir(gradleBase).childP(new StartWithThenNumberChildPattern("gradle-${childName}-")), gradleBase.childP(new StartWithChildPattern('gradle-')).childL('src/' + childName))
    }

    static File2FileRefWithSupportI buildRefToLibDir(ChildFileLazy gradleBase) {
        return gradleBase.childP(new StartWithChildPattern('gradle-')).childL('lib')
    }

    static BinaryWithSource3 baseZip(UnzipRef gradleBase, String childName, boolean isPlugin) {
        return base1(gradleBase, childName, isPlugin)
    }

    static BinaryWithSource3 base1(ChildFileLazy gradleBase, String childName, boolean isPlugin) {
        return base2Flatten(gradleBase.childP(new StartWithChildPattern('gradle-')), childName, isPlugin)
    }

    static BinaryWithSource3 base2Flatten(File gradleBase, String childName, boolean isPlugin) {
        return base2Flatten(new FileToFileRef(gradleBase), childName, isPlugin)
    }

    static BinaryWithSource3 base2Flatten(ChildFileLazy gradleBase, Gradle7LibsRefs gradle7Refs) {
        return base2Flatten(gradleBase, gradle7Refs.getCustomName(), false)
    }

    static BinaryWithSource3 base2Flatten(ChildFileLazy gradleBase, Gradle7PluginsRefs gradle7Refs) {
        return base2Flatten(gradleBase, gradle7Refs.getCustomName(), true)
    }

    static BinaryWithSource3 base2Flatten(ChildFileLazy gradleBase, String childName, boolean isPlugin) {
        String suffix = 'lib'
        if (isPlugin) {
            suffix += '/plugins'
        }
        return new BinaryWithSource3(gradleBase.childL(suffix).childP(new StartWithThenNumberChildPattern("gradle-${childName}-")), gradleBase.childL('src/' + childName))
    }
}
