package net.sf.jremoterun.utilities.nonjdk.classpath.refs.nonclasses

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions
import net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.JavaAgentOptionBuilder
import net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.JavaProcessRunner
import org.apache.commons.lang3.SystemUtils;

import java.util.logging.Logger;

@CompileStatic
class NpeFixAgent {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ToFileRef2 linkToRef;
    public static String javaArgStr;
    public static File2FileRefWithSupportI richNpeDir = GitSomeRefs.commonUtil.childL('richNpe');

    static File2FileRefWithSupportI buildRef(String suffix) {
        return richNpeDir.childL('richNPE64.' + suffix)
    }

    static ToFileRef2 getRefToRichNpeLib() {
        if (linkToRef != null) {
            return linkToRef
        }
        linkToRef = buildRef(getSuffix())
        return linkToRef
    }

    static String getSuffix() {
        String suffix1;
        if (SystemUtils.IS_OS_WINDOWS) {
            suffix1 = 'dll'
        } else {
            suffix1 = 'so'
        }
        return suffix1
    }

    static String buildPathString() {
        if (javaArgStr != null) {
            return javaArgStr
        }
        javaArgStr = JavaAgentOptionBuilder. createAgentArg(JavaCmdOptions.agentpath, getRefToRichNpeLib())
        return javaArgStr
    }
}
