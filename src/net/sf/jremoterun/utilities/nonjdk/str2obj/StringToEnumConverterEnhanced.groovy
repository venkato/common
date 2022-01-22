package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToEnumConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter

import java.lang.reflect.Type;
import java.util.logging.Logger;

@CompileStatic
class StringToEnumConverterEnhanced implements StringToObjectConverterI2<Enum> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class cl;
    public boolean matchCase = false
    public boolean startWith = false
    public boolean contains = false
    public boolean consoleSuggestion = false

    StringToEnumConverterEnhanced(Class cl) {
        this.cl = cl
    }


    void registerConverter() {
        StringToObjectConverter.defaultConverter.customConverters.put(cl, this)
    }


    List<Enum> getEnumList(Class cl) {
        return getEnumListS(cl)
    }

    static List<Enum> getEnumListS(Class cl) {
        Object[] enumList = JrrClassUtils.getEnumValues(cl)
        return (List) enumList.toList()
    }

    @Override
    Enum convert(String str, Type genericArg) {
        assert cl.isEnum()
        List<Enum> list1 = getEnumList(cl)
        while (true) {
            List<Enum> matchedd = list1.findAll { isMatched(it, str) }
            int size1 = matchedd.size()
            if (size1 == 0) {
                str = consoleSuggestion("no enum found ${str} in ${cl.getName()}, available : ${enumsToStr(list1)}")
            } else if (size1 > 1) {
                str = consoleSuggestion("matched ${size1} enums ${enumsToStr(matchedd)} in ${cl.getName()}")
            } else {
                return matchedd[0]
            }
        }
    }

    String enumsToStr(List<Enum> matchedd) {
        return matchedd.toString()
    }


    String consoleSuggestion(String m) {
        if (!consoleSuggestion) {
            throw new IllegalArgumentException(m)
        }
        log.info(m)
        String line1 = System.console().readLine()
        return line1
    }


    boolean isMatched(Enum e, String argFromCmd) {
        if (argFromCmd == null) {
            return false
        }
        String fullName = e.name()
        if (!matchCase) {
            fullName = fullName.toLowerCase()
            argFromCmd = argFromCmd.toLowerCase()
        }

        if (fullName == argFromCmd) {
            return true
        }
        if (contains) {
            return fullName.contains(argFromCmd)
        }
        if (startWith) {
            return fullName.startsWith(argFromCmd)
        }
        return false
    }
}
