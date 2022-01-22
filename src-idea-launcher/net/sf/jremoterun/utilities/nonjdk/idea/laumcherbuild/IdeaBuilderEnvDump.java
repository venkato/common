package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

public class IdeaBuilderEnvDump extends JavaEnvDump {


    public static void dumpEnv() {
        System.out.println(new IdeaBuilderEnvDump().collectData());
    }


    @Override
    public void buildData() {
        super.buildData();
        addLine("mainArguments=" + IdeaBuildRunnerSettings.argsPv2);
    }


}
