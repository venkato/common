package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.json

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.additioalmethodwriters.CreatedAtMethodWriter
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.CreatedAtInfoI
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer7Sub
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CustomWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.StringWriter
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class StoreComplexJson<T> extends Writer3Json {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Writer3Import writer3Importer = this

    public ObjectWriterI objectWriter;

    public OutputFormat outputFormat
    public String stringWrapperChar = '"'



    @Deprecated
    public Writer3Json writer7Sub = this

    StoreComplexJson(OutputFormat outputFormat) {
        this.outputFormat = outputFormat
        objectWriter=createObjectWriter()
    }

    ObjectWriter createObjectWriter(){
        ObjectWriter objectWriter1 = new ObjectWriter()
        objectWriter1.nullObject  = stringWrapperChar+'null'+stringWrapperChar
        StringWriter stringWriter2 = new StringWriter()
        stringWriter2.stringPrefixOverride  = stringWrapperChar
        objectWriter1.stringWriterCurrent = stringWriter2
        return objectWriter1
    }


    @Override
    void writeAdditionalMethods() {
        super.writeAdditionalMethods()
    }

    void onBodyCreated() {

    }

    void onFailedWriteEl(Object el, int countInList, Throwable ex) {
        throw ex
    }

    abstract String saveComplexObject(T obj) throws Exception


}
