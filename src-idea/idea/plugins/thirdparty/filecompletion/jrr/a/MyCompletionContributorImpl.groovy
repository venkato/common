package idea.plugins.thirdparty.filecompletion.jrr.a

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils


/**
 * Reserved for future flexibility
 */
@CompileStatic
class MyCompletionContributorImpl extends CompletionContributor {

    //private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
    }
}
