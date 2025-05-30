package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.io.FileVisitResult
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.BinaryWithSourceI
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.AndroidArchive
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2

import java.util.logging.Logger

@CompileStatic
abstract class AddFileWithSources extends AddFilesToClassLoaderGroovy implements AddFilesWithSourcesI {
    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    MavenCommonUtils mavenCommonUtilsForSources = new MavenCommonUtils()
    List<File> addedSourceFiles = []

    static String eclipseSourceFileSuffix = ".source_"

//    volatile FileTransformer fileTransformer = new FileTransformer();

    boolean downloadSources = false;

    List<String> srcDirs = ['src', 'java', 'main']

    List<String> ignoredDirPrefix = ["resource", "classes", "build", "target"]

    abstract void addLibraryWithSource(File binary, List<File> source) throws Exception;

    abstract void addSourceFImpl(File source) throws Exception;

    abstract void addSourceS(String source) throws Exception;

    AddFileWithSources() {
        mavenCommonUtilsForSources.fileType = MavenFileType2.source.fileSuffix
    }

    @Override
    void addBinaryWithSource(BinaryWithSourceI fileWithSource) throws Exception {
        List<File> sources
        if (fileWithSource instanceof CustomSourceRefRedirect) {
            CustomSourceRefRedirect red = fileWithSource
            sources=[red.getSourceRedirect().resolveToFile()]
        }else{
            sources = fileWithSource.resolveSource()
        }
        addLibraryWithSourceCount(fileWithSource.resolveToFile(), sources);
    }

    void addLibraryWithSourceCount(File binary, List<File> source) throws Exception {
        net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(binary)
        addLibraryWithSource(binary, source);
        getAddedFiles2().add(binary);
        if (source != null) {
            source.each {
                net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(it)

            }
            addedSourceFiles.addAll(source);
        }
    }

    @Override
    void addSourceF(File source) throws Exception {
        net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(source)
        addSourceFImpl(source)
        if(addedSourceFiles.contains(source)){
            if(isLogFileAlreadyAdded){
                log.info "source file already added ${source}"
            }
        }else {
            addedSourceFiles.add(source)
        }
    }

    @Override
    void addFileImpl(File file) throws Exception {
        if (file.isFile()) {
            addFileSourceHelper(file, null)
        } else {
            addFolderAndTryFindSourceDir(file)
        }

    }


    void addSourceM(MavenIdAndRepoContains mavenId) {
        File source = addSourceMNoExceptionOnMissing(mavenId.getMavenIdAndRepo())

        if (source == null) {
            onException new FileNotFoundException("Failed find source for ${mavenId}");
        }else {
            addSourceF(source)
        }
    }

    @Override
    void addSourceM(MavenId mavenId) {
        File source = addSourceMNoExceptionOnMissing(mavenId)

        if (source == null) {
            onException new FileNotFoundException("Failed find source for ${mavenId}");
        }else {
            addSourceF(source)
        }
    }

    // why it is needed
    void addFileSourceHelper(File binary, File source) {
        if (binary != null) {
            net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(binary)
        }
        if (source != null) {
            net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(source)
        }
        String absolutePath = null;
        if (binary != null) {
            absolutePath = binary.absoluteFile.canonicalPath.replace('\\', '/')
        }
//        if (fileTransformer.acceptFile(binary, source, absolutePath)) {
        if (binary == null) {
            addSourceF(source)
        } else {
            List source3 = []
            if(source!=null){
                source3.add(source)
            }
            addLibraryWithSource(binary, (List)source3);
            getAddedFiles2().add(binary);
            if(source!=null) {
                addedSourceFiles.add(source)
            }
        }
//        }
    }

    static Collection calcEclipseClassPath(File eclipsePluginDir) throws Exception {
        net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(eclipsePluginDir)
        List<File> listFiles = eclipsePluginDir.listFiles().toList();
        Map<String, File> fileMap = new HashMap();
        for (File file : listFiles) {
            String name = file.getName();
            if (file.isFile() && name.contains(eclipseSourceFileSuffix)) {
                fileMap.put(name.replace(eclipseSourceFileSuffix, '_'), file);
            }
        }
        listFiles = listFiles.findAll { it.isFile() }.findAll { !it.getName().contains(".source_") }.findAll {
            it.name.endsWith('.jar')
        };
        return listFiles.collect {
            String name = it.getName();
            File source = fileMap.get(it.name);
            if (source == null) {
                return it;
            } else {
                BinaryWithSource binaryWithSource = new BinaryWithSource(it, source)
                return binaryWithSource
            }
            return null;
        }.findAll { it != null }
    }


    @Override
    File addSourceMNoExceptionOnMissing(MavenId mavenId){
        File source = mavenCommonUtilsForSources.findMavenOrGradle(mavenId)
        if (source == null) {
            source = onMissingMavenSource(mavenId)
        }
        return source
    }

