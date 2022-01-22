package net.sf.jremoterun.utilities.nonjdk.git.walktree.gitreport

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum GitReportColumns {
    commitDate,
    childCount,
    sha1,
    sha1Parent,
    rawBuffer,
    authorName,
    authorEmail,
    diffSummary,
    descriptionShort,
    descriptionLong,



}
