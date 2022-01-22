package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.JavaVersionChecker
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddToAdderSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.BinaryWithSource3
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenIdAndRepoCustomSourceContains
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ToFileRefIfDownload
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipExactlyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternative
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternativeRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.reftype.ToFileRefUnsupported
import net.sf.jremoterun.utilities.nonjdk.compiler3.CopyFilesTmp
import net.sf.jremoterun.utilities.nonjdk.downloadutils.IffUnzipUtils
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3

import net.sf.jremoterun.utilities.nonjdk.git.CloneGitRepo4
import net.sf.jremoterun.utilities.nonjdk.git.CopyFileRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBaseRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpecRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SourceForgeDownloader
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import net.sf.jremoterun.utilities.nonjdk.svn.SvnUtils
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipFile

@CompileStatic
class CustomObjectHandlerImpl implements CustomObjectHandler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File replicaDir

    CloneGitRepo4 cloneGitRepo3

    MavenCommonUtils mcu = new MavenCommonUtils();

    SourceForgeDownloader sfDownloader

    UrlDownloadUtils3 urlDownloadUtils = new UrlDownloadUtils3()
    IffUnzipUtils iffUnzipUtils

    File gitTmpDir;

    File androidUnArchive;

    SvnUtils svnUtils;

    public CopyFilesTmp copyFilesTmp

    static CustomObjectHandlerImpl getSelfRef(){
        MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl;
    }

    CustomObjectHandlerImpl(File gitRepo) {
        JavaVersionChecker.checkJavaVersion();
        cloneGitRepo3 = new CloneGitRepo4(gitRepo);
        replicaDir = new File(gitRepo, CustomObjectHandlerDownloadPrefix.replica.getCustomName())
        replicaDir.mkdir()
        assert replicaDir.exists()
        File sfDir = gitRepo.child(CustomObjectHandlerDownloadPrefix.sf.getCustomName())
        sfDir.mkdir()
        assert sfDir.exists()
        sfDownloader = new SourceForgeDownloader(sfDir)
        this.gitTmpDir = new File(gitRepo, CustomObjectHandlerDownloadPrefix.tmp.getCustomName())
        //File copyOnUsageDir;

        this.androidUnArchive = new File(gitRepo, CustomObjectHandlerDownloadPrefix.android.getCustomName())
        File unzup111 =  new File(gitRepo, CustomObjectHandlerDownloadPrefix.unzip.getCustomName())
        this.iffUnzipUtils   = new IffUnzipUtils(unzup111)
        gitTmpDir.mkdir()
        svnUtils = new SvnUtils(gitRepo, gitTmpDir)
        if(CopyFilesTmp.tmpDirS==null) {
            File copyOnUsageDir = new File(gitRepo, CustomObjectHandlerDownloadPrefix.copytmp.getCustomName())
            copyOnUsageDir.mkdir()
            assert copyOnUsageDir.exists()
            copyOnUsageDir.listFiles().toList().each {
                FileUtils.deleteQuietly(it)
            }
            CopyFilesTmp.tmpDirS = copyOnUsageDir
            copyFilesTmp = new CopyFilesTmp()
        }else{
            log.info "CopyFilesTmp.tmpDirS already set ${CopyFilesTmp.tmpDirS}"
        }
    }

    @Override
    File resolveToFileIfDownloaded(Object object) {
        switch (object) {
            case { object instanceof ToFileRefUnsupported }:
                ToFileRefUnsupported obj1 = object as ToFileRefUnsupported
                log.info "resoling unsupported : ${obj1.msg}"
                return null
            case { object instanceof File }:
                return (File)object;
            case { object instanceof ZeroOverheadFileRef }:
                ZeroOverheadFileRef fileToFileRef = object as ZeroOverheadFileRef;
                return fileToFileRef.resolveToFile();
            case { object instanceof UnzipExactlyRef }:
                return resolveUnzipIfDownloaded(object as UnzipExactlyRef)
            case { object instanceof UnzipRef }:
                return resolveUnzipIfDownloaded(object as UnzipRef)
            case { object instanceof FileChildLazyRef }:
                return resolveLazyChildOfDownloaded((FileChildLazyRef) object)
            case { object instanceof SfLink }:
                return sfDownloader.resolveIfDownloaded(object as SfLink)
            case { object instanceof GitRefRef }:
                return resolveRefIfDownloaded(((GitRefRef)object).ref)
            case { object instanceof GitBaseRef }:
                return cloneGitRepo3.getFileIfDownloaded((GitBaseRef) object)
            case { object instanceof GitSpecRef }:
                GitSpecRef gitSpec = (GitSpecRef) object;
                return cloneGitRepo3.getFileIfDownloaded(gitSpec.getGitSpec())
            case { object instanceof UrlProvided }:
                return resolveToFileIfDownloaded(((UrlProvided) object).convertToUrl())
            case { object instanceof MavenIdContains }:
                return mcu.findMavenOrGradle(((MavenIdContains)object).m)
            case { object instanceof MavenIdAndRepoContains }:
                return mcu.findMavenOrGradle(((MavenIdAndRepoContains)object).mavenIdAndRepo.m)
            case { object instanceof MavenIdAndRepoCustomSourceContains }:
                return mcu.findMavenOrGradle(((MavenIdAndRepoCustomSourceContains)object).mavenIdAndRepo.m)
            case { object instanceof URL }:
                return urlDownloadUtils.buildDownloadUrl((URL) object)
            case { object instanceof ClassWhereLocatedAndAlternativeRef }:
                ClassWhereLocatedAndAlternativeRef fileToFileRef = object as ClassWhereLocatedAndAlternativeRef;
                ClassWhereLocatedAndAlternative locatedAndAlternative = fileToFileRef.getRef()
                URL location3 = UrlCLassLoaderUtils.getClassLocation3(locatedAndAlternative.clazz);
                if (location3 == null) {
                    log.info "failed resolve ${locatedAndAlternative.clazz.getName()}, trying alternative ${locatedAndAlternative.alternative}"
                    return resolveToFileIfDownloaded(locatedAndAlternative.alternative)
                }
                File location = UrlCLassLoaderUtils.convertClassLocationToPathToJar2(location3)
                return location;
            case { object instanceof IfFrameworkResourceDirs }:
                IfFrameworkResourceDirs resourceDirs = object as IfFrameworkResourceDirs
                if(InfocationFrameworkStructure.ifDir!=null){
                    return resourceDirs.resolveToFile()
                }
                log.info("need define InfocationFrameworkStructure.ifDir to resolve : ${object}")
                return null;
            case { object instanceof IfFrameworkSrcDirs }:
                IfFrameworkSrcDirs resourceDirs = object as IfFrameworkSrcDirs
                if(InfocationFrameworkStructure.ifDir!=null){
                    return resourceDirs.resolveToFile()
                }
                log.info("need define InfocationFrameworkStructure.ifDir to resolve : ${object}")
                return null;
            case { object instanceof SvnSpecRef }:
                SvnSpecRef svnRef = object as  SvnSpecRef
                return getSvnRefIfDownloaded(svnRef.getSvnSpec())
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                return resolveToFileIfDownloaded(redirect)
            case { object instanceof ToFileRefRedirect2 }:
                ToFileRefRedirect2 toFileRefRedirect = (ToFileRefRedirect2) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                return resolveToFileIfDownloaded(redirect)
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                return resolveToFileIfDownloaded(refLink.enumm)
            case { object instanceof ToFileRefIfDownload }:
                // ToFileRefIfDownload must be last
                ToFileRefIfDownload refLink4 = (ToFileRefIfDownload) object
                File f44 = refLink4.resolveToFileIfDownloaded()
                if(f44==null){
                    log.info("not supported : ${object.class.name} ${object}")
                    return null
                }
                return f44
            default:
                log.info("not supported : ${object.class.name} ${object}")
                return null
        }
    }

    @Override
    File resolveToFile(Object object) {
        switch (object) {
            case { object instanceof File }:
                return (File)object;
            case { object instanceof FileChildLazyRef }:
                FileChildLazyRef refLink2 = (FileChildLazyRef) object
                return resolveLazyChild(refLink2)
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                return resolveToFile(refLink.enumm)
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                return resolveToFile(redirect)
            case { object instanceof UnzipExactlyRef }:
                return downloadAndResolveUnzip(object as UnzipExactlyRef,true)
            case { object instanceof UnzipRef }:
                return downloadAndResolveUnzip(object as UnzipRef,false)
            case { object instanceof SfLink }:
                SfLink sfLink = object as SfLink
                return sfDownloader.download(sfLink)
            case { object instanceof GitRefRef }:
                GitRefRef gitRef = (GitRefRef) object;
                return resolveRef(gitRef.ref)
            case { object instanceof GitSpecRef }:
                GitSpecRef gitSpec = (GitSpecRef) object;
                return cloneGitRepo3.cloneGitRepo3(gitSpec.getGitSpec())
            case { object instanceof SvnSpec }:
                SvnSpec svnRef = object as SvnSpec
                return resolveSvnRef(svnRef)
            case { object instanceof AndroidArchive }:
                AndroidArchive aar = (AndroidArchive) object;
                return downloadAndroid(aar);
            case { object instanceof UrlProvided }:
                UrlProvided u = object as UrlProvided
                URL url = u.convertToUrl()
                return urlDownloadUtils.downloadUrl(url)
            case { object instanceof URL }:
                URL url = object as URL
                return urlDownloadUtils.downloadUrl(url)
            case { object instanceof MavenIdAndRepoContains }:
                MavenIdAndRepoContains u2 = object as MavenIdAndRepoContains
                MavenIdAndRepo mavenId2 = u2.getMavenIdAndRepo()
                return mavenId2.resolveToFile()
            case { object instanceof MavenIdAndRepoCustomSourceContains }:
                MavenIdAndRepoCustomSourceContains u2 = object as MavenIdAndRepoCustomSourceContains
                MavenIdAndRepo mavenId2 = u2.getMavenIdAndRepo()
                return mavenId2.resolveToFile()
            case { object instanceof ClassWhereLocatedAndAlternativeRef }:
                ClassWhereLocatedAndAlternativeRef fileToFileRef = object as ClassWhereLocatedAndAlternativeRef;
                ClassWhereLocatedAndAlternative locatedAndAlternative = fileToFileRef.getRef()
                URL location3 = UrlCLassLoaderUtils.getClassLocation3(locatedAndAlternative.clazz);
                if (location3 == null) {
                    log.info "failed resolve ${locatedAndAlternative.clazz.getName()}, trying alternative ${locatedAndAlternative.alternative}"
                    return resolveToFile(locatedAndAlternative.alternative)
                }
                File location = UrlCLassLoaderUtils.convertClassLocationToPathToJar2(location3)
                return location;
            case { object instanceof MavenIdContains }:
                MavenIdContains u = object as MavenIdContains
                MavenId mavenId = u.getM()
                return mavenId.resolveToFile()
//            case { object instanceof CopyOnUsage }:
//                CopyOnUsage copyOnUsage = object as CopyOnUsage
//                return copyOnUsageImpl(copyOnUsage)
            case { object instanceof CopyFileRef }:
                CopyFileRef r = object as CopyFileRef
                File file123 = resolveToFile(r.nested)
                return copyFilesTmp.copyOnUsageImpl(file123);
            case { object instanceof ToFileRef2 }:
                ToFileRef2 u = object as ToFileRef2
                return u.resolveToFile()
            case { object instanceof ToFileRefRedirect2 }:
                ToFileRefRedirect2 toFileRefRedirect = (ToFileRefRedirect2) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                return resolveToFile(redirect)

            default:
                throw new IllegalArgumentException("${object.class.name} ${object}")

        }
    }

