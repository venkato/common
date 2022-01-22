package net.sf.jremoterun.utilities.nonjdk.java11.modulewrappers;

import jdk.internal.module.Modules;
import net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi.ModuleWrapperI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MethodFinderReceive implements net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi.ModuleFinderI{


    public ModuleWrapper getModuleForClass2(String name){
        java.util.Optional<Module> m =  Modules.findLoadedModule(name);
        if(m.isPresent()){
            return new ModuleWrapper(m.get());
        }
        return null;
    }


    public ClassLoader getSystemClassLoader(){
        return ClassLoader.getSystemClassLoader();
    }

    public ClassLoader getPlatformClassLoader(){
        return ClassLoader.getPlatformClassLoader();
    }

    public ModuleWrapper getUnnamedModuleForClassLoader(ClassLoader cl1){
        return new ModuleWrapper(cl1.getUnnamedModule());
    }

    public ModuleWrapper getModuleForClass(Class clazz){
        return new ModuleWrapper(clazz.getModule());
    }

    public ModuleWrapperI wrap(Object module){
        return new ModuleWrapper((Module) module);
    }



    public List<ModuleWrapperI> getAllModules() {
         List<ModuleWrapper> modulesNamed = new ArrayList<>();
         //List<Module> modulesUnNamed = new ArrayList<>();
        ModuleLayer moduleLayer = ModuleLayer.boot();
        Set<Module> modules1 = moduleLayer.modules();
        for (Module it : modules1) {
            if (it.isNamed()) {
                modulesNamed.add( new ModuleWrapper( it));
            } else {
               // modulesUnNamed.add(it);
            }
        }
        return (List)modulesNamed;
    }
}