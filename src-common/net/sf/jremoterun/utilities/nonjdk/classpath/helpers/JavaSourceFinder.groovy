package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.expr.Name
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes;

import java.util.logging.Logger;

@CompileStatic
class JavaSourceFinder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    String getPackageForClass(File f) {
        assert f.getName().endsWith(ClassNameSuffixes.dotjava.customName)
//        CompilationUnit cu = JavaParser.parse(f);
        CompilationUnit cu = new JavaParser().parse(f).result.get();
        Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration()
        if (!packageDeclaration.isPresent()) {
            return null
        }
        PackageDeclaration packageDeclaration1 = packageDeclaration.get()
        Name name1 = packageDeclaration1.getName()
        return name1.asString().trim()
    }

    List<File> foundDirs = []

    List<File> findAllJavaSrcDirs(File f) {
        findAllJavaSrcDirsImpl(f)
        return foundDirs
    }

    void findAllJavaSrcDirsImpl(File f) {
        assert f.isDirectory()
        boolean isFOund = false
        File find4 = foundDirs.find { it.isChildFile(f) }
        if (find4 == null) {
            List<File> list1 = f.listFiles().toList()
            File find1 = list1.find { it.getName().endsWith(ClassNameSuffixes.dotjava.customName) && it.isFile() }
            if (find1 != null) {
                String clazz1 = getPackageForClass(find1)
                if (clazz1 != null) {
                    File root = foundJavaClass(find1, clazz1)
                    if (root != null) {
                        foundDirs.add(root)
                        isFOund = true
                    }

                }
            }
            if (!isFOund) {
                list1.findAll { it.isDirectory() }.each {
                    findAllJavaSrcDirsImpl(it)
                }
            }
        }
    }

    File foundJavaClass(File f, String packages1) {
        File p = f.getParentFile()
        List<String> tokenize1 = packages1.tokenize('.').reverse()
        boolean isGood = true
        tokenize1.each {
            if (isGood) {
                if (p.getName() == it) {
                    p = p.getParentFile()
                } else {
                    isGood = false
                    onPackageMismatch(f, p, packages1)
                }
            }
        }
        if (isGood) {
            return p
        }
        return null
    }

    void onPackageMismatch(File javaF, File mismatched, String fulllP) {
        throw new Exception("pakcge mismact for ${javaF} ${mismatched.getName()} on ${fulllP}")
    }


}
