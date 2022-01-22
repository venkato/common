package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.fileloayout.Java17Files
import org.apache.commons.lang3.SystemUtils
import org.fife.rsta.ac.java.JarManager
import org.fife.rsta.ac.java.buildpath.JarLibraryInfo
import org.fife.rsta.ac.java.buildpath.LibraryInfo
import org.fife.rsta.ac.java.buildpath.ZipSourceLocation
import org.fife.ui.autocomplete.Completion
import org.fife.ui.autocomplete.CompletionProvider

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class RstaLangSupportStatic {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static RstaLangSupportStatic langSupport = new RstaLangSupportStatic();

//    public

    public static File toolsJarFile;

    // no need cache JarManger as this field has it
    public AddFileSourceToRsta addFileSourceToRsta;

    public OsInegrationClientI osInegrationClient

    public static boolean addJfrIfExistS = true

    public ClassPathCalculatorWithAdder classPathCalculatorGroovy = new ClassPathCalculatorWithAdder();


    JarManager createJarManager() {
        return new LogImprovedJarManager()
    }

    public void init() throws Exception {
        if (addFileSourceToRsta != null) {
            return;
        }
        JarManager jarManager = createJarManager()
        try {
            addJavaLibraries(jarManager)
        } catch (IOException ioe) {
            log.log(Level.INFO, "Can't add jdk sources", ioe);
        }
        addFileSourceToRsta = new AddFileSourceToRsta(jarManager);
        defaultClassPathInit();
    }

    void addJava11Libraries(JarManager jarManager , File javaHome){
        File jmodDir = new File(javaHome, Java17Files.jmods.customName)
        if (jmodDir.exists()) {
            File sourceFile1 = findSourceFile(javaHome)
            List<File> jmodsFiles = jmodDir.listFiles().toList()
            assert jmodsFiles.size() > 0
            jmodsFiles.each {
                if (it.getName().endsWith('.jmod')) {
                    JModLibraryInfo jModLibraryInfo = new JModLibraryInfo(it);
                    if (sourceFile1 != null) {
                        jModLibraryInfo.setSourceLocation(new ZipSourceLocation(sourceFile1))
                    }
                    jarManager.addClassFileSource(jModLibraryInfo)
                }
            }
        }
    }

    void addJavaLibraries(JarManager jarManager ){
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File jmodDir = new File(javaHome, Java17Files.jmods.customName)
        if (jmodDir.exists()) {
            addJava11Libraries(jarManager,javaHome)
            log.info('jmods added')
        } else {
            File sourceFile1 = findSourceFile(javaHome)
            LibraryInfo rtJar = getJVMLibraryInfoImpl(javaHome)
            if(rtJar!=null) {
                if(sourceFile1!=null) {
                    rtJar.setSourceLocation(new ZipSourceLocation(sourceFile1))
                }
                jarManager.addClassFileSource(rtJar)
            }
        }
    }

    void addJfrJarIfExist() {
        File javaHome = SystemUtils.getJavaHome();
        assert javaHome.exists();
        File jfrJar = javaHome.child('lib/jfr.jar');
        if (jfrJar.exists()) {
            classPathCalculatorGroovy.filesAndMavenIds.add(jfrJar)
        }
    }

    void addEntryInFly(Object obj) {
        GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.add(obj)
        addFileSourceToRsta.add(obj)
    }

    public static boolean calcClassPath = true

    boolean isNeedAddSrc() {
        if (calcClassPath) {
            boolean shouldAddSrc = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver != null
            return shouldAddSrc
        }
        return false
    }


    void defaultClassPathInit() throws Exception {
        addClassesFromUrlClassLoader();
        boolean shouldAddSrc = isNeedAddSrc()
        if (shouldAddSrc) {
            classPathCalculatorGroovy.calcAndAddClassesToAdded(addFileSourceToRsta);
        } else {
            addFileSourceToRsta.addAll(classPathCalculatorGroovy.filesAndMavenIds.findAll { it != null })
        }
        log.info("jars added : " + addFileSourceToRsta.addedFiles2.size());
    }

    void addClassesFromUrlClassLoader() {
        if (GroovyMethodRunnerParams.gmrp != null) {
            AddFilesToUrlClassLoaderGroovy addFilesToClassLoaderFromGmrp = GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader
            if (addFilesToClassLoaderFromGmrp != null) {
                addFilesToClassLoaderFromGmrp.addedGroovyClassPathFiles.each {
                    classPathCalculatorGroovy.addFilesToClassLoaderGroovySave.addFromGroovyFile(it);
                }
                classPathCalculatorGroovy.filesAndMavenIds.addAll(addFilesToClassLoaderFromGmrp.addedFiles2)
            }
        }
        ClassLoader currentClassLoader = JrrClassUtils.getCurrentClassLoader();
        if (currentClassLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) currentClassLoader;
            classPathCalculatorGroovy.addFilesToClassLoaderGroovySave.addClassPathFromURLClassLoader(urlClassLoader);
        } else {
            log.info("non url classloader");
        }
        MavenCommonUtils mcu = new MavenCommonUtils()
        File javahome = System.getProperty('java.home') as File
        assert javahome.exists()
        classPathCalculatorGroovy.filesAndMavenIds = classPathCalculatorGroovy.filesAndMavenIds.collect {
            if (it instanceof File) {
                File f = (File) it;
                if (mcu.isParent(javahome, f)) {
                    return null
                }
            }
            return it;
        }
        if (toolsJarFile == null) {
            toolsJarFile = mcu.getToolsJarFile();
            if (toolsJarFile != null && toolsJarFile.exists()) {
                classPathCalculatorGroovy.filesAndMavenIds.add toolsJarFile
            } else {
                log.info "failed find tools.jar : ${toolsJarFile}"
            }
        } else {
            classPathCalculatorGroovy.filesAndMavenIds.add toolsJarFile
        }
        if (addJfrIfExistS) {
            addJfrJarIfExist()
        }

        if (isAddGroovyJar()) {
            classPathCalculatorGroovy.filesAndMavenIds.add DropshipClasspath.groovy
        }

    }

    boolean isAddGroovyJar() {
        return MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver != null
    }



    static List<String> srcFiles = ["lib/src.zip", "lib/src.jar", "src.zip", "../src.zip",
                                    "src.jar", "../src.jar"];

    static List<String> rtJar = ["lib/rt.jar", "../Classes/classes.jar",]


    LibraryInfo getJVMLibraryInfoImpl(File jreHome) {
        assert jreHome.exists()
        final File classesArchive = findExistingPath(jreHome, this.rtJar);
        if (classesArchive == null) {
            log.warn("Failed to load default JRE JAR info");
            return null;
        }

        final LibraryInfo        info = new JarLibraryInfo(classesArchive);

        final File sourcesArchive = findSourceFile(jreHome);
        if (sourcesArchive != null) {
            info.setSourceLocation(new ZipSourceLocation(sourcesArchive));
        }

        return info;
    }

    File findSourceFile(File jreHome) {
        final File sourcesArchive = findExistingPath(jreHome, srcFiles);
        return sourcesArchive;
    }


    static File findExistingPath(final File baseDir, List<String> paths) {
        List<File> findAll1 = paths.collect { new File(baseDir, it) }.findAll { it.exists() }
        if (findAll1.size() > 0) {
            return findAll1[0]
        }
        return null
    }

}
