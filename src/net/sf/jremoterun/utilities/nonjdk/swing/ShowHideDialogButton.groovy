package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JButton
import javax.swing.JFrame
import java.awt.Window
import java.awt.event.ActionListener;
import java.util.logging.Logger;

@CompileStatic
class ShowHideDialogButton extends JButton{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String mainName;

    public boolean statedHidden = true
    public String collapseSign = ' -'
    public String expandSign = ' +'
    public Window frame

    ShowHideDialogButton( String mainName, Window frame) {
        super(mainName);
        statedHidden = true
        this.mainName = mainName
        this.frame = frame
        addActionListener {
            onAction()
        }
        setText(mainName+expandSign)
    }

    void onAction(){
        if(statedHidden){
            onAction2(true)
            setText(mainName+collapseSign)
            statedHidden= false
        }else{
            onAction2(false)
            setText(mainName+expandSign)
            statedHidden= true
        }
    }

    void onAction2(boolean setVisible){
        frame.setVisible(setVisible)
    }

}
