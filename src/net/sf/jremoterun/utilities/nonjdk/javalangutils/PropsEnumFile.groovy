package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.reftype.ToFileRefUnsupported
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2

/**
 * @see net.sf.jremoterun.utilities.nonjdk.windowsos.UserHomeOsGenericClass* @see org.apache.commons.lang3.SystemUtils
 */
@CompileStatic
enum PropsEnumFile implements EnumNameProvider,  ToFileRefRedirect2 {
    java_home,
    user_home,
    user_dir,
    /**
     * @see javax.management.loading.MLet#init
     */
    jmx_mlet_library_dir,

    /**
     * @see sun.security.ssl.TrustStoreManager.TrustStoreDescriptor#jsseDefaultStore
     */
    javax_net_ssl_trustStore,

    /**
     * @see sun.security.ssl.SSLContextImpl.DefaultManagersHolder#getKeyManagers
     */
    javax_net_ssl_keyStore,

    java_io_tmpdir,
    ;


    String customName;

    PropsEnumFile() {
        customName = name().replace('_', '.')
    }


    String getValue() {
        return System.getProperty(customName)
    }

    void setValue(String value1) {
        System.setProperty(customName, value1)
    }


//    @Override
//    File resolveToFile() {
//        String value1 = getValue()
//        if (value1 == null || value1.length() == 0) {
//            return null
//        }
//        return new File(value1)
//    }
//
//
//    @Override
//    FileChildLazyRef childL(String child) {
//        return new FileChildLazyRef(this, child);
//    }
//
//    @Override
//    FileChildLazyRef childP(ChildPattern child) {
//        return new FileChildLazyRef(this, child)
//    }

    @Override
    File2FileRefWithSupportI getRedirect() {
        String value1 = getValue()
        if (value1 == null || value1.length() == 0) {
            return new ToFileRefUnsupported(this)
        }
        return new FileToFileRef(new File(value1))
    }
}
