package net.sf.jremoterun.utilities.nonjdk.sonar

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.sonar.api.utils.log.LoggerLevel
import org.sonar.api.utils.log.Loggers;

import java.util.logging.Logger;

@CompileStatic
class SonarLoggerFactory extends Loggers{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    protected org.sonar.api.utils.log.Logger newInstance(String name) {
        return null
    }

    @Override
    protected LoggerLevel getLevel() {
        return null
    }

    @Override
    protected void setLevel(LoggerLevel level) {

    }
}
