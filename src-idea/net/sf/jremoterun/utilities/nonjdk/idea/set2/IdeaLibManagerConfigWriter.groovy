package net.sf.jremoterun.utilities.nonjdk.idea.set2

import com.intellij.tools.Tool
import com.intellij.tools.ToolManager
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.JavaBeanStore
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI
import org.codehaus.groovy.runtime.MethodClosure;

import java.util.logging.Logger;

@CompileStatic
class IdeaLibManagerConfigWriter extends JavaBeanStore{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static MethodClosure setMavenServerMethod = (MethodClosure) (Closure) IdeaLibManagerConfig.&setMavenServer


    IdeaLibManagerConfigWriter() {
        super(IdeaLibManagerConfig)
    }

    @Override
    List<LineInfoI> save(JavaBean javaBean) {
        List<LineInfoI> r = []
        r.addAll(addCustomFields())
        r.addAll super.save(javaBean)
        return r;
    }

    List<LineInfoI> addCustomFields(){
        String mavenServer3 = MavenDefaultSettings.mavenDefaultSettings.mavenServer;
        String mavenServerS = objectWriter.writeObject(writer3Importer,mavenServer3)
        List<Tool> tools = ToolManager.instance.tools
        tools =  tools.findAll {it.enabled}
        String toolsS =  tools.collect{it.name}.join(',')
        String s = "${varName}.${setMavenServerMethod} ${mavenServerS} ;";
//        int minWidth = ideaLibManagerSwing.getMinWidth()
//        String s2 = "${varName}.ideaLibManagerSwing.setMinWidth ${minWidth} ;";
        toolsS = "// Put any available tools from Settings->Tools->External tools: ${toolsS}"
        //List<String> list = JavaBeanStore.save(varName, this, writer3, objectWriter,true)
        List<? extends LineInfoI> result = [new LineInfo(Brakets.netural, s),new LineInfo(Brakets.netural, toolsS)]
        return (List)result
    }

}
