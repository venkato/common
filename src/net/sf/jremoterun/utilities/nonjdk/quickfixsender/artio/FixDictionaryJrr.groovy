package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import uk.co.real_logic.artio.FixDictionaryImpl
import uk.co.real_logic.artio.dictionary.FixDictionary;

import java.util.logging.Logger;

@CompileStatic
class FixDictionaryJrr extends FixDictionaryImpl implements FixDictionary{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String beginString1 = "FIX.4.4"

    @Override
    String beginString() {
        return beginString1
    }
}
