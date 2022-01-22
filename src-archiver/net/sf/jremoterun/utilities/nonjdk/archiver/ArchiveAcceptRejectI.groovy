package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ArchiveAcceptRejectI {

    String map(String relativePath, File f,JrrCommonsArchiver archiver)

}