    File addSourceMNoExceptionOnMissing(MavenIdAndRepo mavenId){
        File source = mavenCommonUtilsForSources.findMavenOrGradle(mavenId.m)
        if (source == null) {
            source = onMissingMavenSource(mavenId)
        }
        return source
    }


    @Override
    void addM(MavenId artifact) throws IOException {
        File file = resolveMavenId(artifact);
        if (file == null) {
            onException new FileNotFoundException(artifact.toString());
        }else {
            File source = addSourceMNoExceptionOnMissing(artifact);
            if (source == null) {
                log.info("no source for maven : " + artifact);
            }
            addFileSourceHelper(file, source);
        }
    }

    @Override
    File onMissingMavenId(MavenId artifact) {
        return super.onMissingMavenId(artifact)
    }

    @Override
    void addM(MavenIdContains artifact) throws IOException {
        addM2(artifact)
    }

    @Override
    void addM(MavenIdAndRepoContains artifact) throws IOException {
        addM2 (artifact)
    }

    void addM2(MavenIdAndRepoContains artifact) throws IOException {
        File file = resolveMavenId(artifact.getMavenIdAndRepo());
        if (file == null) {
            onException new FileNotFoundException(artifact.toString());
        }else {
            File source
            if (artifact instanceof CustomSourceRefRedirect) {
                CustomSourceRefRedirect redirect = (CustomSourceRefRedirect) artifact;
                source = redirect.getSourceRedirect().resolveToFile()
            }else {
                source = addSourceMNoExceptionOnMissing(artifact.getMavenIdAndRepo());
            }
            if (source == null) {
                log.info("no source for maven : " + artifact);
            }
            addFileSourceHelper(file, source);
        }
    }


    void addM2(MavenIdContains artifact){
        File file = resolveMavenId(artifact.m);
        if (file == null) {
            onException new FileNotFoundException(artifact.toString());
        }else {
            File source
            if (artifact instanceof CustomSourceRefRedirect) {
                CustomSourceRefRedirect redirect = (CustomSourceRefRedirect) artifact;
                source = redirect.getSourceRedirect().resolveToFile()
            }else {
                source = addSourceMNoExceptionOnMissing(artifact.m);
            }
            if (source == null) {
                log.info("no source for maven : " + artifact);
            }
            addFileSourceHelper(file, source);
        }
    }

    File onMissingMavenSource(MavenIdAndRepo mavenId) {
        if (!downloadSources) {
            log.info("no source for ${mavenId}")
            return null
        }
        try {
            MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver.downloadSource(mavenId.m,mavenId.repo)
            File sourceFile = mavenCommonUtilsForSources.findMavenOrGradle(mavenId.m)
//            log.info "source for ${mavenId} found ${sourceFile != null}"
            return sourceFile
        } catch (Throwable e) {
            log.info "failed donwload source for ${mavenId} ${e}"
            return null
        }
    }

    File onMissingMavenSource(MavenId mavenId) {
        if (!downloadSources) {
            log.info("no source for ${mavenId}")
            return null
        }
        try {
            MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver.downloadSource(mavenId)
            File sourceFile = mavenCommonUtilsForSources.findMavenOrGradle(mavenId)
//            log.info "source for ${mavenId} found ${sourceFile != null}"
            return sourceFile
        } catch (Throwable e) {
            log.info "failed donwload source for ${mavenId} ${e}"
            return null
        }

    }

    void addFolderAndTryFindSourceDir(File file) {
        File findSourceDirForBinaryCLass = findSourceDirForBinaryClass(file);
        if (file.equals(findSourceDirForBinaryCLass)) {
            findSourceDirForBinaryCLass = null;
        }
        if (findSourceDirForBinaryCLass == null) {
            log.info("can't find sources for " + file.getAbsolutePath());
            addFileSourceHelper(file, null);
        } else {
            log.info("src folder " + findSourceDirForBinaryCLass.getAbsolutePath() + " for " + file);
            addFileSourceHelper(file, findSourceDirForBinaryCLass);
        }
    }

    File findSourceDirForBinaryClass(File file) {
        if (file.isFile()) {
            return null
        }
        File[] listFiles = file.listFiles();
        if (listFiles.length == 0) {
            return null;
        }
        File sampleClassFile2 = findSampleFile(file);
        if (sampleClassFile2 == null) {
            log.info "sample class file not found in ${file}"
            return null
        }
        String sampleClassFile = mavenCommonUtils.getPathToParent(file, sampleClassFile2.parentFile)
//        sampleClassFile = sampleClassFile.substring(0, sampleClassFile.length() - '.class'.length() + 1)
        log.fine "sample file : ${sampleClassFile}"
        File result = findSourceFolderUp(file.parentFile, 2, sampleClassFile)
        if (file == result) {
            log.info "source equals dest ${file}"
            return null;
        }
        return result
    }

