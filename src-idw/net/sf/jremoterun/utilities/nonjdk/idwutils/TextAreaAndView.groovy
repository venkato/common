package net.sf.jremoterun.utilities.nonjdk.idwutils

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.JPanelBorderLayout
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea
import org.fife.rsta.ui.CollapsibleSectionPanel
import org.fife.rsta.ui.search.FindToolBar
import org.fife.rsta.ui.search.ReplaceToolBar
import org.fife.rsta.ui.search.SearchEvent
import org.fife.rsta.ui.search.SearchListener
import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rtextarea.SearchContext
import org.fife.ui.rtextarea.SearchEngine


import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.KeyStroke
import javax.swing.UIManager
import java.awt.BorderLayout
import java.awt.Event
import java.awt.event.KeyEvent
import java.util.logging.Logger

@CompileStatic
class TextAreaAndView implements SearchListener{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public MyTextArea textArea;

    public JPanel panel = new JPanelBorderLayout()

    public RTextScrollPane scrollPane;

    public View view;

    public CollapsibleSectionPanel csp = new CollapsibleSectionPanel()
    public FindToolBar findToolBar=new FindToolBar(this)
    public ReplaceToolBar replaceToolBar=new ReplaceToolBar(this)

    TextAreaAndView(String title,MyTextArea textArea) {
        this.textArea = textArea
        scrollPane = new RTextScrollPane(textArea, false);
//        panel.add(scrollPane, BorderLayout.CENTER)
        csp.add(scrollPane)
        panel.add(csp,BorderLayout.CENTER)
        view = new View(title, null, panel);
    }

    TextAreaAndView(String title) {
        this(title, new MyTextArea())
    }

    void applyCustomsScrollBarSettings(){
        applyCustomsScrollBarSettingsS(scrollPane)
    }

    static void applyCustomsScrollBarSettingsS(JScrollPane scrollPane){
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    }

    void addSearchPanel(){

        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK)
        csp.addBottomComponent(ks,findToolBar)
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK)
        csp.addBottomComponent(ks,replaceToolBar)
    }

    @Override
    void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType()
        SearchContext context = e.getSearchContext()
        org.fife.ui.rtextarea.SearchResult result
        switch (type){
            case SearchEvent.Type.MARK_ALL :
                result = SearchEngine.markAll(textArea,context)
                break
            case SearchEvent.Type.FIND :
                result = SearchEngine.find(textArea,context)
                if(!result.wasFound()){
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea)
                }
                break
            case SearchEvent.Type.REPLACE :
                result = SearchEngine.replace(textArea,context)
                if(!result.wasFound()){
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea)
                }
                break
            case SearchEvent.Type.REPLACE_ALL :
                result = SearchEngine.replaceAll(textArea,context)
                JOptionPane.showMessageDialog(textArea,result.getCount()+" occurrence replaced")
                break

        }
        String text
        if(result.wasFound()){
            text = "Text found; occurrences marked: "+result.getMarkedCount()
        }else if(type ==  SearchEvent.Type.MARK_ALL){
            if(result.getMarkedCount()>0){
                text = "occurrences marked: "+result.getMarkedCount()
            }else {
                text = ""
            }
        }else {
            text = "Text not found"
        }
        log.info "${text}"

    }

    @Override
    String getSelectedText() {
        return textArea.getSelectedText()
    }
}
