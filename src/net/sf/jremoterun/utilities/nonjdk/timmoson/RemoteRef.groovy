package net.sf.jremoterun.utilities.nonjdk.timmoson

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef;

import java.util.logging.Logger;

@CompileStatic
class RemoteRef implements Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = -6511634932885188423L;

    public ToFileRef2 baseRef;

    public  ChildPattern child;

    RemoteRef(FileChildLazyRef ref) {
        this.baseRef = ref.parentRef;
        this.child = ref.child
    }

    RemoteRef(ToFileRef2 baseRef, ChildPattern child) {
        this.baseRef = baseRef
        this.child = child
    }

    FileChildLazyRef createFullRef(){
        new FileChildLazyRef(baseRef,child)
    }

}
