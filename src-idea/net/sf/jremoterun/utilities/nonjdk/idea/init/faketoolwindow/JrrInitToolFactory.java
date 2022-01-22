package net.sf.jremoterun.utilities.nonjdk.idea.init.faketoolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import net.sf.jremoterun.utilities.nonjdk.idea.init.MyInitWrapper2;
import org.jetbrains.annotations.NotNull;

public class JrrInitToolFactory implements ToolWindowFactory {

    static {
        MyInitWrapper2.init2();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {

    }

    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }
}
