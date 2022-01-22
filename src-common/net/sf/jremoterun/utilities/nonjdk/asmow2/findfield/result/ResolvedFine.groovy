package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.FieldFoundedEls
import net.sf.jremoterun.utilities.nonjdk.store.WriteStateMySelf
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI

import java.util.logging.Logger

@CompileStatic
class ResolvedFine implements WriteStateMySelf{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldFoundedEls fieldFoundedEls
    public Object parentLocation

    ResolvedFine(FieldFoundedEls fieldFoundedEls, Object parentLocation) {
        this.fieldFoundedEls = fieldFoundedEls
        this.parentLocation = parentLocation
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter) {
        return objectWriter.writeConstructorWithArgs(writer3, getClass(),  [fieldFoundedEls, parentLocation]);
    }
}
