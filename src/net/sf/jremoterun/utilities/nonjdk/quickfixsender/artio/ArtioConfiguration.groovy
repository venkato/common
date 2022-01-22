package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ArtioConfiguration {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public int basePort
    public int baseOffset
    public File baseDir


    public File AERON_DIR_NAME = new File(baseDir, "client-aeron${baseOffset}")
    public File ARCHIVE_DIR_NAME = new File(baseDir, "client-aeron-archive${baseOffset}")
    public String CONTROL_REQUEST_CHANNEL = "aeron:udp?endpoint=localhost:${basePort + 0 + baseOffset}";
    public String CONTROL_RESPONSE_CHANNEL = "aeron:udp?endpoint=localhost:${basePort + 1 + baseOffset}";
    public String RECORDING_EVENTS_CHANNEL = "aeron:udp?control-mode=dynamic|control=localhost:${basePort + 2 + baseOffset}";
    public String aeronChannel = "aeron:udp?endpoint=localhost:${basePort + 3 + baseOffset}";
    public String REPLICATION_CHANNEL = "aeron:udp?endpoint=localhost:0";
    public FixDictionaryJrr fixDictionary = new FixDictionaryJrr()

    public String beginString = "FIX.4.4";
    public String ACCEPTOR_COMP_ID = "acceptor";
    public String INITIATOR_COMP_ID = "initiator";
    public String host = "localhost";
    public File logDir = new File(baseDir, "client-logs${baseOffset}");
    public int port = 9991;
    public long sleepTime = 100;
    public int poolTime = 100;
    public int onLogonSentSeqNumber = -1;


    public int attempts = 2
    public String monitringFile = uk.co.real_logic.artio.CommonConfiguration.optimalTmpDirName() + File.separator + "fix-client${baseOffset}" + File.separator + "engineCounters"
    public long maxCountOnTimeout = 1000_000

    ArtioConfiguration(File baseDir) {
        this(baseDir, 15500, 0)

    }

    ArtioConfiguration(File baseDir, int basePort, int baseOffset) {
        assert baseDir.exists()
        this.baseDir = baseDir
        this.basePort = basePort
        this.baseOffset = baseOffset
    }
}
