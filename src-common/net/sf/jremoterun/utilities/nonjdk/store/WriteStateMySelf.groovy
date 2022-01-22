package net.sf.jremoterun.utilities.nonjdk.store

import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI


interface WriteStateMySelf {

    String save(Writer3Import writer3, ObjectWriterI objectWriter);

}