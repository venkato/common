package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.oro.text.GlobCompiler;

import java.util.logging.Logger
import java.util.regex.Pattern;

@CompileStatic
class HumanRegexpFileNameFiler implements FilenameFilter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Pattern pattern

    HumanRegexpFileNameFiler(String s) {
        String perl5 = GlobCompiler.globToPerl5(s.toCharArray(), 0)
        pattern= Pattern.compile(perl5)
    }

    HumanRegexpFileNameFiler(Pattern pattern) {
        this.pattern = pattern
    }



    @Override
    boolean accept(File dir, String name) {
        return pattern.matcher(name).matches()
    }
}
