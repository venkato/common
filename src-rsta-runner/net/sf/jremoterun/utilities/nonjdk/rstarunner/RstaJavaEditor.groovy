package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class RstaJavaEditor extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    View view

    RstaJavaEditor(File file) {
        super(file)
        view= new View(file.name,null,panel)
    }

    RstaJavaEditor(String name,String text2) {
        super(text2)
        view= new View(name,null,panel)
    }
}