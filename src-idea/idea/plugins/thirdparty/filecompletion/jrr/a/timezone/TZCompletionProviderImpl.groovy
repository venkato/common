package idea.plugins.thirdparty.filecompletion.jrr.a.timezone

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.CompletionProviderCommon
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import javax.swing.*

@CompileStatic
public class TZCompletionProviderImpl extends CompletionProviderCommon {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Icon timeZoneIcon = IconLoader.getIcon('/icons/time_zone.png', OSIntegrationIdea);

    static String[] timeZones = TimeZone.availableIDs;

    static BigDecimal oneHour =   new BigDecimal(DurationConstants.oneHour.timeInMsLong)
//    static BigDecimal oneHour =   3600000.0g

    static Map<String, String> displayText = new HashMap(TimeZone.availableIDs.collectEntries {
        TimeZone zone = TimeZone.getTimeZone(it)
        BigDecimal bd = new BigDecimal(zone.rawOffset) / oneHour;
        String displayStr = it + ' ' + (bd.signum() == 1 ? '+' : '') + bd.toPlainString()
        return [(it): (displayStr)]
    }
    )

    @Override
    protected void addCompletions(
             CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
        PsiElement psiElement = parameters.position;
        if (!(psiElement instanceof LeafPsiElement)) {
            return;
        }
        assert timeZoneIcon != null
        JavassistCompletionBean completionBean = MyAcceptTZProviderImpl.isOkPsiElement((LeafPsiElement) psiElement);
//        String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(completionBean.literalElement);
//        String realValue = value4.replace(IdeaMagic.addedConstant, '');
        String realValue = completionBean.getValueInLiteral();


        //int offset = value4.indexOf(IdeaMagic.addedConstant);
//        String valutoClac = value4.substring(0, offset);
//        if (offset < 0) {
//            log.error("invalid offset cp2 ${offset} , value = ${realValue} , value3 = ${value4}")
//            return;
//        }
        log.debug("cp 8 : value = ${realValue}")

        timeZones.collect {
            String aaa = it;
            LookupElement element23 = LookupElementBuilder.create(it).withIcon(timeZoneIcon).withCaseSensitivity(true);
            String display = displayText.get(aaa);
            if (display == null) {
                log.debug "display is null for ${aaa}"
            }
            TimeZoneLookupElement lookupElement = new TimeZoneLookupElement(element23, display, timeZoneIcon);
            result.addElement(lookupElement);
        };
    }


    private void testNotUsed() {
        TimeZone.getTimeZone('')
    }

}
