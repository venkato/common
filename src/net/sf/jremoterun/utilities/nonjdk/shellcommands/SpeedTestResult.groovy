package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import groovy.transform.ToString;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@ToString
@CompileStatic
class SpeedTestResult {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public String host
    public String id
    public String externalIp
    public String timestamp
    public long downloadMbitPerSec

    public long uploadMbitPerSec;
    public BigDecimal latencyMs
    public BigDecimal jitter

}
