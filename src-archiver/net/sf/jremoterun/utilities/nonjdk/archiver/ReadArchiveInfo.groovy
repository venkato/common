package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.generalutils.PeriodToStringHumanConverter
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.ziputil.ZipReadEntry
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class ReadArchiveInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrGroovyScriptRunner groovyScriptRunner = new JrrGroovyScriptRunner()
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
        ArchiveInfo content = readContent(s, zipArchive.getName())
        String dateDiff = periodToStringHumanConverter.diff(content.creationDate.date,new Date())
        return "${zipArchive.getName()} ${dateDiff} ${content.includeFilter} ${content.contentHash}"
    }

    ArchiveInfo readContent(File zipArchive) {
        byte[] entry = ZipReadEntry.extractOneEntry(zipArchive, JrrArchiveSettings.archivePath);
        if(entry==null){
            throw new Exception("Entry not found ${JrrArchiveSettings.archivePath}")
        }
        String s = new String(entry)
        return readContent(s, zipArchive.getName())
    }

    ArchiveInfo readContent(String content, String zipArchive) {
        ArchiveInfo archiveInfo = new ArchiveInfo()
        groovyScriptRunner.loadSettingsWithParam(content, zipArchive.replace(ClassNameSuffixes.dotgroovy.customName, ''), archiveInfo);
        return archiveInfo;
    }


}
