package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LineInfo implements LineInfoI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Brakets brakets;

    String lineContent;

    LineInfo(Brakets brakets, String lineContent) {
        this.brakets = brakets
        this.lineContent = lineContent
        assert lineContent!=null
    }

    @Override
    String toString() {
        return lineContent
    }
}
