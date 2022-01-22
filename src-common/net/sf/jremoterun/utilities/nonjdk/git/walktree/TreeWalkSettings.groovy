package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.logging.Logger;

@CompileStatic
class TreeWalkSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public List<String> commitRawContains = [];
    public List<String> commitContains = [];
    public String diffAddOnlyContains;
    public String diffRemoveOnlyContains;
    public String diffTouchOnlyContains;
    public String commitAuthorContains;
    public boolean allNeedMatched = true;

    public boolean inspectOnly0Parent = true


    public Date startOlder;
    public Date stopOlder;
    public boolean parseAddRemove = true

    public int maxSizeInBytes = 10_000_000
    public boolean writeContextLine1 = false
    public int depthCountMax = 100
    public TreeFilter pathFilter;

    boolean isDoDiff() {
        if (diffAddOnlyContains != null) {
            return true
        }
        if (diffRemoveOnlyContains != null) {
            return true
        }
        if (diffTouchOnlyContains != null) {
            return true
        }
        return false
    }


     boolean isContains(String msg){
//         log.info "msg = ${msg}"
         String find1 = commitContains.find { msg.contains(it) }
         return find1!=null
    }

     boolean isRawContains(String msg){
         String find1 = commitRawContains.find { msg.contains(it) }
         return find1!=null
    }

}
