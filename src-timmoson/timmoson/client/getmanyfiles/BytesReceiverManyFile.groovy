package timmoson.client.getmanyfiles

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.ObjectWrapper
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import timmoson.server.service.BytesReceiver

import java.util.logging.Logger;

@CompileStatic
class BytesReceiverManyFile implements BytesReceiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public volatile int idd = -1;

    public volatile BufferedOutputStream stream1;
//    List<TransferObject> filesRef
    //List<String> filesFromDir
    public List<File> filesReal
    public File baseDirForDirs
    public File tmpFile2
    public volatile Throwable exceptionOccured

    BytesReceiverManyFile(File baseDirForDirs, File tmpFile2) {
        this.baseDirForDirs = baseDirForDirs
        this.tmpFile2 = tmpFile2
        assert tmpFile2 != null
        assert baseDirForDirs!=null
        assert baseDirForDirs.exists()
    }

    BytesReceiverManyFile(List<File> filesReal, File tmpFile2) {
//        this.filesRef = filesRef
        this.filesReal = filesReal
        this.tmpFile2 = tmpFile2
        assert tmpFile2 != null
        assert filesReal!=null
        assert filesReal.size()>0
    }

    @Override
    Class sendAsClass() {
        return BytesReceiver
    }

    @Override
    void notifyFiles(List<String> files) {

        try {
            log.info "${files}"
            files.collect { assert !it.contains('..') }

            filesReal = files.collect { new File(baseDirForDirs, it) }
            filesReal.collect { it.getParentFile() }.each {
                it.mkdirs()
                assert it.exists()
            }
        } catch (Throwable e) {
            onException(e)
        }
    }

    @Override
    void sendBytes(byte[] bs) {
        try {
            log.info "getting bytes count = ${bs.length}"
            checkStreamNull()
            stream1.write(bs)
        } catch (Throwable e) {
            onException(e)
        }
    }

    void onException(Throwable e) {
        if(exceptionOccured==null) {
            exceptionOccured = e
        }
        log.warn("", e)
        throw e
    }

    @Override
    void noChanges() {
        try {
            log.info "${getFileId()} no changes"
            checkStreamNull()
            stream1.close()
            cleanTmpFile(tmpFile2)
        } catch (Throwable e) {
            onException(e)
        }
    }

    void checkStreamNull() {
        if (stream1 == null) {
            throw new NullPointerException('stream is null')
        }
    }

    @Override
    void allSentFileIsEmpty() {
        try {
            log.info "${getFileId()} all sentFile is empty"
            if(stream1!=null) {
                stream1.close()
            }
            cleanTmpFile(tmpFile2)
            FileOutputStream realF = new FileOutputStream(filesReal[getFileId()], false)
            realF.close()
        } catch (Throwable e) {
            onException(e)
        }
    }

    @Override
    void allSent() {
        try {
            log.info "${getFileId()} all sent"
            checkStreamNull()
            copyCurrentFile()
        } catch (Throwable e) {
            onException(e)
        }
    }

    private int getFileId(){
        return idd
    }

    void copyCurrentFile(){
        try {
            stream1.flush()
            stream1.close()
            if(filesReal==null){
                throw new NullPointerException("real files is null")
            }
            if(getFileId()<0){
                throw new NullPointerException('id object is null')
            }
            if(getFileId()>filesReal.size()){
                throw new IllegalArgumentException("real files size = ${filesReal.size()},  got ${getFileId()}")
            }
            File file1 = filesReal[getFileId()]
            if(file1==null){
                throw new IllegalArgumentException("file is null for ${getFileId()}")
            }
            log.info("${getFileId()} coping size=${tmpFile2.length()} to ${file1} ")
            FileUtilsJrr.copyFile(tmpFile2, file1)
            assert tmpFile2.length()== file1.length()
            cleanTmpFile(tmpFile2)
        }catch(Throwable e){
            String toObj="${getFileId()}"
            if(getFileId()!=null && (getFileId()<filesReal.size())){
                toObj += ' file ='+filesReal[getFileId()]
            }
            log.warn("failed copy file from ${tmpFile2} to ${toObj}",e)
            throw e
        }
    }

    void cleanTmpFile(File tmpFile3) {
//        boolean deleted = tmpFile3.delete()
        new FileOutputStream(tmpFile3).close()
        stream1=null
//        if (!deleted) {
//            throw new Exception("failed remove tmp file ${tmpFile3}")
//        }

    }

    @Override
    void processingNextFile(int i) {
        try {
            log.info "getting next file ${i}"
//            if(stream1!=null){
//                copyCurrentFile()
//            }
            assert stream1==null
            if(i<0){
                throw new IllegalArgumentException("negative : ${i}")
            }
            if(i>=filesReal.size()){
                throw new IllegalStateException("real files size = ${filesReal.size()},  got ${i}")
            }
            idd= i
            stream1 = tmpFile2.newOutputStream()

        } catch (Throwable e) {
            onException(e)
        }
    }

    @Override
    void wrongPassword() {
        log.info "wrongPassword"
    }
}
