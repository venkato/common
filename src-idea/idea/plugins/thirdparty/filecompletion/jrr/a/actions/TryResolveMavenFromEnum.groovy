package idea.plugins.thirdparty.filecompletion.jrr.a.actions

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectly;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrEnumConstant
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.typedef.enumConstant.GrEnumConstantImpl;

import java.util.logging.Logger;

@CompileStatic
class TryResolveMavenFromEnum {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static Object tryResolve2(GrEnumConstant aa) {
        PsiClass declaredType = aa.getContainingClass()
        String className1 = declaredType.getQualifiedName()
        boolean ifCan = FieldResolvedDirectly.fieldResolvedDirectly.tryResolveEnumIfSuperClass(declaredType)
        if (!ifCan) {
            log.info "not supported ${className1}"
            return null
        }
        String fieldName1 = aa.getName();
//        log.info "resolving enum ${fieldName1} ${className1} .. "
        try {
            Object vv = FieldResolvedDirectly.fieldResolvedDirectly.tryResolveEnumIfCan(className1, fieldName1)

            return vv
        } catch (Exception e) {
            log.error("failed resolve ${className1}  ${fieldName1}", e)
            JrrUtilitiesShowE.showException("failed resolve ${className1}  ${fieldName1}", e)
            return null
        }

    }

    static MavenId tryResolve(GrEnumConstant aa) {
        Object vv = tryResolve2(aa)
        if (vv instanceof MavenIdContains) {
            MavenIdContains mmm = vv
            return mmm.m
        }
        if (vv instanceof MavenIdAndRepoContains) {
            MavenIdAndRepoContains kk = vv
            return kk.mavenIdAndRepo.m;
        }
//            log.info "cannot resolve enum ${fieldName1} ${className1}"
        return null
    }


}
