package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.logging.Logger;

@CompileStatic
class CommitIdCanonicalTreeParser extends CanonicalTreeParser{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public String name;
    public RevCommit id ;

    CommitIdCanonicalTreeParser(String name, RevCommit id) {
//        this.name = name
        this.id = id
    }
}
