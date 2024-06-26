package idea.plugins.thirdparty.filecompletion.share

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import idea.plugins.thirdparty.filecompletion.jrr.a.CompetionContributerRenew
import idea.plugins.thirdparty.filecompletion.jrr.a.MyCompletionContributorImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jetbrains.annotations.NotNull

public class MyGroovyCompletionContributor extends CompletionContributor {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static CompletionContributor completionContributor = new MyCompletionContributorImpl();

    public static MyGroovyCompletionContributor myGroovyCompletionContributor;

    public static Exception callStack;

    public MyGroovyCompletionContributor() {
        if (myGroovyCompletionContributor != null) {
            log.error("myGroovyCompletionContributor was defined before, see next exception when");
            log.error("myGroovyCompletionContributor was defined before, here : ", callStack);
        }
        myGroovyCompletionContributor = this;
        callStack = new Exception();
        CompetionContributerRenew.renewGroovyContextAssist();

    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        completionContributor.fillCompletionVariants(parameters, result);
        //log.debug("parameters " + parameters.getCompletionType());
        super.fillCompletionVariants(parameters, result);
    }


}
