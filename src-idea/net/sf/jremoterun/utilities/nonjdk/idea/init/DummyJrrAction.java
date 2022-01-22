package net.sf.jremoterun.utilities.nonjdk.idea.init;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@CompileStatic
class DummyJrrAction extends AnAction {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean inited = MyInitWrapper2.init2();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        log.info("dummy action");
    }

}
