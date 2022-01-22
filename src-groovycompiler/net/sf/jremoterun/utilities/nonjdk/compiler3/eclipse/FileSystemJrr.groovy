package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.internal.compiler.batch.FileSystem
import org.eclipse.jdt.internal.compiler.env.IBinaryType
import org.eclipse.jdt.internal.compiler.env.IModule
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer

import java.util.logging.Logger

@CompileStatic
public class FileSystemJrr extends FileSystem{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HashSet<String> usedNames = new HashSet<>()

    public FileSystemJrr(String[] classpathNames, String[] initialFileNames, String encoding) {
        super(classpathNames, initialFileNames, encoding)
    }

    public  FileSystemJrr(String[] classpathNames, String[] initialFileNames, String encoding, Collection<String> limitModules) {
        super(classpathNames, initialFileNames, encoding, limitModules)
    }

    public  FileSystemJrr(FileSystem.Classpath[] paths, String[] initialFileNames, boolean annotationsFromClasspath, Set<String> limitedModules) {
        super(paths, initialFileNames, annotationsFromClasspath, limitedModules)
    }

    public  FileSystemJrr(FileSystem.Classpath[] paths, String[] initialFileNames, boolean annotationsFromClasspath) {
        super(paths, initialFileNames, annotationsFromClasspath)
    }


    void setModule1(IModule m){
        module = m
    }



    @Override
    NameEnvironmentAnswer findType(char[] typeName, char[][] packageName, char[] moduleName) {
        try {
            NameEnvironmentAnswer res = super.findType(typeName, packageName, moduleName)
            gotResolved12(res)
            return res;
        }catch(Exception e) {
            log.warning "failed resolve ${new String(typeName)}"
            throw e;
        }
    }

    @Override
    NameEnvironmentAnswer findType(char[][] compoundName, char[] moduleName) {
        NameEnvironmentAnswer res =  super.findType(compoundName, moduleName)
        gotResolved12(res)
        return res;
    }

    void gotResolved12(NameEnvironmentAnswer res){
        if(res!=null){
            gotResolved12NotNull(res)
        }
    }

    void gotResolved12NotNull(NameEnvironmentAnswer res){
        IBinaryType binaryType = res.getBinaryType()
        if(binaryType==null) {
        }else{
            char[] name1 = binaryType.getName()
            if(name1==null){

            }else {
                usedNames.add(new String(name1));
            }
        }
    }


}
