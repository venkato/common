package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum AsmOw implements MavenIdContains, EnumNameProvider  {
    asm_util,
    asm_tree,
    //asm_test,
    asm_commons,
    asm_analysis,
    asm,
//    asm_deprecated,
    ;


    String  customName;

    MavenId m;

    AsmOw() {
        customName = name().replace('_','-')
        // 9.7 throws verify error. Try next version
        m = new MavenId('org.ow2.asm', customName, '9.9');
    }

    public static List<AsmOw> all = values().toList()


}
