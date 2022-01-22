package net.sf.jremoterun.utilities.nonjdk.swing;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class HeadLessModeSetter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void setHeadlessMode(boolean value){
        JrrClassUtils.setFieldValue(GraphicsEnvironment,'headless',value)
        assert GraphicsEnvironment.isHeadless() == value
    }

}
