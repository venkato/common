package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class StringWriter implements CustomWriter<String> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, String str) {
        return writeString(str)
    }


    boolean isUseMultiLine(String s){
        if (s.contains('\n')) {
            return true
        }
        return false
    }

    String writeString(String s) {
        boolean useMultiline = isUseMultiLine(s);
        boolean useSingleQuote = true;
        if (s.contains('"') && s.contains("'")) {
            s = escapeStringComplex(s)
            useSingleQuote = false;
        }else{
            if (s.contains('"')) {
                useSingleQuote = true
            }
            if (s.contains("'")) {
                useSingleQuote = false
            }
            s = escapeStringSimple(s)
        }
        String prefix = getStringPrefix(useSingleQuote, useMultiline)
        return "${prefix}${s}${prefix}"
    }

    String escapeStringComplex(String s) {
        return org.apache.commons.text.StringEscapeUtils.escapeJava(s)
    }


    String escapeStringSimple(String s){
        return s.replace("\\", "\\\\")
    }

    String getStringPrefix(boolean useSingleQuote, boolean useMultiline) {
        if (useSingleQuote) {
            if (useMultiline) {
                return "'''"
            }
            return "'"
        }
        if (useMultiline) {
            return '"""'
        }
        return '"'
    }


    @Override
    Class<String> getDataClass() {
        return String
    }
}
