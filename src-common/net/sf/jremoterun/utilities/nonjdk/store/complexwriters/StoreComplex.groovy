package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.additioalmethodwriters.CreatedAtMethodWriter
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.CreatedAtInfoI
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer7Sub
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class StoreComplex<T> extends Writer7Sub {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Writer3Import writer3Importer = this

    public ObjectWriterI objectWriter = new ObjectWriter();

    public List<Class> implementedInterfaces = []

    //public boolean useCreatedAddAdditionalInfo = true;
    public CreatedAtMethodWriter createdAtMethodWriter = new CreatedAtMethodWriter(this)

    @Deprecated
    public Writer7Sub writer7Sub = this

    StoreComplex(Class configClass) {
        super(configClass)
    }

    @Override
    void writeAdditionalMethods() {
        super.writeAdditionalMethods()
        if (createdAtMethodWriter != null) {
            if (implementedInterfaces.contains(CreatedAtInfoI)) {
                log.info "already contains ${CreatedAtInfoI.getName()}"
            } else {
                createdAtMethodWriter.writeAdditionalMethods()
            }
        }

    }

    void onBodyCreated() {

    }

    void onFailedWriteEl(Object el, int countInList, Throwable ex) {
        throw ex
    }

    abstract String saveComplexObject(T obj) throws Exception


    void addParentMethodCall(ObjectWriterI objectWriter, MethodClosure methodName, List args) {
        addParentMethodCall(objectWriter, methodName.getMethod(), args)
    }

    void addParentMethodCall(ObjectWriterI objectWriter, String methodName, List args) {
        Writer3 r3 = this
        List<String> args4 = args.collect { objectWriter.writeObject(r3, it) }
        String r = "${methodName}( ${args4.join(',')} ) ${semiColumn}"
        body.add new LineInfo(Brakets.netural, r)
    }


    @Override
    String addInterfaces() {
        if (implementedInterfaces.size() == 0) {
            return ''
        }
        return implementedInterfaces.collect { addImportWithName(it) }.join(', ') + ' '
    }

}
