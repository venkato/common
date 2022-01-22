package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.Logger

@CompileStatic
class GrapeRepoHashToFileMap {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Map<String, List> sha1Dups = [:]
    Map<String, MavenId> hash2MavenIdMap = [:]
    Map<String, File> hash2FileMap = [:]
    MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()
    Map<MavenId, String> mavenId2HashMap = [:]
    long initTime;


    public
    static ToFileRef2 noMavenIdFilesJsonDefault = BaseDirSetting.baseDirSetting.childL("configs/ivy_hash_cache2.json")


    void init(LongTaskInfo longTaskInfo) {
        assert longTaskInfo!=null
        Date start = new Date()
        Map<File, String> fileCache3 = [:]

        fileCache3.putAll(File2HashMapJsonSaver.readJson2(noMavenIdFilesJsonDefault.resolveToFile()))

        mavenCommonUtils.mavenDefaultSettings.grapeFileFinder.getMavenLocalDir2().eachFileRecurse(FileType.FILES, {
            handleFile(it,fileCache3,longTaskInfo)
        })
        initTime = System.currentTimeMillis() - start.time
        File2HashMapJsonSaver.saveToJson(fileCache3, noMavenIdFilesJsonDefault.resolveToFile())
    }

    void handleFile(File file1,Map<File, String> fileCache3 ,LongTaskInfo longTaskInfo ){
        String name = file1.name;
        longTaskInfo.setCurrentTask("analizing ${file1}")
        if (name.endsWith(".jar") && !name.endsWith("-sources.jar")) {
//                String sha1 = file1.parentFile.name
//                hash2FileMap.put(sha1, file1)
            String sha1;
            sha1 = fileCache3.get(file1)
            if (sha1 == null) {
                sha1 = FileCheckSumCalc.calcSha1ForFile(file1)
                fileCache3.put(file1, sha1)
            }
            MavenId mi = mavenCommonUtils.mavenDefaultSettings.grapeFileFinder.detectMavenIdFromFileName(file1, false,mavenCommonUtils.fileType)
            if (mi != null) {

//                    hash2FileMap.put(sha1, file1)
                mavenId2HashMap.put(mi, sha1)
                if (sha1Dups.containsKey(sha1)) {
                    sha1Dups.get(sha1).add(file1)
                } else {
                    MavenId mavenIdBefore = hash2MavenIdMap.put(sha1, mi)
                    if (mavenIdBefore != null) {
                        List dupInfo = [mavenIdBefore, file1]
                        sha1Dups.put(sha1, dupInfo)
                        hash2MavenIdMap.remove(sha1)
                    }
                }

            } else {
                log.info "failed detect maven id : ${file1}"
            }
        }
    }


}
