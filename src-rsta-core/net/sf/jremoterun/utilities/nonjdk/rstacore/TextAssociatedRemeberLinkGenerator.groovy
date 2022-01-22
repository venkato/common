package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.OpenUrlHandler
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

import java.util.logging.Logger

@CompileStatic
class TextAssociatedRemeberLinkGenerator extends TextAssociatedLinkGenerator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HashSet<String> links = new HashSet<>()
    public OpenUrlHandler openUrlHandler;
    public URL urlPrefix;

    TextAssociatedRemeberLinkGenerator(OpenUrlHandler openUrlHandler) {
        this.openUrlHandler = openUrlHandler
    }

    @Override
    LinkGeneratorResult isLinkAtOffset2(RSyntaxTextArea textArea3, int offs, String text) {
        URL u = findUrl(text)
        if (u == null) {
            return null
        }
        return new OpenInBrowserLinkGeneratorResult2(offs, u.toString(), openUrlHandler);
    }

    URL findUrl(String text) {
        boolean contains2 = links.contains(text)

        if (contains2) {
            return buildUrl(text)
        }
        return null
    }

    URL buildUrl(String text) {
        return new URL(urlPrefix.toString() + text);
    }


}
