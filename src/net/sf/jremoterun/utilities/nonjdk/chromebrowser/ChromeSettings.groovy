package net.sf.jremoterun.utilities.nonjdk.chromebrowser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI;

import java.util.logging.Logger;

/**
 * chrome://settings/
 * chrome://version/
 */
@CompileStatic
enum ChromeSettings  implements ChildRedirect{
    Bookmarks,
    History,
    ;


    String childPath;

    ExactChildPattern ref;

    ChromeSettings() {
        this.childPath = name()
        ref = new ExactChildPattern(name())
    }

}
