package net.sf.jremoterun.utilities.nonjdk.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.javaonly.InitInfo
import org.codehaus.groovy.control.CompilationFailedException

import org.eclipse.osgi.internal.debug.Debug
import org.eclipse.osgi.internal.framework.EquinoxContainer
import org.eclipse.osgi.internal.loader.EquinoxClassLoader
import org.eclipse.osgi.internal.loader.classpath.ClasspathEntry
import org.eclipse.osgi.internal.loader.classpath.ClasspathManager

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class EclipseLunaClasspathAdd {

    public static InitInfo initInfo = new InitInfo(EclipseLunaClasspathAdd);
    //public static volatile boolean inited = false
	public static volatile Thread initializerThread;
    static final Object lock1 = new Object()
	public static org.eclipse.osgi.internal.loader.EquinoxClassLoader clP;
    public static List<Integer> debugFlags = [];
	public static File eclipseInitScriptDebug;

	
	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    static {
        log.info("JrrUtils inited");
		
    }

    public static volatile AddFilesToClassLoaderGroovy addCl;
    public static volatile Throwable startupError;

	

    public static ClasspathManager findClasspathManager() {
        clP = (EquinoxClassLoader) JrrClassUtils
                .getCurrentClassLoader();
        log.info("cl = " + clP);
        log.info("bundle name = " + clP.getBundle().getSymbolicName());
        ClasspathManager classpathManager = clP.getClasspathManager();
        return classpathManager;
    }
	
	static void enableBootDeligation() {
		EquinoxContainer eq =  clP.getBundleLoader().@container;
		JrrClassUtils.setFieldValue( eq,'bootDelegateAll', true);
	}

    public static ArrayList<ClasspathEntry> al;
    public static ClasspathManager classpathManager;

    public static void propaget() throws Exception {
        JrrClassUtils.setFieldValue(classpathManager, "entries", al.toArray(new ClasspathEntry[0]));
    }


    public static boolean init() {
        synchronized (lock1) {
            if (initInfo.isInited()) {
                log.info("already inited")
                return true
            } else {
                initInfo.setInited();
                try {
                    initImpl();
                    return true
                } catch (Throwable e) {
                    startupError = e
                    log.log(Level.SEVERE, null, e);
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed init eclipse", e);
                    return false
                }
            }
        }
    }

    public static void initImpl() throws Exception {
		debugFlags.add(1);
		initializerThread = Thread.currentThread();
        SetConsoleOut2.setConsoleOutIfNotInited();
		debugFlags.add(2);
        initImpl2();
		debugFlags.add(3);
    }
	
	static void enableParalellLoading(boolean enable) {
		JrrClassUtils.setFieldValue(clP, "isRegisteredAsParallel", enable);
	}

    public static void initImpl2() throws Exception {
		debugFlags.add(21);
        log.info("starting added cutom classed");

        log.info("thread details : ${initializerThread.name} ${initializerThread.id}");
        classpathManager = findClasspathManager();
		debugFlags.add(22);
		enableParalellLoading(false);
		debugFlags.add(23);

        // al.add(classpathEntry);
        ClasspathEntry[] classpathEntries = (ClasspathEntry[]) JrrClassUtils.getFieldValue(classpathManager, "entries");
		debugFlags.add(24);
        al = new ArrayList<ClasspathEntry>(Arrays.asList(classpathEntries));		
        // classpathManager.addClassPathEntry(al, clName, classpathManager,
        // null);

        // FileInputStream fis=new FileInputStream(file);
        addCl = new EclipseInitAddFilesToClassLoaderGroovy();
		debugFlags.add(26);
        // addCl.addFromGroovyFile(EclipseSettings.fileWithCLassPath2);
        runScriptInUserDir();
		debugFlags.add(27);
        runCustom();
		debugFlags.add(28);
        //		addCl.addFromGroovyFile(EclipseSettings.fileWithCLassPath4);
        log.info("trying adding :  " + addCl.getAddedFiles2().size());
        JrrClassUtils.setFieldValue(classpathManager, "entries", al.toArray(classpathEntries));
		debugFlags.add(29);
        log.info("classes added fine");
    }

    static void runScriptInUserDir() throws CompilationFailedException, IOException {
		if(JrrStarterVariables2.getInstance().filesDir==null){
			debugFlags.add(1);
			log.info "files dir is null"            
			net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("files dir is null",new Exception("files dir is null"));
		}else{
			eclipseInitScriptDebug = new File(JrrStarterVariables2.getInstance().filesDir,JrrEclipseStartupSettings.eclipseInitGroovyScriptName);
			if(eclipseInitScriptDebug.exists()){
				debugFlags.add 2;
				if(JrrStarterVariables2.getInstance().classesDir!=null){
					debugFlags.add 3;
					addCl.addF JrrStarterVariables2.getInstance().classesDir
				}
				log.info("running ${eclipseInitScriptDebug} ..");
                runFile(eclipseInitScriptDebug)
//				Script parse = GroovyMethodRunner.groovyScriptRunner.groovyShell.parse(eclipseInitScriptDebug)
//				parse.run()
				log.info("finished ${eclipseInitScriptDebug}");
			}else{
				log.info "file not exist : ${eclipseInitScriptDebug}"
				debugFlags.add 4;
				net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("file not exist : ${eclipseInitScriptDebug}",new Exception("file not exist : ${eclipseInitScriptDebug}"));
			}
			debugFlags.add 5;
		}
		
		
        File userHome = new File(System.getProperty("user.home"));
        if (userHome.exists()) {
            File scriptInUserDir = new File(userHome, "jrr/configs/eclipse.groovy");
            if (scriptInUserDir.exists()) {
                log.info("running ${scriptInUserDir} ..");
//                Script parse = GroovyMethodRunner.groovyScriptRunner.getGroovyShell().parse(scriptInUserDir);
//                parse.run();

                log.info("finished ${scriptInUserDir}");
            }
        }
    }

    static void runFile(File f){
        RunnableFactory.runRunner(f)
//        net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory.receiveGroovyClassLoader()
//        GroovyConfigLoaderGeneric groovyConfigLoaderJrr=new GroovyConfigLoaderGeneric()
//        Object obj1 = groovyConfigLoaderJrr.parseConfig(f)
//        if (obj1 instanceof Script) {
//            Script parse = (Script) obj1;
//            parse.run()
//        }else{
//            Runnable runnable = obj1 as Runnable
//            runnable.run()
//        }
    }

    public static String customScriptProperty = "eclipse.custom.init";

    static void runCustom() throws CompilationFailedException, IOException {
        String customScript = System.getProperty(customScriptProperty);
        if (customScript == null) {
            log.info("property not set :" + customScriptProperty);
        } else {
            File customScriptF = new File(customScript);
            if (customScriptF.exists()) {
                log.info("running " + customScriptF);
                runFile(customScriptF)
//                Script parse = GroovyMethodRunner.groovyScriptRunner.getGroovyShell().parse(customScriptF);
//                parse.run();
                log.info("finished ${customScriptF}");
            } else {
                log.info("File not found : " + customScript);
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("File not found " + customScript, new FileNotFoundException(customScript));
            }
        }
    }

    public static void enableDebugClassloader() throws Exception {
        ClasspathManager classpathManager = findClasspathManager();
        Debug debug = (Debug) JrrClassUtils.getFieldValue(classpathManager, "debug");
        JrrClassUtils.setFieldValue(debug, "DEBUG_LOADER", true);
    }

}
