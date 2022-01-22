package net.sf.jremoterun.utilities.nonjdk.rstacore;

import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

import javax.swing.JPopupMenu;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


// was RSyntaxTextAreaCodeAssist
@CompileStatic
abstract class RSyntaxTextAreaCodeAssistWithCustMenu extends RSyntaxTextArea{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    protected void appendFoldingMenu(JPopupMenu popupMenu) {
        super.appendFoldingMenu(popupMenu);
        appendFoldingMenu2(popupMenu);
    };



    public void setCursorToStart() {
        select(0, 0);
    }

    abstract void appendFoldingMenu2(JPopupMenu popupMenu);

}
