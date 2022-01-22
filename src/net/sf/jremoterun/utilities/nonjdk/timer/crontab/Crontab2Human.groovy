package net.sf.jremoterun.utilities.nonjdk.timer.crontab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.timer.CronExpression

import java.text.ParseException
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

    boolean isGoodLine(String linene){
        List<String> tokenize1 = linene.tokenize(' ')
        if(tokenize1.size()<5){
            return false
        }
        if(tokenize1[0].contains('=')){
            return false
        }
        if(linene.startsWith('@reboot')){
            return false
        }
        return true
    }


    void eachLine2(String linene){
        if (linene.startsWith('#')) {
            commentBefore.add(linene)
        } else if (linene.isEmpty()) {
            unusedComments.addAll commentBefore
            commentBefore.clear()
        } else {
            if(isGoodLine(linene)) {
                try {
                    parseOneCronExpression(linene)
                }catch (Exception e){
                    onException(linene,e)
                    commentBefore.clear()
                }
            }else {
                otherLines.addAll commentBefore
                otherLines.add linene
                commentBefore.clear()
            }
        }
    }

    public int maxErrorLines = 5
    public List<String> unusedComments= []
    public List<String> otherLines= []
    public List<CronErrorLine> errorLines= []

    void onException(String line, Exception e){
        errorLines.add(new CronErrorLine(line,e))
        if(e instanceof ParseException){

        }else{
            throw e
        }
        if(errorLines.size()>maxErrorLines){
            throw errorLines[0].exception;
        }
    }

    String fixDayOfWeek2(String dayOfWeek){
        char[] charArray = dayOfWeek.toCharArray()
        for (i in 0..<charArray.length) {
            char ccc = charArray[i]
            if(ccc.isDigit()){
                int dayOfWeekI = ccc as int
                dayOfWeekI++
                charArray[i] = dayOfWeekI as char
            }

        }
        return new String(charArray)
    }

    void fixDayOfWeekAndDay(List<String> list33){
        if(list33[2] =='*' ||list33[4] =='*' ){
            boolean changed = false
            if(list33[4] =='*'){
                if(!changed){
                    list33[4] = '?'
                    changed = true
                }
            }

            if(list33[2] =='*'){
                if(!changed){
                    list33[2] = '?'
                    changed = true
                }
            }
        }
    }

    void fixDayOfWeek(List<String> list33){
        list33[4] = fixDayOfWeek2( list33[4])
    }

    String fixExpressoimn(List<String> list33){
        fixDayOfWeek(list33)
        fixDayOfWeekAndDay(list33)
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
