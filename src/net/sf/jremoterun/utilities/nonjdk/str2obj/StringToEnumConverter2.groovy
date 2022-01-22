package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToEnumConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter

import java.lang.reflect.Type;
import java.util.logging.Logger;

@CompileStatic
class StringToEnumConverter2 extends StringToEnumConverter{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public boolean matchCase = false
    public boolean startWith = true
    public boolean contains = false
    public boolean consoleSuggestion = true


    @Override
    Enum convertToEnum(String str, Class toType, Type genericArg) {
        StringToEnumConverterEnhanced enhanced = new StringToEnumConverterEnhanced(toType)
        enhanced.matchCase = matchCase
        enhanced.startWith = startWith
        enhanced.contains = contains
        enhanced.consoleSuggestion = consoleSuggestion

        return enhanced.convert(str,genericArg)
    }

    void register(){
        StringToObjectConverter.defaultConverter.stringToEnumConverter = this
    }
}
