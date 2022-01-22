package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.AnalizeCommon
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.LoaderStuff
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.ElementInfoGroovy
import net.sf.jremoterun.utilities.nonjdk.str2obj.StringToEnumConverterEnhanced
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.control.CompilePhase

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
class AnalizeGroovyFields3 extends AnalizeCommon<FieldFoundedEls> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    AnalizeGroovyFields3(LoaderStuff loaderStuff) {
        this.loaderStuff = loaderStuff
    }

    List<FieldFoundedEls> analizeDir2(File dir) {
        List<FieldFoundedEls> res = []
        analizeDir(dir, res)
        return res
    }

    List<String> blackListedFiles = [StringToEnumConverterEnhanced.getSimpleName()+'.groovy']

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
                    if (f.name.endsWith('.groovy')) {
                        if (f.length() == 0) {
                            log.info "empty file ${f}"
                        } else {
                            if(!blackListedFiles.contains(f.getName())) {
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
                    if (zipEntry.name.endsWith('.groovy')) {
                        InputStream ins = zipFile.getInputStream(zipEntry);
                        String text = ins.text
                        List<FieldFoundedEls> foundedEls = analizeClass(text, zipEntry.name)
                        foundedEls.each {
                            it.locationRef = new LocationRef(new ClRef(zipEntry.name.replace('.groovy','').replace('/','.')),it.expression.lineNumber)
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
        assert f.name.endsWith('.groovy')
        List<FieldFoundedEls> foundedEls = analizeClass(f.text, f.absolutePath)
        foundedEls.each {
            it.locationRef = new LocationRef(f,it.expression.lineNumber)
        }
        //interested.addAll(foundedEls)
        return foundedEls
    }

    //public List<FieldFoundedEls> interested=[]

    List<FieldFoundedEls> analizeClass(String groovyText, String fileName) {
        //List<FieldFoundedEls> result = []
        AstBuilder astBuilder = new AstBuilder();
        List<ASTNode> astNodes23 = astBuilder.buildFromString(CompilePhase.INSTRUCTION_SELECTION, true, groovyText);
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
        ExpressionFinderFields3 expressionFinder = new ExpressionFinderFields3();
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

                cn.visitContents(expressionFinder)
            } else {
                expressionFinder.elName = fileName
                expressionFinder.printablePath = fileName
//                log.info "${it.class.name}"
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
