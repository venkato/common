package net.sf.jremoterun.utilities.nonjdk.backup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface BackupElementDescI {

    String name()

    File getFile();

}
