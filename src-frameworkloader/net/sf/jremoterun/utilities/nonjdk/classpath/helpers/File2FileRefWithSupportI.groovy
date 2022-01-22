package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
interface File2FileRefWithSupportI extends ToFileRef2, ChildFileLazy {

    @Override
    File resolveToFile();

    @Override
    FileChildLazyRef childL(String child)

    @Override
    FileChildLazyRef childP(ChildPattern child)

}
