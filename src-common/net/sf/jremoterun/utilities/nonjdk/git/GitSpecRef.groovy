package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI;

import java.util.logging.Logger;

@CompileStatic
interface GitSpecRef extends ToFileRef2, ChildFileLazy, File2FileRefWithSupportI {

    GitSpec getGitSpec();


}
