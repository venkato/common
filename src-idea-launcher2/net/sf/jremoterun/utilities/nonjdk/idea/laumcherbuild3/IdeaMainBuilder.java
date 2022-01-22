package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild3;

import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaBuiderSettings3;
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaMainBuilderI;
import org.jetbrains.jps.cmdline.BuildMain;

import java.util.Arrays;
import java.util.logging.Logger;

public class IdeaMainBuilder implements IdeaMainBuilderI {

    private static final Logger log = Logger.getLogger(IdeaMainBuilder.class.getName());

    public  static IdeaBuiderSettings3 params;

    @Override
    public void created(IdeaBuiderSettings3 params1) {
        params = params1;
        if(params1.setLogging){
            IdeaBuildLogSetter.doAll();
        }
        log.info("args = "+ Arrays.toString(params1.args));
        BuildMain.main( params1.args);
    }


}
