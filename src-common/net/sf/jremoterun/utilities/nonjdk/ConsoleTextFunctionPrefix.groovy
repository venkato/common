package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ConsoleTextFunctionPrefix {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String functionWord = 'TextFunction : ';

    public static String ignoreWord = 'ignore ';
    public static String ignoreWord2 = 'JRR_CONSTANT_sound=TextFunction : p1';

    public static List<String> ignoreWords = [ignoreWord, 'echo ',ignoreWord2];

}
