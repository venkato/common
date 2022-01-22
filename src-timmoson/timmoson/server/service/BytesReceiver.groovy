package timmoson.server.service

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import timmoson.client.ProxyCall
import timmoson.client.StayLocalObject
import timmoson.server.NoCreateNewThread;

import java.util.logging.Logger;

@CompileStatic
interface BytesReceiver extends StayLocalObject, NoCreateNewThread {


    void sendBytes(byte[] bs);

    void noChanges();

    void allSentFileIsEmpty();
    void allSent();

    void processingNextFile(int i)

    void wrongPassword()

    void notifyFiles(List<String> files)
}
