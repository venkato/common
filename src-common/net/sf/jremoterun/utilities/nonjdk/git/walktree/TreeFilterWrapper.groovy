package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.logging.Logger;

@CompileStatic
class TreeFilterWrapper extends TreeFilter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TreeFilter nested;

    TreeFilterWrapper(TreeFilter nested) {
        this.nested = nested
    }

    @Override
    TreeFilter negate() {
        return nested.negate()
    }

    @Override
    int matchFilter(TreeWalk walker) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        return nested.matchFilter(walker)
    }

    @Override
    String toString() {
        return nested.toString()
    }

    @Override
    boolean include(TreeWalk walker) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        return nested.include(walker)
    }

    @Override
    boolean shouldBeRecursive() {
        return nested.shouldBeRecursive()
    }

    @Override
    TreeFilter clone() {
        return nested.clone()
    }
}
