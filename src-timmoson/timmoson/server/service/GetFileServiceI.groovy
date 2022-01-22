package timmoson.server.service

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.timmoson.RemoteRef
import net.sf.jremoterun.utilities.nonjdk.timmoson.TransferObject;

@CompileStatic
interface GetFileServiceI {

    void getDirOrFile(RemoteRef transferObject, Map<String, String> sha1, Map<String, Long> lengthes, BytesReceiver bytesReceiver, int bytesToRead, String password)

    void getFiles(List<TransferObject> fileId, BytesReceiver bytesReceiver, int bytesToRead, String password)
}