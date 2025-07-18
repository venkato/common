package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.AnalizeCommon
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.LoaderStuff
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.ElementInfoGroovy
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.JavaSourceElemntInfo

import java.util.logging.Logger

@CompileStatic
class AnalizeJavaSourceFile3 extends AnalizeCommon<JavaSourceElemntInfo> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static String cnrName = "L${ClRef.name.replace('.', '/')};";


    AnalizeJavaSourceFile3(LoaderStuff loaderStuff) {
        this.loaderStuff = loaderStuff
    }

    List<JavaSourceElemntInfo> analizeDir2(File dir) {
        List<JavaSourceElemntInfo> res = []
        analizeDir(dir, res)
        return res
    }

    void analizeDir(File dir, List<JavaSourceElemntInfo> res) {
        assert dir.exists()
        assert dir.directory
        dir.listFiles().toList().each {
            File f = it;
            try {
                if (it.isDirectory()) {
                    analizeDir(f, res)
                } else {
                    assert f.file
                    if (f.name.endsWith(ClassNameSuffixes.dotjava.customName)) {
                        if (f.length() == 0) {
                            log.info "empty file ${f}"
                        } else {
                            res.addAll analizeFile(f)
                        }
                    }
                }
            } catch (Throwable e) {
                loaderStuff.onException(e, f)
            }
        }
    }

    @Override
    boolean analizeElement3(JavaSourceElemntInfo el) {
        if (el.isStatic()) {
            return true
        }
        TypeDeclaration orInt = el.clOrInt
        if (orInt instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) orInt;
            assert !classOrInterfaceDeclaration.isInterface()
            if(classOrInterfaceDeclaration.isAbstract()){
                return false
            }
            List<ConstructorDeclaration> constructors = classOrInterfaceDeclaration.constructors
            if (constructors.size() == 0) {
                return true
            }

            if (constructors.find { it.parameters.size() == 0 }) {
                return true
            }
        }
        return false
    }

    List<ElementInfoGroovy> analizeJar(File jarFile) {
        throw new UnsupportedOperationException()
    }


    List<JavaSourceElemntInfo> analizeFile(File f) {
        assert f.getName().endsWith(ClassNameSuffixes.dotjava.customName)
//        CompilationUnit cu = JavaParser.parse(f);
        JavaParser javaParser = new JavaParser()
        javaParser.getParserConfiguration()
        CompilationUnit cu = javaParser.parse(f).result.get();
        cu.getSymbolResolver()
//        Name package23 = cu.packageDeclaration.get().name
//        ClassOrInterfaceDeclaration typeDeclaration = cu.getPrimaryType().get() as ClassOrInterfaceDeclaration

        JavaFileAnalizerVisitor3 analizerVisitor = new JavaFileAnalizerVisitor3()
        analizerVisitor.cu = cu
        analizerVisitor.loaderStuff = loaderStuff
//        analizerVisitor.cuClassName = "${package23}.${typeDeclaration.name}"
        analizerVisitor.printablePath = f
        try {
            cu.accept(analizerVisitor, null)
        } catch (Exception e) {
            throw JrrUtils.getRootException(e)
        }
        analizedGroovyFiles.addAll(analizerVisitor.foundedClasses)

        return analizerVisitor.els
    }


}
