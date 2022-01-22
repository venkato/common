package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.treewalk.TreeWalk;

import java.util.logging.Logger;

@CompileStatic
class TreeWalkJrr extends TreeWalk{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int skipped=0;
    public int matched=0;
    public int parent=0;

    TreeWalkJrr(Repository repo) {
        super(repo)
    }

    TreeWalkJrr(Repository repo, ObjectReader or) {
        super(repo, or)
    }

    TreeWalkJrr(ObjectReader or) {
        super(or)
    }

    @Override
    int isPathMatch(byte[] p, int pLen) {
        int res=  super.isPathMatch(p, pLen)
        //log.info "${res}"
        if(res==0){
            matched++
        }
        if(res>0){
            skipped++
        }
        if(res<0){
            parent++
        }

        return res;
    }

    String reportStat(){
        return "m=${matched} s=${skipped} p=${parent}"
    }

}
