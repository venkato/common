package net.sf.jremoterun.utilities.nonjdk.idea.init;

import groovy.transform.CompileStatic;

@CompileStatic
public interface IdeaClassPathStoreI {
    void reset();

    void useCache(boolean b) throws Exception;
}
