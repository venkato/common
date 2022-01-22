package net.sf.jremoterun.utilities.nonjdk.docker

import com.google.cloud.tools.jib.api.JibEvent
import com.google.cloud.tools.jib.api.LogEvent
import com.google.cloud.tools.jib.event.events.ProgressEvent
import com.google.cloud.tools.jib.event.events.TimerEvent
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.function.Consumer;
import java.util.logging.Logger;

@CompileStatic
class JibEventLogger implements Consumer<JibEvent> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DockerHelper dockerHelper;

    @Override
    void accept(JibEvent jibEvent) {
        if(jibEvent instanceof ProgressEvent) {
            ProgressEvent p = jibEvent
            log.info "p ${p.allocation.description} ${p.units}"
        }else if(jibEvent instanceof LogEvent){
            LogEvent j = jibEvent
            log.info "e ${j.level} ${j.message}"
        }else if(jibEvent instanceof TimerEvent){
            TimerEvent timerEvent = jibEvent
            log.info "t ${timerEvent.description} ${timerEvent.state}"
        }else {
            log.info "${jibEvent.getClass().getName()} ${jibEvent}"
        }
        if(dockerHelper!=null){
            //dockerHelper.checkForStop()
        }
    }
}
