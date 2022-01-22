package net.sf.jremoterun.utilities.nonjdk.smb

import groovy.transform.CompileStatic;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc;
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils

import java.util.logging.Logger;

@CompileStatic
public class SmbUtilsJrr {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    static boolean isFileEqualsS(File f1, File f2) {
//        if (!f2.exists()) {
//            return false
//        }
//        assert f1.exists()
//        if (f1.length() != f2.length()) {
//            return false
//        }
//        String sha1 = FileCheckSumCalc.calcSha256ForFile(f1)
//        String sha2 = FileCheckSumCalc.calcSha256ForFile(f2)
//        return sha1 == sha2
//    }


    static String calcSha256ForFile(SmbFile file) {
        InputStream fis = file.getInputStream()
        try {
            String hex = DigestUtils.sha256Hex(fis)
            return hex;
        } finally {
            JrrIoUtils.closeQuietly2(fis, log)
        }
    }



    static void copyDirectory(SmbFile smbFile, File file2) {
        if(smbFile.isFile()){
            copyFile(smbFile,file2)
        }else {
            if(!file2.exists()) {
                file2.mkdir()
            }
            List<SmbFile> files = smbFile.listFiles().toList()
            files.each {
                try {
                    File dest1 = new File(file2, it.getName()+'/');
                    copyDirectory(it, dest1)
                }catch (Throwable w){
                    log.info "failed on ${it} : ${w}"
                    throw w
                }
            }
        }
    }


    static void copyDirectory(File file1, SmbFile smbFile) {
        if(file1.isFile()){
            copyFile(file1,smbFile)
        }else {
            if(!smbFile.exists()) {
                smbFile.mkdir()
            }
            List<File> files = file1.listFiles().toList()
            files.each {
                try {
                    SmbFile dest1 = new SmbFile(smbFile, it.getName()+'/');
                    copyDirectory(it, dest1)
                }catch (Throwable w){
                    log.info "failed on ${it} : ${w}"
                    throw w
                }
            }
        }
    }



    static void copyFile(File src,SmbFile dest){
        assert src.exists()
        OutputStream out1 = dest.getOutputStream()
        InputStream in1 = src.newInputStream()
        try{
            IOUtils.copy(in1,out1)
        }finally {
            JrrIoUtils.closeQuietly2 (out1,log)
            JrrIoUtils.closeQuietly2 (in1,log)
        }
    }


    static void copyFile(SmbFile src,File dest){
        assert src.exists()
        OutputStream out1 = dest.newOutputStream()
        InputStream in1 = src.getInputStream()
        try{
            IOUtils.copy(in1,out1)
        }finally {
            JrrIoUtils.closeQuietly2 (out1,log)
            JrrIoUtils.closeQuietly2 (in1,log)
        }
    }

}
