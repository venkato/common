package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class ChildFileCreatorToFileRef implements ToFileRef2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ChildFileCreator childFileCreator;

    ChildFileCreatorToFileRef(ChildFileCreator childFileCreator) {
        this.childFileCreator = childFileCreator
    }

    @Override
    File resolveToFile() {
        return childFileCreator.getDirCreate()
    }
}
