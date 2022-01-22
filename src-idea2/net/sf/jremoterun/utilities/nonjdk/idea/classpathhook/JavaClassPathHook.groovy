package net.sf.jremoterun.utilities.nonjdk.idea.classpathhook

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.JavaCommandLineState
import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.runners.ExecutionEnvironment
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.IdeaJavaRunner2Settings
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.javaonly.InitInfo
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import org.apache.commons.io.FilenameUtils


import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class JavaClassPathHook extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static InitInfo initInfo = new InitInfo(JavaClassPathHook)

    static void installBothHooks() {
        if (initInfo.isInited()) {
            log.info "already inited"
        } else {
            initInfo.setInited()
            JavaClassPathHook.installHook()
            try {
                JavaClassPathHookV2.installHook()
            } catch (Exception e) {
                log.log(Level.SEVERE, "failed do hook", e)
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed do hook", e)
            }
            JavaStartConfigHook.installHook()
        }
    }

    @Override
    protected Object handleException(Object key, Throwable throwable) {
        super.handleException(key, throwable)
        net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Can't create java start hook", throwable)
        return null
    }

    static void installHook() {
        Class clazz = JavaCommandLineState
        CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz)
        CtMethod method = JrrJavassistUtils.findMethodByCount(clazz, ctClass, 'createCommandLine', 0)
        method.insertAfter """
            ${CodeInjector.createSharedObjectsHookVar2(clazz)}
            ${CodeInjector.myHookVar}.get(new Object[]{this,\$_});
        """
        JrrJavassistUtils.redefineClass(ctClass, clazz);
        CodeInjector.putInector2(clazz, new JavaClassPathHook())
    }

    @Override
    Object getImpl(Object key) {
        java.lang.Object[] obj = (Object[]) key;
        JavaCommandLineState commandLineState = obj[0] as JavaCommandLineState;
        GeneralCommandLine generalCommandLine = obj[1] as GeneralCommandLine

        ParametersList parametersList = generalCommandLine.getParametersList()
        log.info "parametersString : ${parametersList.getParametersString()}"
        List<String> list = parametersList.getList()
//        list
        IdeaJavaRunner2Settings.jvmOptions.reverse(false).each {
            parametersList.addAt(0, it)
        }

        ExecutionEnvironment environment = commandLineState.getEnvironment()
        String runnerName = environment.toString()
        File runnerFile = new File(IdeaJavaRunner2Settings.runners, runnerName)
        if (!runnerFile.exists()) {
            log.info "no runner file ${runnerFile}"
            return null
        }
        File libNameAsGroovy = new File(IdeaJavaRunner2Settings.libs, runnerFile.text + ClassNameSuffixes.dotgroovy.customName)
        if (!libNameAsGroovy.exists()) {
            log.info "file not exists : ${libNameAsGroovy}"
            return null
        }


        int index = list.indexOf('-classpath')
        if (index < 0) {
            List<File> all = list.findAll { it.startsWith('@') }.collect {
                it.substring(1) as File
            }.findAll { it.exists() && it.isFile() }.findAll { it.text.readLines()[0] == '-classpath' }
            if (all.size() == 1) {
                log.info "seems java9"
                addppendJava9(all.first(), libNameAsGroovy)
            } else {
                log.error("bad index : ${list}")
            }
        } else {
            String classPathJar = list.get(index + 1)
            File file = classPathJar as File
            assert file.exists()
            List<File> classpath = [];
            File myLibsPrefix = createZip7(libNameAsGroovy)
            classpath.add myLibsPrefix
            classpath.add file
            classpath.each { assert it.exists() }
            String newClassPath = classpath.collect { it.absolutePath }.join(';')
            parametersList.set(index + 1, newClassPath)
        }
        return null
    }

    void addppendJava9(File ideaClassPathFile, File libNameAsGroovy) {
        List<File> classpath = [];
        AddFilesToClassLoaderGroovy addCl = new AddFilesToClassLoaderGroovy() {
            @Override
            void addFileImpl(File file33) throws Exception {
                classpath.add(file33)
            }
        }
        addCl.addFromGroovyFile(libNameAsGroovy)

        List<String> lines = ideaClassPathFile.text.readLines()
        String s = classpath.join(';') + ';' + lines[1]
        lines[1] = s
        ideaClassPathFile.text = lines.join('\n')

    }

    public static File createZip7(File libNameAsGroovy) throws Exception {
        List<File> classpath = AddFileToClassloaderDummy.extractFilesFromGroovyFile(libNameAsGroovy)
        File suffix = createZip2(classpath, FilenameUtils.getBaseName(libNameAsGroovy.getName()))
        return suffix
    }

    public static File createZip2(List<File> files, String libName) throws Exception {
        assert IdeaJavaRunner2Settings.jars.exists()
        File libFile = new File(IdeaJavaRunner2Settings.jars, libName + '.jar')
        UrlCLassLoaderUtils2.createJarForLoadingClassesF(files, libFile)
        return libFile
    }


}
