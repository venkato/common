package idea.plugins.thirdparty.filecompletion.jrr.a

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.ExtensionsArea
import com.intellij.openapi.extensions.impl.ExtensionComponentAdapter
import com.intellij.openapi.extensions.impl.ExtensionPointImpl
import com.intellij.util.containers.MultiMap
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.ReloadClassActionImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.RemoteRunActionImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.OpenFileInExternalToolActionImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.charset.CharsetCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.charset.MyAcceptCharsetProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.MyAcceptJavassistProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.JrrCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.MyAcceptJrrProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.MavenCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.MyAcceptMavenProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.systemprop.MyAcceptSystemPropProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.systemprop.SystemPropCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.timezone.MyAcceptTZProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.timezone.TZCompletionProviderImpl
import idea.plugins.thirdparty.filecompletion.share.MyGroovyCompletionContributor
import idea.plugins.thirdparty.filecompletion.share.MyJavaCompletionContributor
import idea.plugins.thirdparty.filecompletion.share.OpenFileInExternalToolAction
import idea.plugins.thirdparty.filecompletion.share.ReloadClassAction
import idea.plugins.thirdparty.filecompletion.share.RemoteRunAction
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrClassUtils2
import net.sf.jremoterun.utilities.classpath.ClRef
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import java.lang.reflect.Field

@CompileStatic
class CompetionContributerRenew {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void renewAll() {
        CompetionContributerRenew.renewGroovyContextAssist()
        CompetionContributerRenew.renewGoto()
        CompetionContributerRenew.renewDocumentation()
        OpenFileInExternalToolAction.thisObject.deligate = new OpenFileInExternalToolActionImpl()
        ReloadClassAction.thisObject.deligate = new ReloadClassActionImpl()
        RemoteRunAction.thisObject.deligate = new RemoteRunActionImpl()
        //CompetionContributerRenew.renewAction(new DebugAction());
    }

