package uk.co.real_logic.artio.library;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import uk.co.real_logic.artio.Reply;
import uk.co.real_logic.artio.session.Session;

import java.util.logging.Logger;

@CompileStatic
public class FixLibraryJrr extends FixLibrary {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FixLibraryJrr(LibraryConfiguration configuration) {
        super(configuration);
    }

    static void connectStatic(FixLibrary instance) throws Exception {
        JrrClassUtils.invokeJavaMethod(instance, "connect");
    }

    public void connect1() throws Exception {
        connectStatic(this);
        final FixLibrary library = this;
        while (!library.isConnected()) {
            library.poll(1);
            Thread.yield();
        }
    }

    @Override
    public Reply<Session> initiate(SessionConfiguration configuration) {
        return super.initiate(configuration);
    }

    @Override
    public int poll(int fragmentLimit) {
        return super.poll(fragmentLimit);
    }
}
