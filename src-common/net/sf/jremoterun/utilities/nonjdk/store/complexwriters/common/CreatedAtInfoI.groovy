package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat;

import java.util.logging.Logger;

@CompileStatic
interface CreatedAtInfoI {

    DateWithFormat getCreatedDate();


}
