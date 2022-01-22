package net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi;

import java.util.Set;

public interface ModuleWrapperI {


    public boolean isNamed();

    public String getName();

    public ClassLoader getClassLoader();

    Object getNestedObject();

    public boolean canRead(ModuleWrapperI other);

    //    @CallerSensitive
//    public ModuleWrapperI implAddExportsOrOpens(ModuleWrapperI other);
    public ModuleWrapperI addReads(ModuleWrapperI other);

    public boolean isExported(String pn, ModuleWrapperI other);

    public boolean isOpen(String pn, ModuleWrapperI other);

    public boolean isExported(String pn);

    public boolean isOpen(String pn);

    //@CallerSensitive
    public ModuleWrapperI addExports(String pn, ModuleWrapperI other);

    // @CallerSensitive
    public ModuleWrapperI addOpens(String pn, ModuleWrapperI other);

    //  @CallerSensitive
    public ModuleWrapperI addUses(Class<?> service);

    public boolean canUse(Class<?> service);

    public Set<String> getPackages();


}
