package net.sf.jremoterun.utilities.nonjdk.idea.init.ideainittracker;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.Serializable;
import java.util.logging.Logger;

@CompileStatic
public class IdeaLogItem implements Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //    public String location;
    public String msg;
    public Throwable exception;

    @Override
    public String toString() {
        if (exception == null) {
            return msg;
        }
        return "" + msg + " " + exception;
    }
}
