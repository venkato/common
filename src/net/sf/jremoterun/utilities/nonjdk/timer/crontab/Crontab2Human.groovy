package net.sf.jremoterun.utilities.nonjdk.timer.crontab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.CronExpression

import java.util.logging.Logger;

@CompileStatic
class Crontab2Human {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public List<String> commentBefore = []
    public List<CrontabLine> cronLines = []
    public int currentLineNum = -1
    public Date fromDate

    Crontab2Human(Date fromDate) {
        this.fromDate = fromDate
    }

    void parseExpression(List<String> lines) {
        lines.each {
            try {
                currentLineNum++
                eachLine2 it.trim()
            } catch (Exception e) {
                log.info "failed parse ${it} : ${e}"
                throw e
            }
        }
    }


    void eachLine2(String linene){
        if (linene.startsWith('#')) {
            commentBefore.add(linene)
        } else if (linene.isEmpty()) {
            commentBefore.clear()
        } else {
            parseOneCronExpression(linene)
        }
    }

    String fixExpressoimn(List<String> list33){
        if (list33[2] == '*' && list33[4] == '*') {
            list33[2] = '?'
        }
        String crononlyu = '59 ' + list33.join(' ')
        return crononlyu
    }

    void parseOneCronExpression(String linene){
        List<String> tokenize1 = linene.tokenize(' ')
        assert tokenize1.size() > 5
        List<String> list33 = tokenize1.subList(0, 5)
        //assert list33.size() == 5
        String crononlyu =fixExpressoimn(list33)
        CronExpression cronExpression = new CronExpression(crononlyu);
        Date after2 = cronExpression.getNextValidTimeAfter(fromDate)
        createCronEl(after2,linene)
    }

    void createCronEl(Date after2,String linene ){
        List<String> msg = []
        msg.addAll(commentBefore)
        msg.addAll(linene)
        cronLines.add new CrontabLine(after2,msg)
        commentBefore.clear()

    }
}