    static void renewGroovyContextAssist() {
        MyGroovyCompletionContributor myGroovyCompletionContributor = MyGroovyCompletionContributor.myGroovyCompletionContributor;
        clearCompletionContributor(myGroovyCompletionContributor)
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptFileProviderImpl(), new FileCompletionProviderImpl());
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptJrrProviderImpl(), new JrrCompletionProviderImpl());
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptJavassistProviderImpl(), new JavassistCompletionProviderImpl());
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptTZProviderImpl(), new TZCompletionProviderImpl());
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptMavenProviderImpl(), new MavenCompletionProviderImpl());
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptSystemPropProviderImpl(), new SystemPropCompletionProviderImpl());

        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptCharsetProviderImpl(), new CharsetCompletionProviderImpl());
    }

    static void renewJavaContextAssist() {
        MyJavaCompletionContributor myGroovyCompletionContributor = MyJavaCompletionContributor.myGroovyCompletionContributor;
        clearCompletionContributor(myGroovyCompletionContributor)
        myGroovyCompletionContributor.extend(CompletionType.BASIC, new MyAcceptFileProviderImpl(), new FileCompletionProviderImpl());
    }


    static void clearCompletionContributor(CompletionContributor completionContributor) {
        com.intellij.util.containers.MultiMap<com.intellij.codeInsight.completion.CompletionType, com.intellij.openapi.util.Pair<com.intellij.patterns.ElementPattern<? extends com.intellij.psi.PsiElement>, com.intellij.codeInsight.completion.CompletionProvider<com.intellij.codeInsight.completion.CompletionParameters>>> myMap = (MultiMap) JrrClassUtils.getFieldValue(completionContributor, 'myMap');
        myMap.clear();
    }

    static <T> com.intellij.openapi.extensions.impl.ExtensionPointImpl getExentionPointDebug(ExtensionPointName<T> pointName) {

        com.intellij.openapi.extensions.impl.ExtensionPointImpl point = (ExtensionPointImpl) Extensions.rootArea.getExtensionPoint(pointName);
        return point
    }

    /**
     * works ?
     */
    static <T> void renewExtention(ExtensionPointName<T> pointName, T newIstance) {
        assert newIstance.class != Class.class
        com.intellij.openapi.extensions.impl.ExtensionPointImpl point = (ExtensionPointImpl) Extensions.rootArea.getExtensionPoint(pointName);
        java.util.List<com.intellij.openapi.extensions.impl.ExtensionComponentAdapter> adapterds = JrrClassUtils.getFieldValue(point, 'myLoadedAdapters') as List<ExtensionComponentAdapter> //NOFIELDCHECK
        assert adapterds != null

//        Field myComponentField = JrrClassUtils.findField(ExtensionComponentAdapter, 'myComponentInstance');
        Field myComponentField2 = JrrClassUtils2.findField(ExtensionComponentAdapter){ Field field->
            return field.name == 'myImplementationClassName' ||field.name == 'myImplementationClassOrName'
        }

        List<ExtensionComponentAdapter> adapters = adapterds.findAll {
            String ob = myComponentField2.get(it)
            if (ob != null) {
                // log.debug "${ob.getClass().getName()}"
                return ob == newIstance.class.name
            } else {
                log.debug "can't find ${it}"
            }
            return false;
        }
        assert adapters.size() == 1
        ExtensionComponentAdapter adapter = adapters.first()
        ClRef clRef4=new ClRef('com.intellij.openapi.extensions.impl.ExtensionPointImpl$ObjectComponentAdapter')
        if (adapter.getClass().getName() == clRef4.className) {
            Object oldInstance = JrrClassUtils.getFieldValueR(clRef4, adapter, 'myExtension') //NOFIELDCHECK
            assert oldInstance != null
            assert oldInstance.class != newIstance.class
            JrrClassUtils.setFieldValueR(clRef4,adapter, 'myExtension', newIstance) //NOFIELDCHECK
            //JrrClassUtils.setFieldValue(adapter,'myImplementationClass',newIstance.class)
            JrrClassUtils.setFieldValue(point, 'myExtensionsCache', null) //NOFIELDCHECK
        } else {
            Object oldInstance = JrrClassUtils.getFieldValue(adapter, 'myComponentInstance') //NOFIELDCHECK
            assert oldInstance != null
            JrrClassUtils.setFieldValue(adapter, 'myComponentInstance', newIstance) //NOFIELDCHECK
            JrrClassUtils.setFieldValue(adapter, 'myImplementationClass', newIstance.class) //NOFIELDCHECK
            JrrClassUtils.setFieldValue(adapter, 'myDelegate', null) //NOFIELDCHECK
        }
        log.debug("reload done for ${pointName.name}")
    }

    /**
     * Doesn't work
     */
    @Deprecated
    static void renewAction(AnAction newAction) {
        ActionManagerImpl instance = (ActionManagerImpl) ActionManager.getInstance()
        Map<AnAction, String> myAction2Id = (Map) JrrClassUtils.getFieldValue(instance, 'myAction2Id') //NOFIELDCHECK
        Map<String, AnAction> myId2Action = (Map) JrrClassUtils.getFieldValue(instance, 'myId2Action') //NOFIELDCHECK

        Set<AnAction> oldActions = myAction2Id.keySet().findAll { it.class.name == newAction.class.name }
        assert oldActions.size() == 1
        AnAction oldAction = oldActions.first()

        assert oldAction != null
        assert oldAction.class != newAction.class

        String key = myAction2Id.get(oldAction)
        assert key != null

        assert myId2Action.containsKey(key)
        //List<String> keys = myId2Action.entrySet().findAll { it.value.class.name == newAction.class.name }.collect { it.key }
        assert myAction2Id.remove(oldAction) == key
        myAction2Id.put(newAction, key)
        myId2Action.put(key, newAction)

        log.debug "action reloaded 2 : ${newAction.class.name}"
    }

    static void renewDocumentation() {
        renewExtention(DocumentationProvider.EP_NAME, new MyDocumentationProviderImpl())
    }


    static void renewGoto() {
        renewExtention(GotoDeclarationHandler.EP_NAME, new GotoDeclarationHandlerImpl())
    }


    static void regDocumentation() {
        ExtensionsArea area = Extensions.rootArea;
        area.getExtensionPoint(DocumentationProvider.EP_NAME).registerExtension(new MyDocumentationProviderImpl());
    }

    static void regGotto() {
        ExtensionsArea area = Extensions.rootArea;
        area.getExtensionPoint(GotoDeclarationHandler.EP_NAME).registerExtension(new GotoDeclarationHandlerImpl());
    }

}