//    public DecimalFormat decimalFormat1 = new DecimalFormat('000')

//    File findFileToCopy(File originalFFile){
//        String orignJustName = originalFFile.getName()
//
//        for(int i=101;i<1000;i++){
//            File f5 = new File(copyOnUsageDir,decimalFormat1.format(i)+ orignJustName);
//            if(!f5.exists()){
//                return f5
//            }
//        }
//        throw new IOException("no file left in ${copyOnUsageDir} ${orignJustName}")
//    }

//    File copyOnUsageImpl(CopyOnUsage copyOnUsage5){
//        File f =copyOnUsage5.origin.resolveToFile()
//        assert f.isFile()
//        File f5 = findFileToCopy(f);
//        FileUtilsJrr.copyFile(f,f5)
//        f5.deleteOnExit()
//        return f5
//    }

    String resolvePathForRefApproximately(ToFileRef2 ref) {
        if (ref == null) {
            throw new NullPointerException('ref is null')
        }
        if (ref instanceof UrlProvided) {
            return urlDownloadUtils.mcu.buildDownloadUrlSuffix(ref.convertToUrl())
        }
        if (ref instanceof SfLink) {
            return CustomObjectHandlerDownloadPrefix.sf.getCustomName()+'/'+ref.getPath()
        }
        if (ref instanceof RefLink) {
            return resolvePathForRefApproximately(ref.fileRef)
        }
        if (ref instanceof ToFileRefRedirect) {
            return resolvePathForRefApproximately(ref.getRedirect())
        }
        if (ref instanceof ToFileRefRedirect2) {
            return resolvePathForRefApproximately(ref.getRedirect())
        }
        if (ref instanceof UnzipRef) {
            return CustomObjectHandlerDownloadPrefix.unzip.getCustomName()+'/'+resolvePathForRefApproximately(ref.refToZip);
        }
        if (ref instanceof FileChildLazyRef) {
            String parent = resolvePathForRefApproximately(ref.parentRef)
            return parent + '/' + ref.child.approximatedName();
        }
        if (ref instanceof MavenIdAndRepoContains) {
            return resolvePathForRefApproximately(ref.getMavenIdAndRepo().m)
        }
        if (ref instanceof MavenIdAndRepoCustomSourceContains) {
            return resolvePathForRefApproximately(ref.getMavenIdAndRepo().m)
        }
        if (ref instanceof MavenIdContains) {
            MavenId mavenId = ref.m
            String childd = mavenId.toString()
            childd = childd.replace(':', '/')
            return "${CustomObjectHandlerDownloadPrefix.maven.getCustomName()}/${childd}"
        }
        if (ref instanceof SvnSpecRef) {
            return UrlSymbolsReplacer.replaceBadSymbols(ref.getSvnSpec().repo)
        }
        if (ref instanceof GitSpecRef) {
            return CloneGitRepo4.createGitRepoSuffix(ref.getGitSpec().repo);
        }
        throw new UnsupportedOperationException("${ref.getClass().getName()} ${ref}")
    }

    File resolveUnzipIfDownloaded(UnzipRef ref) {
        String suffix = resolvePathForRefApproximately(ref.refToZip);
//        String suffix = resolvePathForRefApproximately(ref.refToZip,false);
        iffUnzipUtils.init()
        File f = iffUnzipUtils.buildRefToUnzipFile(suffix)
        if (!f.exists()) {
            log.info "file not exist : ${f}"
            return null
        }
        return f
    }

    File downloadAndResolveUnzip(UnzipRef ref,boolean isZipSure) {
        String suffix = resolvePathForRefApproximately(ref.refToZip);
        //String suffix = resolvePathForRefApproximately(ref.refToZip,true);
        File unzipRef = iffUnzipUtils.buildRefToUnzipFile(suffix)
        if(unzipRef.exists()){
            return unzipRef
        }
        File zipFile = resolveToFile(ref.refToZip)
        File f = iffUnzipUtils.unzip(zipFile, suffix,isZipSure)
        assert unzipRef.exists()
        return unzipRef
    }

