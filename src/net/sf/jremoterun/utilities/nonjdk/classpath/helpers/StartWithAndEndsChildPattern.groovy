package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class StartWithAndEndsChildPattern implements ChildChildPattern, Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String childStartWith;
    public String endWith;

    StartWithAndEndsChildPattern(String childStartWith,String endWith) {
        this.childStartWith = childStartWith
        this.endWith = endWith
    }

    @Override
    File resolveChild(File parent) {
        if(!parent.exists()){
            throw new FileNotFoundException("${parent} file not exist")
        }
        File[] files1 = parent.listFiles()
        if(files1.length == 0){
            throw new FileNotFoundException("${parent} file has no childs")
        }
        List<File> list1 = files1.toList()
        List<File> foundChilds = list1.findAll {isMatched(it) }
        int size1 = foundChilds.size()
        if (size1 == 0) {
            throw new IllegalStateException("Child not found ${childStartWith} endWith=${endWith} in ${parent}, all childs : ${list1.collect {it.getName()}}")
        }
        if (size1 > 1) {
            throw new IllegalStateException("found many ${size1} in ${parent} : ${foundChilds.collect {it.getName()}}")
        }
        return foundChilds[0]
    }

    boolean isMatched(File f){
        String name1 = f.getName()
        return name1.startsWith(childStartWith) && name1.endsWith(endWith)
    }

    @Override
    String approximatedName() {
        return childStartWith + '_any'+endWith
    }

    @Override
    String toString() {
        return childStartWith
    }

    @Override
    ChildChildPattern childL(String child1) {
        return new ExactChildPPattern(this,child1);
    }

    @Override
    ChildChildPattern childP(ChildPattern child1) {
        return new ExactChildPPattern(this,child1);
    }
}
