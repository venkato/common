package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class StartWithThenNumberChildPattern implements ChildPattern {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String childStartWith;

    StartWithThenNumberChildPattern(String childStartWith) {
        this.childStartWith = childStartWith
    }

    @Override
    File resolveChild(File parent) {
        if (!parent.exists()) {
            throw new FileNotFoundException("${parent} file not exist")
        }
        File[] files1 = parent.listFiles()
        if (files1.length == 0) {
            throw new FileNotFoundException("${parent} file has no childs")
        }
        List<File> list1 = files1.toList()
        List<File> foundChilds = list1.findAll { isMatched(it) }
        int size1 = foundChilds.size()
        if (size1 == 0) {
            throw new IllegalStateException("Child not found ${childStartWith} in ${parent}, all childs : ${list1.collect { it.getName() }}")
        }
        if (size1 > 1) {
            throw new IllegalStateException("found many ${size1} in ${parent} : ${foundChilds.collect { it.getName() }}")
        }
        return foundChilds[0]
    }

    boolean isMatched(File f) {
        String name1 = f.getName()
        if (name1.startsWith(childStartWith)) {
            if (name1.length() > childStartWith.length()) {
                return name1.charAt(childStartWith.length()).isDigit()
            }
        }
        return false
    }

    @Override
    String approximatedName() {
        return childStartWith + '_d_any'
    }

    @Override
    String toString() {
        return childStartWith
    }
}
