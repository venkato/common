package net.sf.jremoterun.utilities.nonjdk.chromebrowser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

// https://peter.sh/experiments/chromium-command-line-switches/
@CompileStatic
enum ChromeCmdOptions implements EnumNameProvider {
    disable_extensions,

    ;

    String customName;

    ChromeCmdOptions() {
        this.customName = '--' + name().replace('_', '-')
    }

}
