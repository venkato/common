package net.sf.jremoterun.utilities.nonjdk.javaparser

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import groovy.transform.CompileStatic
import javassist.ClassPool
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import org.eclipse.jdt.core.dom.ASTVisitor

import java.util.logging.Logger

@CompileStatic
class AddFilesToJavaParser extends AddFilesToClassLoaderGroovy {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public CombinedTypeSolver combinedTypeSolver;
    //ASTVisitor astVisitor =

    AddFilesToJavaParser() {
        combinedTypeSolver = new CombinedTypeSolver();
    }

    AddFilesToJavaParser(CombinedTypeSolver combinedTypeSolver) {
        this.combinedTypeSolver = combinedTypeSolver
    }

    @Override
    void addFileImpl(File file) throws Exception {
        JarTypeSolver jarTypeSolver = new JarTypeSolver(file)
        combinedTypeSolver.add(jarTypeSolver);
    }


}
