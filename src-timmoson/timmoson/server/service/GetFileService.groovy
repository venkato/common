package timmoson.server.service

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.timmoson.RemoteRef
import net.sf.jremoterun.utilities.nonjdk.timmoson.TransferObject
import org.apache.commons.codec.digest.DigestUtils
import timmoson.client.ClientSendRequest
import timmoson.common.CallInfoServer
import timmoson.common.sertcp.RemoteService
import timmoson.server.ServiceLocator;

import java.util.logging.Logger;

@CompileStatic
class GetFileService implements GetFileServiceI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String password1

    boolean isAuth(String pass) {
        return password1 == pass
    }

    @Override
    void getDirOrFile(RemoteRef transferObject, Map<String, String> sha1, Map<String, Long> lengthes, BytesReceiver bytesReceiver, int bytesToRead, String password) {
        if (isAuth(password)) {
            File f = resolveFile(transferObject)
            if (f.isDirectory()) {
                Map<String, File> map1 = [:]
                List<String> childs = []
                List<File> files = []
                f.traverse {
                    if (it.isFile()) {
                        files.add(it)
                        String childPath = f.getPathToParent(it)
                        map1.put(childPath, f)
                        childs.add(childPath)
                    }
                }
                ClientSendRequest.getClientParams().waitResult = false
                bytesReceiver.notifyFiles(childs)
                int i = 0;
                while (i < childs.size()) {
                    String childPathSuffix = childs[i]
                    File fileLocal1 = new File(f, childPathSuffix)
                    try {
                        Long legthh = lengthes.get(childPathSuffix)
                        if (legthh == null) {
                            legthh = -1
                        }
                        bytesReceiver.processingNextFile(i)
                        getFileOneImpl(fileLocal1, sha1.get(childPathSuffix), legthh, bytesReceiver, bytesToRead)
                        i++
                    } catch (Throwable e) {
                        log.info("failed get on ${childPathSuffix} ${fileLocal1}", e)
                        throw e
                    }

                }
            }
        } else {
            bytesReceiver.wrongPassword()
        }
    }

    @Override
    void getFiles(List<TransferObject> fileId, BytesReceiver bytesReceiver, int bytesToRead, String password) {
        if (isAuth(password)) {
            int i = 0;
            while (i < fileId.size()) {
                ToFileRef2 ref21 = fileId[i].remoteRef.createFullRef()
                try {
                    bytesReceiver.processingNextFile(i)
                    getFileOne(fileId[i], bytesReceiver, bytesToRead)
                    i++
                } catch (Throwable e) {
                    log.info("failed get on ${i} ${ref21}", e)
                    throw e
                }
            }
        } else {
            bytesReceiver.wrongPassword()
        }
    }

    public static String calcSha(File f) {
        BufferedInputStream stream1 = f.newInputStream()
        try {
            String hex1 = DigestUtils.sha256Hex(stream1)
            return hex1
        } finally {
            JrrIoUtils.closeQuietly2(stream1, log)
        }
    }

    void getFileOne(TransferObject transferObject, BytesReceiver bytesReceiver, int bytesToRead) {
        File f = resolveFile(transferObject.remoteRef)
        getFileOneImpl(f, transferObject.sha1, transferObject.fileLength, bytesReceiver, bytesToRead)
    }

    void getFileOneImpl(File f, String sha1, long fileLength, BytesReceiver bytesReceiver, int bytesToRead) {
        CallInfoServer callInfoServer1 = RemoteService.callsInfos.get()

        assert callInfoServer1 != null
        if (f.length() == 0) {
            ClientSendRequest.getClientParams().waitResult = false
            bytesReceiver.allSentFileIsEmpty()
        }
        boolean needSend11 = false
        if (f.length() != fileLength) {
            needSend11 = true
        }
        if (!needSend11) {
            if (sha1 == null) {
                needSend11 = true
            }
        }
        if (!needSend11) {
            needSend11 = (calcSha(f) != sha1)
        }
        log.info("needSend=${needSend11} ${f}")
        if (needSend11) {
            byte[] bs = new byte[bytesToRead]
            BufferedInputStream stream2 = f.newInputStream()
            try {
                while (true) {
                    int read1 = stream2.read(bs)
                    if (read1 == 0) {
                        assert false
                    }
                    if (read1 == -1) {
                        log.info("all sent ${f}")
                        ClientSendRequest.getClientParams().waitResult = false
                        bytesReceiver.allSent();
                        break
                    }
                    if (read1 == bytesToRead) {
                        ClientSendRequest.getClientParams().waitResult = false
                        log.info("sendign bytes = ${bs.length}")
                        bytesReceiver.sendBytes(bs)
                    } else {
                        ClientSendRequest.getClientParams().waitResult = false
                        byte[] bs2 = new byte[read1]
                        System.arraycopy(bs, 0, bs2, 0, read1)
                        log.info("sendign bytes = ${bs2.length}")
                        bytesReceiver.sendBytes(bs2)
                    }
                }
            } finally {
                JrrIoUtils.closeQuietly2(stream2, log)
            }
        } else {
            ClientSendRequest.getClientParams().waitResult = false
            log.info("no changes ${f}")
            bytesReceiver.noChanges()
        }
    }

    File resolveFile(RemoteRef fileId) {
        return fileId.createFullRef().resolveToFile()
    }

    void registerService() {
        ServiceLocator.regNewService(GetFileServiceI, this)
    }


}
