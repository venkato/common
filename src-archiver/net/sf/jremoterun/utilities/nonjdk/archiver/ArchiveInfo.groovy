package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean2;

import java.util.logging.Logger;

@CompileStatic
class ArchiveInfo implements JavaBean {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DateWithFormat creationDate;
    String contentHash;
    String humanRemarks;
    List<String> includeFilter = []
    List<String> ignoreFilter = []
    List additionalInfoList = []
    Map additionalInfoMap = [:]


}
