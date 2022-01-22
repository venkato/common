package net.sf.jremoterun.utilities.nonjdk.idea.init.faketoolwindow;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import net.sf.jremoterun.utilities.nonjdk.idea.init.MyInitWrapper2;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * add :
 <extensions defaultExtensionNs="com.intellij">
 <applicationConfigurable groupId="tools" instance="net.sf.jremoterun.utilities.nonjdk.idea.init.faketoolwindow.JrrInitBaseConfigurable" />
 <toolWindow id="JrrInitFakeComp" icon="/images/psiToolWindow.png" anchor="right" factoryClass="net.sf.jremoterun.utilities.nonjdk.idea.init.faketoolwindow.JrrInitToolFactory" />
 </extensions>
 */
public class JrrInitBaseConfigurable extends BaseConfigurable {

    static {
        MyInitWrapper2.init2();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "JrrInitBaseConfigurable";
    }

    public static JrrInitBaseConfigurable jrrInitBaseConfigurable;

    public JrrInitBaseConfigurable() {
        jrrInitBaseConfigurable = this;
    }

    public JPanel panel=new JPanel();

    @Override
    public @Nullable JComponent createComponent() {
        return panel;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
