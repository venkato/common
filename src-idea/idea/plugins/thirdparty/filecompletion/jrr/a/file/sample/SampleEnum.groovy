package idea.plugins.thirdparty.filecompletion.jrr.a.file.sample

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef;

import java.util.logging.Logger;

@CompileStatic
enum SampleEnum implements ChildFileLazy, ToFileRef2, ZeroOverheadFileRef{
    a1('c:/Users/' as File),
    a2('c:/windows/' as File),
    ;

    public File f;

    SampleEnum(File f) {
        this.f = f
    }

    File getF2(){
        return f;
    }

    @Override
    File2FileRefWithSupportI childL(String child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    File2FileRefWithSupportI childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    File resolveToFile() {
        return f
    }
}
