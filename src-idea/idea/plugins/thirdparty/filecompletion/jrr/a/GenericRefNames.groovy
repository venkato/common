package idea.plugins.thirdparty.filecompletion.jrr.a

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum GenericRefNames implements EnumNameProvider{

    this_,
    ;

    String customName;

    GenericRefNames() {
        this.customName = name().replace('_','')
    }
}
