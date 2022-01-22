package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import org.apache.commons.io.FileUtils

import java.text.DecimalFormat;
import java.util.logging.Logger;

@CompileStatic
class CopyFilesTmp implements Closeable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static File tmpDirS;
    public File tmpDir = tmpDirS;
    public String filePrefix = '';
    public List<File> removeOnFinishFiles = []


    File addClassPathCopy(ToFileRef2 f){
        return addClassPathCopy (f.resolveToFile())
    }

    File addClassPathCopy(File f){
        File copy34 = copyOnUsageImpl(f)
        removeOnFinishFiles.add(copy34)
        return copy34
    }

    void close(){
        removeOnFinishFiles.each {
            removeTmpFile(it)
        }
    }

    void removeTmpFile(File f) {
        if (f.exists()){
            boolean removed = f.delete()
//            boolean removed = FileUtils.deleteQuietly(f)
            if(removed) {
                log.info "removed ok : ${f.getName()}"
            }else{
                log.info("failed remove ${f.getAbsolutePath()}")
                f.deleteOnExit()
            }
        }
    }


    static public DecimalFormat decimalFormat1 = new DecimalFormat('000')

    File findFileToCopy(File originalFFile){
        assert tmpDir!=null
        String orignJustName = originalFFile.getName()

        for(int i=101;i<1000;i++){
            File f5 = new File(tmpDir,filePrefix+decimalFormat1.format(i)+ orignJustName);
            if(!f5.exists()){
                return f5
            }
        }
        throw new IOException("no file left in ${tmpDir} ${orignJustName}")
    }

    File copyOnUsageImpl(File f){
        //File f =copyOnUsage5.origin.resolveToFile()
        assert f.isFile()
        File f5 = findFileToCopy(f);
        FileUtilsJrr.copyFile(f,f5)
        f5.setLastModified(System.currentTimeMillis())
        log.info "copied to ${f5}"
//        f5.deleteOnExit()
        return f5
    }


}
