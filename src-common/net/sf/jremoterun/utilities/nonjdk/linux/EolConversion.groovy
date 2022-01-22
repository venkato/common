package net.sf.jremoterun.utilities.nonjdk.linux

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum EolConversion {
    linux('\n'),
    windows('\r\n'),
    asIs(null),

    ;

    public String eol;

    EolConversion(String eol) {
        this.eol = eol
    }
}
