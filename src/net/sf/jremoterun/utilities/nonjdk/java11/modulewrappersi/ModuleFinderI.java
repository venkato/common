package net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi;

import java.util.List;

public interface ModuleFinderI {



    ModuleWrapperI wrap(Object module);

     ModuleWrapperI getModuleForClass2(String name);


    ClassLoader getSystemClassLoader();

    ClassLoader getPlatformClassLoader();

    ModuleWrapperI getUnnamedModuleForClassLoader(ClassLoader cl1);

    ModuleWrapperI getModuleForClass(Class clazz);

    public List<ModuleWrapperI> getAllModules() ;

}
