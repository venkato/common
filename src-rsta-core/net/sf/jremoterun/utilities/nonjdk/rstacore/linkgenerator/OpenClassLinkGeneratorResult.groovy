package net.sf.jremoterun.utilities.nonjdk.rstacore.linkgenerator

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.OsInegrationClientI
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult

import javax.swing.event.HyperlinkEvent
import java.util.logging.Logger

@CompileStatic
class OpenClassLinkGeneratorResult implements LinkGeneratorResult{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int sourceOffset
    public String className;
    public OsInegrationClientI osInegrationClientI;

    OpenClassLinkGeneratorResult(int sourceOffset, String className, OsInegrationClientI osInegrationClientI) {
        this.sourceOffset = sourceOffset
        this.className = className
        this.osInegrationClientI = osInegrationClientI
        if(className==null){
            throw new NullPointerException("class is null")
        }
        if(osInegrationClientI==null){
            throw new NullPointerException("client is null")
        }
    }

    @Override
    HyperlinkEvent execute() {
        try {
            osInegrationClientI.openClass(className)
        }catch (Throwable e){
            JrrUtilitiesShowE.showException("failed open class ${className}",e)
            throw e
        }
        return null
    }

    @Override
    int getSourceOffset() {
        return sourceOffset
    }
}
