package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.compiler3.ClassNodeResolverJrr
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.AnalizeCommon
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.LoaderStuff
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldTypeCollection
import net.sf.jremoterun.utilities.nonjdk.str2obj.StringToEnumConverterEnhanced
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.builder.AstStringCompiler
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.ClassNodeResolver
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration

import java.security.AccessController
import java.security.PrivilegedAction
import java.util.logging.Logger
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class AnalizeGroovyFields3 extends AnalizeCommon<FieldFoundedEls> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public boolean checkNOFIELDCHECK = true
    //public ClassNodeResolverAnalazerJrr classNodeResolverAnalazerJrr
    public ClassNodeResolver classNodeResolver2 = new ClassNodeResolver()

    AnalizeGroovyFields3(LoaderStuff loaderStuff) {
        this.loaderStuff = loaderStuff
        //classNodeResolverAnalazerJrr=new ClassNodeResolverAnalazerJrr(loaderStuff.allClasspathAnalysis)
    }

    List<FieldFoundedEls> analizeDir2(File dir) {
        List<FieldFoundedEls> res = []
        analizeDir(dir, res)
        return res
    }

    public List<String> blackListedFiles = [StringToEnumConverterEnhanced.getSimpleName()+ ClassNameSuffixes.dotgroovy.customName]
    public List<File> blackListedFiles2 = []

    void analizeDir(File dir, List<FieldFoundedEls> res) {
        assert dir.exists()
        assert dir.directory
        dir.listFiles().toList().each {
            File f = it;
            try {
                if (it.isDirectory()) {
                    analizeDir(f, res)
                } else {
                    assert f.file
                    if (f.name.endsWith(ClassNameSuffixes.dotgroovy.customName)) {
                        if (f.length() == 0) {
                            log.info "empty file ${f}"
                        } else {
                            boolean anaaa = true
                            if(blackListedFiles.contains(f.getName())) {
                               anaaa = false
                            }
                            if(blackListedFiles2.contains(f)) {
                               anaaa = false
                            }
                            if(anaaa){
                                res.addAll analizeFile(f)
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                loaderStuff.onException(e, f)
            }
        }
    }

    @Override
    boolean analizeElement3(FieldFoundedEls el) {
        return true
    }

    List<FieldFoundedEls> analizeJar(File jarFile) {
        List<FieldFoundedEls> res = []
        assert jarFile.exists()
        assert jarFile.file
        ZipFile zipFile = new ZipFile(jarFile);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                try {
                    if (zipEntry.name.endsWith(ClassNameSuffixes.dotgroovy.customName)) {
                        InputStream ins = zipFile.getInputStream(zipEntry);
                        String text = ins.text
                        List<FieldFoundedEls> foundedEls = analizeClass(text, zipEntry.name)
                        foundedEls.each {
                            it.locationRef = new LocationRef(new ClRef(zipEntry.name.replace(ClassNameSuffixes.dotgroovy.customName,'').replace('/','.')),it.expression.lineNumber)
                        }
                        res.addAll(foundedEls)
                    }
                } catch (Throwable e) {
                    loaderStuff.onException(e, zipEntry)
                }
            }
        } finally {
            IOUtils.closeQuietly(zipFile)
        }
        return res;
    }


    @Override
    List<FieldFoundedEls> analizeFile(File f) {
        assert f.name.endsWith(ClassNameSuffixes.dotgroovy.customName)
        List<FieldFoundedEls> foundedEls = analizeClass(f.text, f.absolutePath)
        foundedEls.each {
            it.locationRef = new LocationRef(f,it.expression.lineNumber)
        }
        //interested.addAll(foundedEls)
        return foundedEls
    }

    //public List<FieldFoundedEls> interested=[]
    //public List<Class> classesConatains = [JrrClassUtils, JrrJavassistUtils, MethodRef, FieldRef, ConstructorRef,] as List<Class>;

    public int processedFiles=0
    public int ignoredFiles=0

    List<FieldFoundedEls> analizeClass(String groovyText, String fileName) {
        boolean handle = false
        if(net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldTypeCollection.isContainsName(groovyText)){
            handle=true
        }
//        if(groovyText.contains(JrrClassUtils.getName())){
//          handle = true
//        }
//        if(groovyText.contains(ClassType.JrrJavassistUtils.name())){
//          handle = true
//        }
//        if(groovyText.contains(MethodRef.getName())){
//          handle = true
//        }
//        if(groovyText.contains(FieldRef.getName())){
//          handle = true
//        }
//        if(groovyText.contains(ConstructorRef.getName())){
//          handle = true
//        }
//        if(groovyText.contains(ClRef.getSimpleName())){
//          handle = true
//        }
        if(!handle){
            ignoredFiles++
            return []
        }
        processedFiles++
        return analizeClassImpl(groovyText,fileName)
    }

//    public List<ASTNode> compile(String script, CompilePhase compilePhase, boolean statementsOnly) {
//        final String scriptClassName = ;
//        GroovyCodeSource codeSource = new GroovyCodeSource(script, scriptClassName + ".groovy", "/groovy/script");
//        CompilationUnit cu = new CompilationUnit(CompilerConfiguration.DEFAULT, codeSource.getCodeSource(),
//                AccessController.doPrivileged((PrivilegedAction<GroovyClassLoader>) GroovyClassLoader::new));
//        cu.addSource(codeSource.getName(), script);
//        cu.compile(compilePhase.getPhaseNumber());
//
//        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
//        List<ASTNode> result = cu.getAST().getModules().stream().reduce(new LinkedList<>(), (acc, node) -> {
//            BlockStatement statementBlock = node.getStatementBlock();
//            if (null != statementBlock) {
//                acc.add(statementBlock);
//            }
//            acc.addAll(
//                    node.getClasses().stream()
//                            .filter(c -> !(statementsOnly && scriptClassName.equals(c.getName())))
//                            .collect(Collectors.toList())
//            );
//
//            return acc;
//        }, (o1, o2) -> o1);
//
//        return result;
//    }

    List<FieldFoundedEls> analizeClassImpl(String groovyText, String fileName) {

        //List<FieldFoundedEls> result = []
        //AstBuilder astBuilder = new AstBuilder();

        List<ASTNode> astNodes23 = new AstStringCompilerJrr().compile( groovyText, CompilePhase.INSTRUCTION_SELECTION, true,classNodeResolver2)
//        List<ASTNode> astNodes23 = new AstStringCompilerJrr().compile( groovyText, CompilePhase.INSTRUCTION_SELECTION, true,classNodeResolverAnalazerJrr)
//        List<ASTNode> astNodes23 = astBuilder.buildFromString(CompilePhase.INSTRUCTION_SELECTION, true, groovyText);  
//        List<ASTNode> astNodes23 = astBuilder.buildFromString(CompilePhase.INSTRUCTION_SELECTION, true, groovyText);
        List<ASTNode> astNodes = astNodes23.findAll { it instanceof ClassNode }
        int nodeSize = astNodes.size();
        List<Integer> founedLine = []
        if (nodeSize == 0) {
        } else {
            ClassNode first = astNodes.first() as ClassNode;
            analizedGroovyFiles.add(first.getName())
        }

        // ==== 1 begin ===
        ExpressionFinderFields3 expressionFinder = new ExpressionFinderFields3(groovyText.readLines());
        expressionFinder.checkNOFIELDCHECK = checkNOFIELDCHECK
        expressionFinder.loaderStuff = loaderStuff
        expressionFinder.ignoreLines = founedLine.toSet()
        ClassNode classNode1
        astNodes23.each {
            if (it instanceof ClassNode) {
                ClassNode cn = (ClassNode) it;
                classNode1 =cn
                expressionFinder.classNode = cn
                expressionFinder.elName = cn.name
                File f = fileName as File
                if (f.file) {
                    expressionFinder.printablePath = fileName
                } else {
                    expressionFinder.printablePath = cn.name
                }
                // ==== 1 end ===
//                log.info "visit1 "
                cn.visitContents(expressionFinder)
            } else {
                expressionFinder.elName = fileName
                expressionFinder.printablePath = fileName
//                log.info "${it.class.name}"
//                log.info "visit2 "
                it.visit(expressionFinder)
            }
        }
        expressionFinder.interested.each {
            it.classNode = classNode1
        }
        return  expressionFinder.interested
//        return result;

    }


//    String extractFromInitializer2(Expression expression) {
//        if (expression instanceof ConstantExpression) {
//            ConstantExpression ce = (ConstantExpression) expression;
//            return ce.text
//        }
//        if (expression instanceof CastExpression) {
//            CastExpression ce = (CastExpression) expression;
//            return extractFromInitializer2(ce.expression);
//        }
//        if (expression instanceof ConstructorCallExpression) {
//            ConstructorCallExpression ce = (ConstructorCallExpression) expression;
//            Expression arguments = ce.arguments;
//            if (arguments == null) {
//                return null
//            }
//            if (arguments instanceof ArgumentListExpression) {
//                ArgumentListExpression al = (ArgumentListExpression) arguments;
//                List<Expression> expressions = al.expressions
//                if (expressions == null || expressions.size() != 1) {
//                    return null
//                }
//                return extractFromInitializer2(expressions.first())
//            }
//            return null
//        }
//        return null
//    }


    /**
     * Return false - means stop analisys. <br/>
     * Return true - means continue analisys
     *
     */
    boolean analize3(FieldFoundedEls el) {
        return true
    }


}
