package net.sf.jremoterun.utilities.nonjdk.timer.crontab

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupTransferHandler
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.DropFileAware

import javax.swing.DropMode
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class CrontabHumanViewer implements DropFileAware{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public TextAreaAndView textAreaAndView = new TextAreaAndView('CronViewer')

    public List<String> originalCrontab

    public SimpleDateFormat sdfInit = new SimpleDateFormat('yyyy.MM.dd HH:mm')
    public SimpleDateFormat sdfAfterParsing = new SimpleDateFormat('dd HH:mm')
    public JTextField dateField = new JTextField('')
    public JTextField fileTextField = new JTextField('',10)
    public JTextField maxErrorCountTextField = new JTextField('5',2)
    public JButton nowButton = new JButton('Now')
    public JButton reEvalButton = new JButton('Re-eval')
    public JPanel topPanel =new JPanel(new FlowLayout())
    public Crontab2Human crontab2Human

    CrontabHumanViewer(File f) {
        this(f.readLines())
        fileTextField.setText(f.getAbsolutePathUnix())

    }

    CrontabHumanViewer(List<String> originalCrontab) {
        this.originalCrontab = originalCrontab
        init()
    }

    void init(){
        resetDate()
        topPanel.add(dateField)
        topPanel.add(reEvalButton)
        topPanel.add(nowButton)
        topPanel.add(new JLabel('FileLocation'))
        topPanel.add(fileTextField)
        topPanel.add(maxErrorCountTextField)
        maxErrorCountTextField.setToolTipText('Max error count')

        fileTextField.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                if(e.getKeyCode() ==KeyEvent.VK_ENTER ){
                    originalCrontab = new File(fileTextField.getText().trim()).readLines()
                }

            }
        })
        fileTextField.setDropMode(DropMode.INSERT)
        fileTextField.setTransferHandler(new BackupTransferHandler(this));
        textAreaAndView.addSearchPanel()
        textAreaAndView.textArea.setEditable(false)
        textAreaAndView.panel.add(topPanel, BorderLayout.NORTH)
        nowButton.addActionListener {resetDate()}
        reEvalButton.addActionListener {reevaluate()}
        dateField.addKeyListener(new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                if(e.getKeyCode() ==KeyEvent.VK_ENTER ){
                    reevaluate()
                }

            }
        })

    }

    @Override
    void onFileDropped(String filename) {
        filename = filename.trim()
        fileTextField.setText(filename)
        originalCrontab = new File(filename).readLines()
    }

    void resetDate(){
        dateField.setText(' '+sdfInit.format(new Date())+' ')
    }

    Date getInitDate(){
        return sdfInit.parse(dateField.getText().trim())
    }

    void reevaluate() {
        try {
            List<String> lines= reevaluateImpl()
            textAreaAndView.textArea.setText(lines.join('\n'))
            textAreaAndView.textArea.select(1,2)
        }catch (Throwable e){
            log.info('failed parse crontab',e)
            JrrUtilitiesShowE.showException('failed show cron',e)
        }
    }

    void createCrontabHuman(){
        Date initDate = getInitDate()
        crontab2Human = new Crontab2Human(initDate)
    }

    List<String> reevaluateImpl() {
        createCrontabHuman()
        crontab2Human.maxErrorLines = maxErrorCountTextField.getText( ) as int
        crontab2Human.parseExpression(originalCrontab)
        List<String> all = []
        if(crontab2Human.errorLines.size()>0){
            all.add "Error lines ${crontab2Human.errorLines.size()} :".toString()
        }
        all.addAll crontab2Human.errorLines.collect{buildErrorLine(it)}
        all.add('')
        crontab2Human.cronLines.sort().each {
            all.addAll(formatCronLine(it))
        }
        if(crontab2Human.otherLines.size()>0){
            all.add('')
            all.addAll(crontab2Human.otherLines)
        }
        return all
    }

    String buildErrorLine(CronErrorLine cronErrorLine){
        return "${cronErrorLine.line}  ${cronErrorLine.exception}"
    }

    List<String> formatCronLine(CrontabLine cronLine) {
        List<String> els = []
        els.add('# ' + sdfAfterParsing.format(cronLine.nextRun))
        els.addAll cronLine.cronExp
        els.add('')
        return els
    }

}