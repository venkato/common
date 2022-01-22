package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef;

import java.util.logging.Logger;

@CompileStatic
enum JrrConfigDir2ToFileRef implements ToFileRef2, ChildFileLazy, ZeroOverheadFileRef, File2FileRefWithSupportI {
    jrrConfigDir2,
    filesDir,
    classesDir,
    ;

    @Override
    File resolveToFile() {
        File r;
        if (this == jrrConfigDir2) {
            r = JrrStarterVariables2.getInstance().jrrConfigDir2
        }
        if (this == filesDir) {
            r = JrrStarterVariables2.getInstance().filesDir
        }
        if (this == classesDir) {
            r = JrrStarterVariables2.getInstance().classesDir
        }
        if (r == null) {
            throw new IllegalAccessException("" + this)
        }
        return r
    }

    @Override
    File2FileRefWithSupportI childL(String child) {
        return new FileChildLazyRef(this, child)
    }

    @Override
    File2FileRefWithSupportI childP(ChildPattern child) {
        return new FileChildLazyRef(this, child)
    }
}