    File findSampleFile(File binaryDir) {
        File foundFile;
        binaryDir.traverse {
            if (it.name.endsWith(ClassNameSuffixes.dotclass.customName) && !it.name.contains('$')) {
                foundFile = it
                return FileVisitResult.TERMINATE;
            }
        };
        return foundFile;
    }

    File findSourceFolderUp(File dir, int upDepth, String classFile) {
        if (dir == null) {
            log.fine "dir is null"
            return null
        }
        log.fine "checking ${dir}"
        List<File> listFiles2 = dir.listFiles().toList().findAll {
            it.isDirectory() && srcDirs.contains(it.name)
        };
        for (File file2 : listFiles2) {
            File fondSourceFolder = findSourceFolderDown(file2, 2, classFile);
            if (fondSourceFolder != null) {
                return fondSourceFolder;
            }
        }
        log.fine "upDepth : ${upDepth}"
        if (upDepth <= 0) {

            return null
        }
        //upDepth--;
        return findSourceFolderUp(dir.parentFile, upDepth - 1, classFile)
    }


    File findSourceFolderDown(File dir, int remainDepth, String sampleSubFolder) {
        log.fine "checking ${dir}"
        if (checkIfDirOk(dir, sampleSubFolder)) {
            return dir;
        }
        if (remainDepth <= 0) {
            return null
        }
        List<File> listFiles2 = dir.listFiles().toList().findAll { it.isDirectory() };
        for (File file2 : listFiles2) {
            if (ignoredDirPrefix.find { file2.name.startsWith(it) } != null) {

            } else {

                File fileee = findSourceFolderDown(file2, remainDepth - 1, sampleSubFolder);
                if (fileee != null) {
                    return fileee;
                }
            }
        }
        return null;

    }

    boolean checkIfDirOk(File file, String child) {
        File f = new File(file, child)
        if (f.exists() && f.directory) {
            File[] listFiles = f.listFiles();
            for (File file2 : listFiles) {
                if (file2.getName().endsWith(".class")) {
                    log.fine "ignore : found class file : ${file2}"
                    return false;
                }
            }
            return true
        }
        return false
    }


    @Override
    void addSourceGenericAll(List objects) {
        if (objects.size() == 0) {
            onException new IllegalArgumentException("collection is empty")
        }else {
            int count3 =-1
            objects.each {
                count3++
                try {
                    addSourceGeneric(it)
                } catch (Throwable e) {
                    logAdder.info "failed add ${count3} ${it} ${e}"
                    onException e
                }
            }
        }
    }

    void addAar(AndroidArchive androidArchive){
        addSourceM(androidArchive.m);
    }

    @Override
    void addSourceGeneric(Object object) {
        // no : MavenPath, URL
        //
        try{
        switch (object) {
            case { object == null }:
                onException new NullPointerException("object is null")
                break
            case { object instanceof Collection }:
                onException new IllegalArgumentException("Collection : ${object}")
                break
            case { object instanceof AndroidArchive }:
                addAar(object as AndroidArchive)
                break
            case { object instanceof MavenId }:
                MavenId m = (MavenId) object;
                addSourceM(m)
                break;
            case { object instanceof MavenIdAndRepoContains }:
                MavenIdAndRepoContains mavenId1 = (MavenIdAndRepoContains) object;
                addM2 (mavenId1);
                break;
            case { object instanceof File }:
                File f = (File) object;
                addSourceF(f)
                break;
            case { object instanceof URL }:
                URL file = object as URL
                addUrl(file)
                break;
            case { object instanceof String }:
                String s = (String) object;
                addSourceS(s)
                break;
            case { object instanceof BinaryWithSourceI }:
                BinaryWithSourceI file = object as BinaryWithSourceI
                List<File> sources = file.resolveSource();
                if(sources==null){
                    onException new NullPointerException("No source for ${file}")
                }else {
                    if (sources.size()) {
                        onException new NullPointerException("Source size is null for ${file}")
                    }else {
                        sources.each {
                            addSourceF(it)
                        }
                    }
                }
                break;
            case { object instanceof MavenIdContains }:
                MavenIdContains mavenId3 = object as MavenIdContains
                addM2 ( mavenId3)
                break;
            case { object instanceof ToFileRefSelf }:
                ToFileRefSelf toFileRefSelf = object as ToFileRefSelf
                addSourceF toFileRefSelf.resolveToFile()
                break;
            case { object instanceof CustomSourceRefRedirect }:
                addSourceGeneric((object as CustomSourceRefRedirect).getSourceRedirect())
                break
            case { object instanceof ToFileRefRedirect2 }:
                addSourceGeneric((object as ToFileRefRedirect2).getRedirect())
                break
            default:
                CustomObjectHandler customObjectHandler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                if (customObjectHandler == null) {
                    onException new IllegalArgumentException("${object}");
                } else {
                    addSourceF customObjectHandler.resolveToFile(object)
                }

        // throw new UnsupportedOperationException("Not supported : ${object}")
        }
        }catch(Throwable e){
            onException(e);
        }
    }



}
