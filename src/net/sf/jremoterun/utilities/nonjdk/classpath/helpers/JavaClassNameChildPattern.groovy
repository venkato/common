package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes

import java.util.logging.Logger

@CompileStatic
class JavaClassNameChildPattern implements ChildChildPattern , Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<ClassNameSuffixes> suffixes = [ClassNameSuffixes.dotclass,ClassNameSuffixes.dotgroovy,ClassNameSuffixes.dotjava];

    public String childStartWith;
    public String package1;
    public String classNameLast;

    JavaClassNameChildPattern(Class childStartWith) {
        this(new ClRef(childStartWith))
    }

    JavaClassNameChildPattern(ClRef childStartWith1) {
        this.childStartWith = childStartWith1.getClassPath()
        List<String> tokenize1 = this.childStartWith.tokenize('/')
        if(tokenize1.size()<=1){
            package1 = ''
        }else{
            classNameLast=tokenize1.remove(tokenize1.size()-1)
            package1= tokenize1.join('/')
        }
    }

    @Override
    File resolveChild(File parent) {
        if(!parent.exists()){
            throw new FileNotFoundException("${parent} file not exist")
        }

        File pakc;
        if(package1==null||package1.length()==0){
            pakc = parent
        }else {
            pakc = new File(parent, package1)
            if(!pakc.exists()){
                throw new FileNotFoundException(pakc.getAbsolutePath())
            }
        }
        if(!pakc.isDirectory()){
            throw new FileNotFoundException('not dir : '+pakc.getAbsolutePath())
        }
        File f2
        suffixes.each {
            if(f2==null) {
                File f1 = new File(pakc, classNameLast + it.customName)
                if (f1.exists()) {
                    f2 = f1
                }
            }
        }

        if(f2!=null){
            return f2;
        }
        throw new FileNotFoundException("${pakc}/${classNameLast}.any ${suffixes}")
    }


    @Override
    String approximatedName() {
        return childStartWith + '.any'
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
