package net.sf.jremoterun.utilities.nonjdk.jrrcoredebug

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrClassUtils2
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.JrrRunnerPhase
import net.sf.jremoterun.utilities.groovystarter.seqpattern.FinalPhase
import net.sf.jremoterun.utilities.groovystarter.seqpattern.JrrRunnerPhaseI
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2
import net.sf.jremoterun.utilities.groovystarter.st.JrrRunnerPhase2;

import java.util.logging.Logger;

@CompileStatic
class PrintPhases implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void addDebug(JrrRunnerPhaseI phase){
        if( GroovyMethodRunnerParams.gmrpn.seqPatternRunnerGmrp.isPhasePassed(phase)){

        }else{
            Runnable befoer1 = {
                log.info "before ${phase} ${GroovyMethodRunnerParams.gmrpn.args} "
                if(phase== JrrRunnerPhase2.runTargetMethod){
                    printMainMethods()
                }
            }
            Runnable after1 = {
                log.info "after ${phase} ${GroovyMethodRunnerParams.gmrpn.args}"
            }
            GroovyMethodRunnerParams.gmrpn.seqPatternRunnerGmrp.addL(phase,true,befoer1)
            GroovyMethodRunnerParams.gmrpn.seqPatternRunnerGmrp.addL(phase,false,after1)
        }

        if(phase== FinalPhase.finalPhase){

        }else {
            JrrRunnerPhaseI nextPhase = phase.nextPhase()
            assert nextPhase!=null
            addDebug (nextPhase)
        }
    }

    @Override
    void run() {
        addDebug (JrrRunnerPhase.begin)
//        new PrintPhases().addDebug (JrrRunnerPhase2.afterCoreLibAdded)

    }


    void printMainMethods(){
        log.info "main class = ${GroovyMethodRunnerParams2.gmrp2.mainClassFound}"
        if(GroovyMethodRunnerParams2.gmrp2.mainClassFound!=null){
            File location1 = UrlCLassLoaderUtils.getClassLocation(GroovyMethodRunnerParams2.gmrp2.mainClassFound)
            log.info "location1 = ${location1}"
        }

    }
}
