package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
interface File2FileRefWithSupportI extends ToFileRef2, ChildFileLazy {

    @Override
    File resolveToFile();

    @Override
    File2FileRefWithSupportI childL(String child)

    @Override
    File2FileRefWithSupportI childP(ChildPattern child)

}
