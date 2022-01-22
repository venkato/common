package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;

import groovy.transform.CompileStatic;

@CompileStatic
public class ClassChecker2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClassChecker2 classChecker2 = new ClassChecker2();

    public boolean passOnError = false
    //public static int maxTextSizeToPrintOnErrorS =1_000_000
    public int maxTextSizeToPrintOnError = 1_000_000

    void analize2(String groovyText) {
        try {
            GroovyFileChecker.analize2(groovyText)
        } catch (Throwable e) {
            String textToPrint = groovyText
            if (textToPrint.length() > maxTextSizeToPrintOnError) {
                textToPrint = textToPrint.substring(0, maxTextSizeToPrintOnError)
            }
            log.info "failed parse :\n${textToPrint}"
            if (passOnError) {
                log.info("failed parse ", e)
            } else {
                throw e;
            }
        }
    }

}
