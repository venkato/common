package net.sf.jremoterun.utilities.nonjdk.classpath.checkers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmConsoleDecompiler
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenIdAndRepoCustomSourceContains
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.ClassPathMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.AllMavenIdsRefs
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class JavaVersionInJar {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? extends MavenIdContains> ignoredMavenIds = [];

    static {
//        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.commonsIoBad
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.api_metadata.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.architecture_test.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.docs.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.runtime_api_info.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.smoke_test.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.Gradle5MavenIds.soak.m
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds.stdlib_common
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds.stdlib_js
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds.test_annotations_common
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds.test_common
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.KotlinMavenIds.test_js
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.NettyMavenIds.dev_tools
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.NettyMavenIds.testsuite_osgi
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.SquirrelSqlMavenIds.squirrelsql_launcher
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.TwelvemonkeysImageioMavenIds.pdf
        ignoredMavenIds.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.TwelvemonkeysImageioMavenIds.reference

        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.NettyMavenIds.all

        // java11:
        ignoredMavenIds.add net.sf.jremoterun.utilities.mdep.DropshipClasspath.xmlApis
        ignoredMavenIds.addAll  net.sf.jremoterun.utilities.nonjdk.classpath.refs.DerbyMavenIds.allDerbyLocale
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.cssParser

        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.videoPlayerForSwing
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.eclipseWorkbench
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.gnuGetOpt
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.swtWin
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.javaCompiler3
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.gumDiffLangAware
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.xmlApiId
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.pty4j
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.fifeRtext
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.windowsAuth
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.xmlApisExt
        ignoredMavenIds.add  net.sf.jremoterun.utilities.nonjdk.classpath.refs.NexusSearchMavenIds.xpp3XmlParser
        ignoredMavenIds.add  new MavenId('net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:9.20-2.00beta')
    }

    public List allMavenIds;

    void addClassPathMavenIds(ClassPathMavenIds classPathMavenIds){
        allMavenIds.addAll(classPathMavenIds.getMavenIdsCustom().collect {it.m})
    }

    JavaVersionInJar() {
        AllMavenIdsRefs allMavenIdsRefs = new AllMavenIdsRefs()
         allMavenIds= allMavenIdsRefs.buildEnumList()
    }

    List checkAllMavenIds(){
        List java11All = []
        List java8All = []
        List<MavenId> ignoredMave = JavaVersionInJar.ignoredMavenIds.collect { it.m }
        allMavenIds.each {
            if(it instanceof MavenIdContains) {
                checkingOne(it.m, java11All, java8All, ignoredMave)
            }else             if(it instanceof MavenIdAndRepoContains) {
                checkingOne(it.mavenIdAndRepo, java11All, java8All, ignoredMave)
            }else             if(it instanceof ToFileRef2) {
                checkingOne(it, java11All, java8All, ignoredMave)
            }else {
                throw new UnsupportedOperationException("${it}")
            }
        }
        return java11All;
    }

    void checkingOne(ToFileRef2 el1, List java11All ,List java8All ,   List<MavenId> ignoredMave){
        String fullName = "${el1.getClass()} .. ${el1}"
        MavenIdContains mm ;
        if (el1 instanceof MavenIdContains) {
            mm = (MavenIdContains) el1;
        }
        if (el1 instanceof MavenIdAndRepoContains) {
            MavenIdAndRepoContains mma = (MavenIdAndRepoContains) el1;
            mm= mma.getMavenIdAndRepo().m
        }
        if (el1 instanceof MavenIdAndRepoCustomSourceContains) {
            mm= el1.getMavenIdAndRepo().m
        }
        boolean  needIgnore = ignoredMave.contains(mm.m)
//            log.info "${el1} ${mm}  ${needIgnore}"
        if(el1.toString().startsWith('derbyLocale_')){
            needIgnore = true
        }
        if(needIgnore){
            log.fine "ignoring ${fullName}"
        }else {
            try {
                File file = el1.resolveToFile()
                boolean java8 = checkVersionIsJava8OfAnyClass(file)
                if (java8) {
                    java8All.add(el1)
                } else {
                    java11All.add(el1)
                }
            } catch (Throwable e) {
                log.info "failed on ${fullName}"

                throw e
            }
        }
    }

    boolean checkVersionIsJava8OfAnyClass(File jarFile){
        int versionOfAnyClass = readVersionOfAnyClass(jarFile)
        return versionOfAnyClass <= org.objectweb.asm.Opcodes.V1_8
    }

    int readVersionOfAnyClass(File jarFile){
        assert  jarFile.exists()
        HashSet<String> entries = new HashSet<>()
        BufferedInputStream stream1 = jarFile.newInputStream()
        ZipInputStream z = new ZipInputStream(stream1)
        while(true) {
            ZipEntry nextEntry = z.getNextEntry()
            if(nextEntry==null){
                throw new Exception("No classes found ${entries.size()} : ${entries.sort().join(', ')}")
            }
            String name1 = nextEntry.getName()
            entries.add name1
            boolean  okToCheck = name1.endsWith(ClassNameSuffixes.dotclass.customName)
            if(name1=='module-info.class'){
                okToCheck = false
            }
            if(name1.startsWith('META-INF/versions/')){
                okToCheck = false
            }

            if(okToCheck){
                byte[] array = IOUtils.toByteArray(z);
                int version = readVersion(array)
//                log.info "version of  ${name1} is ${version}"
                return version;
            }
        }
        throw new Exception("not reachable")
    }

    void readVersionOfAllClass(File jarFile){

        HashSet<String> entriesJava8 = new HashSet<>()
        HashSet<String> entriesJava11 = new HashSet<>()
        HashSet<String> entries = new HashSet<>()
        BufferedInputStream stream1 = jarFile.newInputStream()
        ZipInputStream z = new ZipInputStream(stream1)
        while(true) {
            ZipEntry nextEntry = z.getNextEntry()
            if(nextEntry==null){
                break
            }
            String name1 = nextEntry.getName()
            entries.add name1
            boolean  okToCheck = name1.endsWith(ClassNameSuffixes.dotclass.customName)
            if(name1=='module-info.class'){
                okToCheck = false
            }
            if(name1.startsWith('META-INF/versions/')){
                okToCheck = false
            }

            if(okToCheck){
                byte[] array = IOUtils.toByteArray(z);
                int version = readVersion(array)
//                log.info "version of  ${name1} is ${version}"
                if(version <= org.objectweb.asm.Opcodes.V1_8){
                    entriesJava8.add(name1)
                }else{
                    entriesJava11.add(name1)
                }
            }
        }
        log.info "java8 : ${entriesJava8.sort()}"
        log.info "java11 : ${entriesJava11.sort()}"
    }

    public int parseOptions =  ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES

    int readVersion(byte[] bytes){
        ObjectWrapper<Integer> version1 = new ObjectWrapper<>(null);
        ClassReader classReader = new ClassReader(bytes);
        ClassVisitor classVisitor =new ClassVisitor(AsmConsoleDecompiler.getMaxAsmM()) {
            @Override
            void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    version1.setObject(version)
            }

        }

        classReader.accept(classVisitor,parseOptions);
        assert version1.object !=null
        return version1.object
    }

}
