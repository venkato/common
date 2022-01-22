package net.sf.jremoterun.utilities.nonjdk.rstacore.linkgenerator

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.OsInegrationClientI
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult

import javax.swing.event.HyperlinkEvent
import java.util.logging.Logger

@CompileStatic
class OpenFileInIdeLinkGeneratorResult implements LinkGeneratorResult{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int sourceOffset
    public File file1;
    public OsInegrationClientI osInegrationClientI;

    OpenFileInIdeLinkGeneratorResult(int sourceOffset, File file1, OsInegrationClientI osInegrationClientI) {
        this.sourceOffset = sourceOffset
        this.file1 = file1
        this.osInegrationClientI = osInegrationClientI
        if(file1==null){
            throw new NullPointerException("file is null")
        }
        if(osInegrationClientI==null){
            throw new NullPointerException("client is null")
        }
    }

    @Override
    HyperlinkEvent execute() {
        try {
            JrrUtilitiesFile.checkFileExist(file1)
            osInegrationClientI.openFile(file1,file1.getName())
        }catch (Throwable e){
            JrrUtilitiesShowE.showException("failed open file ${file1}",e)
            throw e
        }
        return null
    }

    @Override
    int getSourceOffset() {
        return sourceOffset
    }
}
