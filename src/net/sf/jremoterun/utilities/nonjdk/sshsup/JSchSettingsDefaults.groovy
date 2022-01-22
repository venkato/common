package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.JrrSchSessionLog
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener;

import java.util.logging.Logger;

@CompileStatic
class JSchSettingsDefaults {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int connectionTimeoutOverrideDefault = -1; ;
    public volatile static boolean logSessionSettingsDefault = false

    public static volatile boolean logRawMessageDefault = false
    public static volatile boolean isNeedParsePublicKeyDefault = false

    public volatile static JrrSchSessionLog jrrSchSessionLogDefault = new JrrSchSessionLog();

    public static boolean checkIdentityOnlyOneS = true
    public static boolean dumpResolvedPropsS = true
    public static boolean dumpSessionParamsAfterAuthS = false

    public static volatile NewValueListener<JrrJschSession> connectFinishedNotifierS
}
