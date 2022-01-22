package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef;

import java.util.logging.Logger;

@CompileStatic
class FailedParse {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LocationRef locationRef
    public String errorMsg

    FailedParse(LocationRef locationRef, String errorMsg) {
        this.locationRef = locationRef
        this.errorMsg = errorMsg
    }

    @Override
    String toString() {
        return locationRef.toString()+ " "+errorMsg
    }
}
