package net.sf.jremoterun.utilities.nonjdk.tcpmon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea
import org.fife.ui.rtextarea.RTextArea

import javax.swing.JMenuItem
import javax.swing.JPopupMenu;
import java.util.logging.Logger;

@CompileStatic
class TcpMonTextArea extends MyTextArea{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Connection connection;

    TcpMonTextArea(Connection connection,int i, int j) {
        super(i, j)
        this.connection=connection
    }


    @Override
    protected JPopupMenu createPopupMenu() {

        JPopupMenu jPopupMenu= super.createPopupMenu()
        JMenuItem jmenuitem = new JMenuItem('show translated custom');
        jmenuitem.setEnabled(connection.listener.tcpMonSettings.customDataTralator!=null)
        jPopupMenu.add(jmenuitem)
        jmenuitem.addActionListener {onCustomTra()}
        return jPopupMenu;
    }

    void onCustomTra(){
        String selected1 = getSelectedText()
        if(selected1==null){
            JrrUtilitiesShowE.showException('Select selected1 is null',new Exception('Select selected1 is null'))
        }else{
            if(selected1.length()==0){
                JrrUtilitiesShowE.showException('Select selected1 is empty',new Exception('Select selected1 is empty'))
            }else{
                try {
                    CustomDataTralator tralator1 = connection.listener.tcpMonSettings.customDataTralator
                    String translateed = tralator1.translate(this, selected1)
                    connection.listener.textAreaTranslatrorResult.setText(translateed)
                    IdwUtils.setVisible(connection.listener.translatrorResult.view)
                }catch (Throwable e){
                    log.info3("failed traslate ${selected1}",e)
                    JrrUtilitiesShowE.showException('failed traslate',e)
                }
            }
        }
    }

    String getCharsetEncoding(){
        if(this.is(connection.inputText)){
            return connection.requestEncoding.getText()
        }
        if(this.is(connection.outputText)){
            return connection.responseEncoding.getText()
        }
        throw new IllegalAccessException()
    }

}
