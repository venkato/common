package timmoson.client.getmanyfiles

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.timmoson.RemoteRef
import net.sf.jremoterun.utilities.nonjdk.timmoson.TransferObject
import timmoson.client.ClientSendRequest
import timmoson.client.TimmosonSessionStore
import timmoson.common.sertcp.TcpSession
import timmoson.server.service.BytesReceiver
import timmoson.server.service.GetFileServiceI;

import java.util.logging.Logger;

@CompileStatic
class GetFileClient {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GetFileServiceI fileServiceI
    public String password

    public int bytesToSendInOnGo=8196


    static GetFileServiceI getServiceDefault(TcpSession tcpSession) {
        return tcpSession.makeClient(GetFileServiceI)
    }

    GetFileClient(GetFileServiceI fileServiceI) {
        this.fileServiceI = fileServiceI
    }


    GetFileClient(TimmosonSessionStore tcpSession) {
        this(tcpSession.getTcpSession())
    }

    GetFileClient(TcpSession tcpSession) {
        this.fileServiceI = getServiceDefault(tcpSession)
    }


//    void getFile(ToFileRef2 file, GetFileServiceI fileServiceI) {
//        File file1 = resolveFile(file)
//        String sha256
//        if (file1.exists()) {
//            BufferedInputStream stream1 = file1.newInputStream()
//            sha256 = DigestUtils.sha256Hex(stream1)
//            stream1.close()
//        }
//        getFileImpl(file,fileServiceI,file1,sha256)
//    }



    void getFileOne(TransferObject fileRef, File destFile, File tmpFile) {
        List<File> files = [destFile]
        getFileMany([fileRef], files, tmpFile)
    }

    void getDirOrFile(RemoteRef transferObject, Map<String, String> sha1, Map<String, Long> lengthes, File localDir, File tmpFile){
        log.info("sha = ${sha1}")
        log.info("lengthes = ${lengthes}")
        BytesReceiverManyFile bytesReceiver = new BytesReceiverManyFile( localDir, tmpFile);
        ClientSendRequest.clientParams.waitResult = true
        fileServiceI.getDirOrFile(transferObject, sha1,lengthes, bytesReceiver, bytesToSendInOnGo,password)
        if(bytesReceiver.exceptionOccured!=null){
            throw bytesReceiver.exceptionOccured
        }
    }


    void getFileMany(List<TransferObject> srcFileRefs, List<File> destFiles, File tmpFile) {
//        InvokcationAccessor invokcationAccessor = fileServiceI as InvokcationAccessor
//        JavassistProxyFactory factory1 = invokcationAccessor._getJavassistProxyFactory()
//        ObjectWrapper noChanges3 = new ObjectWrapper(false)
        BytesReceiverManyFile bytesReceiver = createBytesReceiverManyFile(srcFileRefs, destFiles, tmpFile);
        ClientSendRequest.clientParams.waitResult = true
        fileServiceI.getFiles(srcFileRefs, bytesReceiver, bytesToSendInOnGo,password)
        if(bytesReceiver.exceptionOccured!=null){
            throw bytesReceiver.exceptionOccured
        }
    }

    BytesReceiverManyFile  createBytesReceiverManyFile( List<TransferObject> srcFileRefs, List<File> destFiles, File tmpFile){
        return new BytesReceiverManyFile( destFiles, tmpFile);
    }


    File resolveFile(ToFileRef2 file) {
        return file.resolveToFile()
    }


}
