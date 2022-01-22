package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class StartWithAndEndsExcludeChildPattern implements ChildChildPattern, Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String childStartWith;
    public String endWith;
    public String exclude;

    StartWithAndEndsExcludeChildPattern(String childStartWith, String endWith,String exclude) {
        this.childStartWith = childStartWith
        this.endWith = endWith
        this.exclude = exclude
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
            throw new IllegalStateException("Child not found ${childStartWith} endWith=${endWith} exclude=${exclude} in ${parent}, all childs ${list1.size()} : ${list1.collect {it.getName()}}")
        }
        if (size1 > 1) {
            throw new IllegalStateException("found many ${size1} in ${parent} : ${foundChilds.collect {it.getName()}}")
        }
        return foundChilds[0]
    }

    boolean isMatched(File f){
        String name1 = f.getName()
        if(childStartWith!=null){
            if(!name1.startsWith(childStartWith)){
                //log.info "cp1 ${name1}"
                return false
            }
        }
        if(endWith!=null){
            if(!name1.endsWith(endWith)){
            //    log.info "cp2 ${name1}"
                return false
            }
        }
        if(exclude!=null){
            if(name1.contains(exclude)){
              //  log.info "cp3 ${name1}"
                return false
            }
        }
        //log.info "cp4 ${name1}"
        return true
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
