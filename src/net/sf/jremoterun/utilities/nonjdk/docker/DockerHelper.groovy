package net.sf.jremoterun.utilities.nonjdk.docker


import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.Credential
import com.google.cloud.tools.jib.api.CredentialRetriever
import com.google.cloud.tools.jib.api.JibEvent
import com.google.cloud.tools.jib.api.buildplan.FileEntriesLayer
import com.google.cloud.tools.jib.api.buildplan.FilePermissions
import com.google.cloud.tools.jib.builder.ProgressEventDispatcher
import com.google.cloud.tools.jib.builder.steps.BuildResult
import com.google.cloud.tools.jib.builder.steps.PullBaseImageStepJrr
import com.google.cloud.tools.jib.builder.steps.StepsRunnerJrr
import com.google.cloud.tools.jib.configuration.BuildContext
import com.google.cloud.tools.jib.configuration.ContainerConfiguration
import com.google.cloud.tools.jib.configuration.ImageConfiguration
import com.google.cloud.tools.jib.event.EventHandlers
import com.google.cloud.tools.jib.global.JibSystemProperties
import com.google.cloud.tools.jib.http.FailoverHttpClient
import com.google.cloud.tools.jib.image.Image
import com.google.cloud.tools.jib.registry.RegistryClient
import com.google.cloud.tools.jib.registry.credentials.CredentialRetrievalException
import com.google.common.util.concurrent.MoreExecutors
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNames

import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.Future
import java.util.logging.Logger;

@CompileStatic
class DockerHelper extends DockerMetadataFetcher{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public DockerLayerJrr dockerLayerJrr = new DockerLayerJrr()
    public DockerMetadataFetcher dockerMetadataFetcher = this


//    public JibContainerBuilderOriginJrr b;
    //public Containerizer c;
    public BuildContext buildContext;

    public File applicationCache;
    public File imageCache;
    public File jrrTmpDir;
    private StepsRunnerJrr stepsRunnerJrr

    public String javaLibsOthers = '/javalibs/other/'
    public String javaLibsIvy = '/javalibs/ivy'

    public String scriptLineSeparator = '\n'
    public String javarunnerSh = 'javarunner.sh'
    public boolean setEntryPoint = true
    public List<FileEntriesLayer> layers = []
    public List<String> entryPoint = []
    public List<String> javaRunnerLines = ['#!/bin/sh']

    public ContainerConfiguration.Builder containerConfigurationBuilder =ContainerConfiguration.builder()
    public BuildContext.Builder contextBuilder = BuildContext.builder()

    public JavaProcessRunnerDocker jpr = new JavaProcessRunnerDocker(this)


    /**
     * com.google.cloud.tools.jib.registry.RegistryClient#factory
     */
    DockerHelper(ImageConfiguration imageConfiguration, ImageConfiguration targetImageConfig, File applicationCache, File imageCache) {
        //dockerMetadataFetcher.setStopper(rstaScriptDockerHelper);
        this.applicationCache = applicationCache
        this.imageCache = imageCache
        jrrTmpDir = new File(imageCache, 'jrrtmp')
        jrrTmpDir.mkdir()
        jpr.pathSeparator = ':'
        jpr.addJavaClasspathAsArg = false
        contextBuilder.setBaseImageConfiguration(imageConfiguration)
        contextBuilder.setTargetImageConfiguration(targetImageConfig)
        contextBuilder.setAdditionalTargetImageTags(new HashSet<String>())
        contextBuilder.setApplicationLayersCacheDirectory(Paths.get(applicationCache.getAbsolutePath()))
        contextBuilder.setBaseImageLayersCacheDirectory(Paths.get(imageCache.getAbsolutePath()))
        contextBuilder.setExecutorService(MoreExecutors.newDirectExecutorService())
        contextBuilder.setAlwaysCacheBaseImage(true)
        //contextBuilder.setToolName('jib-core')
        //contextBuilder.setLayerConfigurations([])
        contextBuilder.setToolVersion(Containerizer.getPackage().getImplementationVersion())
        containerConfigurationBuilder.setCreationTime(Instant.now())
    }

    void createBuildContextIfNeeded(){
        if(buildContext==null){
            createBuildContext()
        }
    }