//    File downloadUrlAndUnzip(SfLink url) {
//        File f = url.resolveToFile()
//        return iffUnzipUtils.unzip(f, 'sf/' + url.path,false)
//    }



    File resolveLazyChild(FileChildLazyRef childLazyRef) {
        File parentRefResolved = childLazyRef.parentRef.resolveToFile()
        if (parentRefResolved == null) {
            throw new IOException("Failed resolve : ${childLazyRef.parentRef}")
        }
        if (!parentRefResolved.exists()) {
            throw new FileNotFoundException(parentRefResolved.getAbsolutePath())
        }
        File childFile = childLazyRef.child.resolveChild(parentRefResolved)
//        File childFile = parentRefResolved.child(childLazyRef.child)
        if (!childFile.exists()) {
            throw new FileNotFoundException(childFile.getAbsolutePath())
        }
        return childFile
    }


    File resolveLazyChildOfDownloaded(FileChildLazyRef childLazyRef) {
        File parentRefResolved = resolveToFileIfDownloaded(childLazyRef.parentRef);
        if (parentRefResolved == null) {
            return null
        }
        if (!parentRefResolved.exists()) {
            return null
        }
        File childFile = childLazyRef.child.resolveChild(parentRefResolved)
//        if (!childFile.exists()) {
//            throw new FileNotFoundException(childFile.getAbsolutePath())
//        }
        return childFile
    }

    File downloadAndroid(AndroidArchive androidArchive) {
        File someDir = new File(mcu.mavenDefaultSettings.grapeFileFinder.getMavenLocalDir2(), "${androidArchive.m.groupId}/${androidArchive.m.artifactId}")

        File aarFile = new File(someDir, "aars/${androidArchive.m.artifactId}-${androidArchive.m.version}.aar")
        if (!aarFile.exists()) {

            MavenDependenciesResolver ivyDepResolver2 = MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver
            List<MavenId> dependencies = ivyDepResolver2.resolveAndDownloadDeepDependencies(androidArchive.m, false, false)
            log.info "${dependencies}"
            if (!aarFile.exists()) {
                throw new FileNotFoundException("Failed resolve ${androidArchive} ${aarFile}")
            }
        }
        File toFile = new File(androidUnArchive, "${androidArchive.m.groupId}/${androidArchive.m.artifactId}/${androidArchive.m.artifactId}-${androidArchive.m.version}.jar")
        if (toFile.exists()) {
            return toFile
        }
        File parentFile = toFile.getParentFile()
        parentFile.mkdirs()
        if (!parentFile.exists()) {
            throw new IOException("Failed create : ${parentFile}")
        }
        boolean unpacked = ZipUtil.unpackEntry(new ZipFile(aarFile), "classes.jar", toFile)
        if (!unpacked) {
            throw new IOException("Failed find classes.jar in  : ${aarFile}")
        }
        if (!toFile.exists()) {
            throw new IOException("Failed extract classes.jar from  : ${aarFile}")
        }
        return toFile
    }

    @Override
    void add(AddFilesToClassLoaderCommon adder, Object object) {
        switch (object) {
            case { object instanceof AddToAdderSelf }:
                AddToAdderSelf addToAdderSelf = object as AddToAdderSelf;
                addToAdderSelf.addToAdder(adder)
                break
            case { object instanceof RefLink }:
                RefLink refLink = (RefLink) object
                adder.addGenericEntery(refLink.enumm)
//                add(adder, refLink.enumm)
                break
            case { object instanceof ToFileRefRedirect }:
                ToFileRefRedirect toFileRefRedirect = (ToFileRefRedirect) object
                ToFileRef2 redirect = toFileRefRedirect.getRedirect()
                assert redirect != null
                adder.addGenericEntery(redirect)
                break
            case { object instanceof GitBinaryAndSourceRefRef }:
                GitBinaryAndSourceRefRef gitRef = (GitBinaryAndSourceRefRef) object;
                addGitRef(adder, gitRef.ref)
                break;
            case { object instanceof MavenIdAndRepoCustomSourceContains }:
                MavenIdAndRepoCustomSourceContains gitRef = (MavenIdAndRepoCustomSourceContains) object;
                File file44 = gitRef.getMavenIdAndRepo().resolveToFile()
                if (adder instanceof AddFilesWithSourcesI) {
                    adder.addGenericEntery( new BinaryWithSource3(gitRef.getMavenIdAndRepo(), gitRef.getSourceRedirect()));
                }else{
                    adder.addGenericEntery(file44)
                }
                break;
            case { object instanceof AndroidArchive }:
                AndroidArchive aar = (AndroidArchive) object;
                File f = downloadAndroid(aar);
                if (adder instanceof AddFilesWithSourcesI) {
                    File sourceFile = adder.addSourceMNoExceptionOnMissing aar.m;
                    if (sourceFile == null) {
                        log.info "not source for android artifact : ${aar.m}"
                        adder.addGenericEntery f
                    }else{
                        adder.addGenericEntery new BinaryWithSource3(f,sourceFile)
                    }
                }else{
                    adder.addGenericEntery f
                }
                break
            default:
                File f = resolveToFile(object)
                adder.add f
                break;
        }
    }


    File getSvnRefIfDownloaded(SvnSpec svnRef) {
        File checkout = svnUtils.getFileIfDownloaded(svnRef)
        return checkout
    }

    File resolveSvnRef(SvnSpec svnRef) {
        File checkout = svnUtils.svnCheckout(svnRef)
         return checkout
    }

    public boolean updateRepoIfRefNotExists = true

    void addGitRef(AddFilesToClassLoaderCommon adder, GitBinaryAndSourceRef gitRef) {
        File gitRepo = cloneGitRepo3.cloneGitRepo3(gitRef)
        File bin = new File(gitRepo, gitRef.pathInRepo)
        File src = new File(gitRepo, gitRef.src)
        boolean allFound = bin.exists() && src.exists()
        if (!allFound && updateRepoIfRefNotExists) {
            log.info "updating repo : ${gitRef}"
            GitRepoUtils.updateGitRepo2(gitRef)
        }
        if (!bin.exists()) {
            throw new FileNotFoundException("bin not found : ${gitRef}")
        }
        if (!src.exists()) {
            throw new FileNotFoundException("src not found : ${gitRef}")
        }
        bin = getRef23(bin)
        src = getRef23(src)
        BinaryWithSource binaryWithSource = new BinaryWithSource(bin, src)
        adder.addBinaryWithSource(binaryWithSource)
    }

    File resolveRef(GitRef gitRef) {
        File gitRepo = cloneGitRepo3.cloneGitRepo3(gitRef)
        File fileInRepo = new File(gitRepo, gitRef.pathInRepo)
        if (!fileInRepo.exists()) {
            log.info "file not exist : ${fileInRepo} , ${gitRef}"
            return fileInRepo
        }
        fileInRepo = getRef23(fileInRepo)
        return fileInRepo
    }

    File resolveRefIfDownloaded(GitRef gitRef) {
        File gitRepo = cloneGitRepo3.getFileIfDownloaded(gitRef)
        if (gitRepo == null || !gitRepo.exists()) {
            return null;
        }

        File fileInRepo = new File(gitRepo, gitRef.pathInRepo)
        if (!fileInRepo.exists()) {
            log.info "file not exist : ${fileInRepo} , ${gitRef}"
            return fileInRepo
        }
        fileInRepo = getRef23(fileInRepo)
        return fileInRepo
    }



    private File getRef23(File fileInRepo) {
        if (fileInRepo.isFile()) {
            String pathToParent = mcu.getPathToParent(cloneGitRepo3.gitBaseDir, fileInRepo)
            File f = new File(replicaDir, pathToParent)
            boolean existsBefore = f.exists()
            try {
                copyFileIfNeeded(fileInRepo, f)
                return f;
            }catch(FileNotFoundException e){
                log.warn("failed copy from ${fileInRepo} to ${f}",e)
                if(existsBefore){
                    return copyFilesTmp.copyOnUsageImpl(fileInRepo)
                }
                throw e;
            }
        }
        return fileInRepo
    }

    static boolean isCopyFileNeeded(File src, File dest) {
        assert src.exists()
        dest.parentFile.mkdirs()
        assert dest.parentFile.exists()
        if (!dest.exists()) {
            return true
        }
        if (src.length() != dest.length()) {
            return true
        }
        if (src.lastModified() != dest.lastModified()) {
            return true
        }
        return false
    }

    static void copyFileIfNeeded(File src, File dest) {
        boolean needCopy = isCopyFileNeeded(src, dest)
        if (needCopy) {
            log.info("coping ${src} to ${dest}")
            FileUtilsJrr.copyFile(src, dest)
            dest.setLastModified(src.lastModified())
        }
    }

    void addGitRef(AddFilesToClassLoaderCommon adder, GitRef gitRef) {
        File fileInRepo = resolveRef(gitRef)
        adder.addF(fileInRepo)
    }
}
