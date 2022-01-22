package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.io.DirCopyUtils
import net.sf.jremoterun.utilities.nonjdk.ioutils.CopyManyFilesToOneDir
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.Backup4
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDir
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals
import net.sf.jremoterun.utilities.nonjdk.linux.EolConversion
import net.sf.jremoterun.utilities.nonjdk.linux.LinuxEolTranslation;

import java.util.logging.Logger;

@CompileStatic
class JrrStartCopyUtils extends DirCopyUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List<FileChildLazyRef> javaAgents = [JrrStarterJarRefs2.java11base.gitOriginRef(),
                                                       JrrStarterJarRefs2.jremoterun.gitOriginRef(),
    ]

    public static List<FileChildLazyRef> copyToEclipse = [JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.calcGitRef(),
                                                          JrrStarterJarRefs2.java11base.gitOriginRef(),
                                                          JrrStarterJarRefs2.jremoterun.gitOriginRef(),
                                                          JrrStarterJarRefs2.jrrassist.gitOriginRef(),
                                                          JrrStarterJarRefs2.jrrbasetypes.gitOriginRef(),
    ]

    static CopyManyFilesToOneDir copyLibs(CopyManyFilesToOneDir copyManyFilesToOneDir) {
        copyManyFilesToOneDir.addSources(JrrStarterJarRefs2.values().toList().collect { it.gitOriginRef() })
        copyManyFilesToOneDir.copyFiles(JrrStarterOsSpecificFiles.copyDir)
    }

    JrrStartCopyUtils(File newLoc) {
        super(newLoc)
    }

    void copyStarterToNewLocation() {
        JrrStarterOsSpecificFilesSrc.values().toList().each {
            copyDir(it.calcGitRefSrc(), it.getSrcPath().child)
        }
        JrrStarterJarRefs2.values().toList().each {
            copyOneFileToDir(it.gitOriginRef(), JrrStarterOsSpecificFiles.originDir.ref.child, EolConversion.asIs, it)
            copyOneFileToDir(it.gitOriginRef(), JrrStarterOsSpecificFiles.copyDir.ref.child, EolConversion.asIs, it)
        }
        copyOneFileToDir(JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef, JrrStarterOsSpecificFiles.onejar.ref.child, EolConversion.asIs, JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef);
        JrrStarterOsSpecificFilesFirstDownload.values().toList().each {
            copyOneFileToDir(it, JrrStarterOsSpecificFilesSrc.firstdownload.ref.child, it.getEolConversion(), it)
        }
        copyDir(JrrStarterOsSpecificFiles.log4j2_config, JrrStarterOsSpecificFiles.log4j2_config.ref.child)
    }


    Boolean copyOneFileToDir(ToFileRef2 fromFile2, String dest, EolConversion convertEol, Object enumRef) {
        File toDir = new File(newLoc, dest)
        createDirAndCheck(toDir)
        File file4 = fromFile2.resolveToFile()
        if(enumRef instanceof JrrStarterOsSpecificFilesFirstDownload){
            if(enumRef.optionalFile){
                if (!file4.exists()) {
                    log.info "ignore copying optional file : ${dest}"
                    skippedAll.add(file4)
                    return false
                }

            }
        }
        return copyOneFile(file4, toDir, convertEol)
    }

    public int rotateCount = 30


}