    static void setGitlabCredentialRetriever(String user, String password, ImageConfiguration.Builder builder){
            builder.setCredentialRetrievers( buildCredentialRetriever(user,password))
    }

    static List<CredentialRetriever> buildCredentialRetriever(String user, String password){
        CredentialRetriever retriever = new CredentialRetriever() {
            @Override
            Optional<Credential> retrieve() throws CredentialRetrievalException {
                return Optional.of(Credential.from(user,password));
            }

        }
        return (List)[retriever]
    }

    Object findResult(){
        Object r = JrrClassUtils.getFieldValue(stepsRunnerJrr,'results')
        return r;
    }

    RegistryClient findTargetRegistryClient(){
        Object r = findResult()
        Future<RegistryClient> f= JrrClassUtils.getFieldValueR(new ClRef('com.google.cloud.tools.jib.builder.steps.StepsRunner$StepResults'), r,'targetRegistryClient') as Future<RegistryClient>
        return f.get()
    }



    void createBuildContext(){
        assert buildContext ==null
        assert dockerMetadataFetcher.httpClient==null
        EventHandlers eventHandlers = EventHandlers.builder().add(JibEvent,dockerMetadataFetcher.loggerConsumer).build();
        contextBuilder.setEventHandlers(eventHandlers)
        if (jpr.javaClasspath.addedFilesWithOrder.size() > 0) {
            buildJavaCmd()
        }
        if (setEntryPoint && entryPoint.size() > 0) {
            containerConfigurationBuilder.setEntrypoint(entryPoint)
        }
        layers.add(dockerLayerJrr.layer1.build())
        contextBuilder.setContainerConfiguration(containerConfigurationBuilder.build())
        contextBuilder.setLayerConfigurations(layers);
        buildContext = contextBuilder.build()
        dockerMetadataFetcher.httpClient = JrrClassUtils.getFieldValue(buildContext, 'httpClient') as FailoverHttpClient
        stepsRunnerJrr = new StepsRunnerJrr(buildContext.getExecutorService(), buildContext)
        dockerMetadataFetcher.configureHttpClient()
    }


    static void speedup() {
        System.setProperty(JibSystemProperties.SKIP_EXISTING_IMAGES, 'true');
    }

    void buildJavaCmd() {
        assert buildContext ==null
        jpr.buildCustomArgs()
        jpr.fullCmd.add('$*')
        String prefix = '/' + javarunnerSh
        if (jpr.javaClassPathBuilt != null) {
            jpr.envHuman.put(OsEnvNames.CLASSPATH.customName, jpr.javaClassPathBuilt)
        }
        jpr.envHuman.each {
            javaRunnerLines.add "export ${it.key}=${it.value}".toString()
        }
        javaRunnerLines.add(jpr.fullCmd.join(' '))
        File child = jrrTmpDir.child(javarunnerSh)
        child.text = javaRunnerLines.join(scriptLineSeparator) + scriptLineSeparator
        dockerLayerJrr.layer1.addEntry(Paths.get(child.getAbsolutePath()), com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath.get(prefix), FilePermissions.fromOctalString('777'))
        entryPoint = [prefix]
    }

    @Override
    void closeConnectionIfCan() {
        buildContext.getExecutorService().shutdown()
        super.closeConnectionIfCan()
    }

    List<Image> pullImage(){
        createBuildContext()
        ProgressEventDispatcher newRoot = ProgressEventDispatcher.newRoot(buildContext.getEventHandlers(), 'pullImage', 30)
        PullBaseImageStepJrr imageStepJrr = new PullBaseImageStepJrr(buildContext, newRoot.newChildProducer())
        Object res = imageStepJrr.call()
        List<Image> res2 = JrrClassUtils.getFieldValueR(new ClRef('com.google.cloud.tools.jib.builder.steps.PullBaseImageStep$ImagesAndRegistryClient'), res,'images') as List<Image>
        return res2
    }


    BuildResult publish(){
        createBuildContext()
        BuildResult buildResult = stepsRunnerJrr.registryPushSteps().run()
        return buildResult;
    }

    BuildResult createTar(File dest){
        createBuildContext()
        BuildResult buildResult = stepsRunnerJrr.tarBuildSteps(Paths.get(dest.getAbsolutePath())).run()
        return buildResult;
    }



}
