package net.sf.jremoterun.utilities.nonjdk.classpath.sl;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.FileScriptSource

import java.security.MessageDigest
import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner
 */
@CompileStatic
class GroovySettingsLoader {
    private static final Logger log = Logger.getLogger(JrrClassUtils.currentClass.name);

    public ClassLoader parentClassLoaderToLoad

    public GroovyShell groovyShell

    public boolean useCache = true

    public Map<String, Class> scriptCache = [:]

    public MessageDigest messageDigest;

    public java.nio.charset.Charset enconding = java.nio.charset.Charset.forName('utf8');

    public static GroovySettingsLoader groovySettingsLoader = new GroovySettingsLoader()

    GroovySettingsLoader() {
        this(JrrClassUtils.currentClassLoader)
    }

    GroovySettingsLoader(ClassLoader parentClassLoaderToLoad) {
        this.parentClassLoaderToLoad = parentClassLoaderToLoad
        groovyShell = createGroovyShell(parentClassLoaderToLoad)
    }

    GroovyShell createGroovyShell(ClassLoader parentClassLoaderToLoad) throws Exception {
        GroovyShell groovyShell = new GroovyShell(parentClassLoaderToLoad);
        return groovyShell;
    }


    Script createScript(String scriptSource, String scriptName, Binding binding) {
        Class scriptClass = createScriptClass(scriptSource, scriptName)
        Script script = scriptClass.newInstance() as Script
        script.setBinding(binding)
        return script
    }


    Class createScriptClass(String scriptSource, String scriptName) {
        String digest;
        if (useCache) {
            digest = calcDigest(scriptSource)
            Class script = scriptCache.get(digest)
            if (script != null) {
                return script;
            }
        }
        log.fine "creating script ${scriptName}"
        Class scriptClass = groovyShell.classLoader.parseClass(scriptSource, scriptName)
        if (useCache) {
            scriptCache.put(digest, scriptClass);
        }
        return scriptClass;
    }




    String calcDigest(String scriptSource) {
        initDigest()
        String digest = new String(messageDigest.digest(scriptSource.getBytes(enconding)), enconding)
        return digest
    }

    Object loadsettings(File file, Binding binding) {
        Script script1
        try {
            script1 = createScript(file.text, file.name, binding)
            if (script1 instanceof FileScriptSource) {
                FileScriptSource configLoaderLocationAware = script1
                configLoaderLocationAware.setFileScriptSource(file)
            }
            return script1.run()
        } catch (Throwable e) {
            log.info("failed load ${file}",e)
            throw e
        }
    }

    void initDigest() {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance('SHA-256');
        }
    }

}
