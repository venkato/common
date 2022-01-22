package net.sf.jremoterun.utilities.nonjdk.javaparser.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.javaparser.OnNewCompilationUnit
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.FileASTRequestor
import org.eclipse.jdt.core.dom.IBinding

import java.util.logging.Logger;

@CompileStatic
class EclipseJavaParser {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ASTParser parser = ASTParser.newParser(AST.JLS_Latest);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
    // In order to parse 1.5 code, some compiler options need to be set to 1.5
    public Map optionsEclipseC = JavaCore.getOptions();
    public IProgressMonitor progressMonitor;
    public OnNewCompilationUnit astVisitor
    public AddFileToClassloaderDummy classpath = new AddFileToClassloaderDummy()

    EclipseJavaParser(OnNewCompilationUnit astVisitor) {
        this.astVisitor = astVisitor
    }

    public FileASTRequestor fileASTRequestor = new FileASTRequestor() {
        @Override
        public void acceptAST(String sourceFilePath, CompilationUnit ast) {
            onNewCompilationUnit(sourceFilePath,ast)
            super.acceptAST(sourceFilePath, ast);
        }

        @Override
        public void acceptBinding(String bindingKey, IBinding binding) {
            super.acceptBinding(bindingKey, binding);
        }
    };

    void onNewCompilationUnit(String sourceFilePath, CompilationUnit ast){
        astVisitor.onNewCompilationUnit(sourceFilePath as File,ast);
    }

    AbstractTypeDeclaration getTypeFromCompilationUnit(CompilationUnit ast){
        List types = ast.types()
        if(types==null){
            return null
        }
        if(types.size()==0){
            return null
        }
        Object type1 = types[0]
        if (type1 instanceof AbstractTypeDeclaration) {
            AbstractTypeDeclaration tc  = (AbstractTypeDeclaration) type1;
            return tc

        }
        return null
    }

    void prepare() {
        setClassPathImpl(classpath.addedFiles2)
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setCompilerOptions(optionsEclipseC);
        parser.setResolveBindings(true);
        parser.setIgnoreMethodBodies(false);
    }


    void setClassPathImpl(Collection<File> files){
        String[] array1 = files.collect {it.getAbsolutePath()}.toArray(new String[0])
        parser.setEnvironment(array1, array1, null, false);
    }

    void parse(Collection<File> files){
        assert files.size()>0
        String[] bindingKeys = [];
        String[] ff = files.collect {it.getAbsolutePath()}.toArray(new String[0]);
        parser.createASTs(ff, null, bindingKeys, fileASTRequestor, progressMonitor);
    }





    /**
     * @see org.eclipse.jdt.core.JavaCore#VERSION_1_8
     */
    void setJavaVersion(String cv) {
        JavaCore.setComplianceOptions(cv, optionsEclipseC);
        JavaCore.setComplianceOptions(cv, optionsEclipseC);
    }
}
