package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

/**
 * https://github.com/irockel/tda
 */
@CompileStatic
class ThreadDumpParser {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String end = 'class space'
    public String begin = 'Full thread dump'
    public File file1

    public String lineBefore;
    public boolean collecting = false
    public List<String> lines;
    public String date1;
    public int maxLinesInThreadDump= 10_000;

    ThreadDumpParser(File file1) {
        this.file1 = file1
        assert file1.exists()
        assert file1.isFile()
    }

    void parseFile(){
        file1.eachLine {
            handleLine(it)
        }
    }

    void handleLine(String line1){
        if(line1.contains(begin)){
            collecting = true
            lines = []
            date1 = lineBefore
            lines.add(lineBefore) // adding date
        }
        if(collecting){
            lines.add(line1)
            int s= lines.size()
            assert s<maxLinesInThreadDump
        }
        if(line1.contains(end)){
            log.info "${date1} ${lines.size()}"
            collecting=false
            onThreadDumpFound(lines,date1)
        }
        lineBefore=line1
    }

    void onThreadDumpFound(List<String> lines,String date2){
        String p = date2.replace(':','_')
        new File(file1.getParentFile(),"${p}.txt").text = lines.join('\n')
    }

}
