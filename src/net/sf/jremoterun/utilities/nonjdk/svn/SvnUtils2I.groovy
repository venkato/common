package net.sf.jremoterun.utilities.nonjdk.svn

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface SvnUtils2I {

    long exportSvnRefImpl(String svnRef, File workingCopyDirectory);


    long checkoutSvnRefImpl(String svnRef, File workingCopyDirectory) ;

}
