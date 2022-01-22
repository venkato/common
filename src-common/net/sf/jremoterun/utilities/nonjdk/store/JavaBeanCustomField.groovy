package net.sf.jremoterun.utilities.nonjdk.store

import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI

import java.lang.reflect.Field


interface JavaBeanCustomField {


    String customizeField(Writer3Import writer3Import, ObjectWriterI objectWriterI, Field field, Object fieldValue)
}