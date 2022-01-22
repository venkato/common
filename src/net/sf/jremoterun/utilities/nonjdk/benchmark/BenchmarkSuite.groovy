package net.sf.jremoterun.utilities.nonjdk.benchmark

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.JavaProcessRunner
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
enum BenchmarkSuite {


    CollectionsAdd,
    CollectionsIterate,
    CollectionsRemove,
    JDK8Streams,
    MapAdd,
    MapIterate,
    MapRemove,
    MessageDigests
    ;


    void setOutputFile2(JavaProcessRunner jpr, File baseDir) {
        assert baseDir.exists()
        String suffix = name() + '_' + new SimpleDateFormat('yyyy-MM-dd__HH-mm').format(new Date()) + '.csv'
        setOutputFile(jpr, baseDir.child(suffix))
    }

    void setOutputFile(JavaProcessRunner jpr, File outputFile) {
        jpr.javaMainArgs.addAll(['-o', outputFile.getAbsolutePathUnix()])
    }

    void setSuiteName(JavaProcessRunner jpr) {
        jpr.javaMainArgs.addAll(['-s', name()]);
    }

    JavaProcessRunner setAllWhatCan2(){
        return setAllWhatCan(GitSomeRefs.benchmarksuite.resolveToFile())
    }

    JavaProcessRunner setAllWhatCan( File base){
        JavaProcessRunner jpr =new JavaProcessRunner()
        jpr.runDir = new File(base,targetDir.child.toString())
        jpr.javaClasspath.add(new File(base,BenchmarkSuite.jarUber.child.toString()))
        jpr.mainClass = net.sf.jremoterun.utilities.nonjdk.benchmark.BenchmarkSuite.mainClass
        String datta =new SimpleDateFormat('yyyy-MM-dd__HH-mm').format(new Date())
        setSuiteName(jpr)
        setOutputFile2(jpr,jpr.runDir)
        jpr.process.consumeOutStreamSysout()
        jpr.process.addWriteOutToFile(new File(jpr.runDir,'log_'+this+'_'+datta+'.log'),10)
        return jpr
    }


    public static FileChildLazyRef targetDir = GitSomeRefs.benchmarksuite.childL('target/')
    public static FileChildLazyRef jarUber = GitSomeRefs.benchmarksuite.childL('target/benchmarksuite-1.0.1-SNAPSHOT.jar')
    public static FileChildLazyRef jarMini = GitSomeRefs.benchmarksuite.childL('target/original-benchmarksuite-1.0.1-SNAPSHOT.jar')
    public static FileChildLazyRef classesRef = GitSomeRefs.benchmarksuite.childL('target/classes/')
    public static ClRef mainClass = new ClRef('de.sfuhrm.benchmarksuite.Main')


}
