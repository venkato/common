package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory2
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.generalutils.PeriodToStringHumanConverter
import net.sf.jremoterun.utilities.nonjdk.ziputil.ZipReadEntry

import java.util.logging.Logger

@CompileStatic
class ReadArchiveInfo extends RunnableWithParamsFactory2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public PeriodToStringHumanConverter periodToStringHumanConverter = new PeriodToStringHumanConverter()

    String readContent2Human(File zipArchive) {
        ArchiveInfo content = readContent(zipArchive)
        String dateDiff = periodToStringHumanConverter.diff(content.creationDate.date,new Date())
        return "${zipArchive.getName()} ${dateDiff} ${content.includeFilter} ${content.contentHash}"
    }

    String readGenericInfo(File zipArchive) {
        String lastModifed2
        long modified = zipArchive.lastModified()
        if(modified>0){
            lastModifed2= periodToStringHumanConverter.diff(new Date(modified),new Date())
        }else{
            lastModifed2 = 'failedReadLastMofied'
        }
        return "${zipArchive.getName()} ${lastModifed2} ${FileCheckSumCalc.calcSha1ForFile(zipArchive)} noJrrArchiveInfo "
    }

    String readContent3Human(File zipArchive) {
        byte[] entry = ZipReadEntry.extractOneEntry(zipArchive, JrrArchiveSettings.archivePath);
        if(entry==null){
            return readGenericInfo(zipArchive)
        }
        String s = new String(entry)
        ArchiveInfo content = readContent(s)
        String dateDiff = periodToStringHumanConverter.diff(content.creationDate.date,new Date())
        return "${zipArchive.getName()} ${dateDiff} ${content.includeFilter} ${content.contentHash}"
    }

    ArchiveInfo readContent(File zipArchive) {
        byte[] entry = ZipReadEntry.extractOneEntry(zipArchive, JrrArchiveSettings.archivePath);
        if(entry==null){
            throw new Exception("Entry not found ${JrrArchiveSettings.archivePath}")
        }
        String s = new String(entry)
        return readContent(s)
    }

    ArchiveInfo readContent(String content) {
        ArchiveInfo archiveInfo = new ArchiveInfo()
        loadSettingsWithParam(content,archiveInfo)
        return archiveInfo;
    }


}
