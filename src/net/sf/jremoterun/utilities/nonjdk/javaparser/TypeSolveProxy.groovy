package net.sf.jremoterun.utilities.nonjdk.javaparser

import com.github.javaparser.resolution.TypeSolver
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration
import com.github.javaparser.resolution.model.SymbolReference
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class TypeSolveProxy implements TypeSolver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TypeSolver nestedSolver;

    TypeSolveProxy(TypeSolver nestedSolver) {
        this.nestedSolver = nestedSolver
    }

    TypeSolver getParent() {
        return nestedSolver.getParent()
    }

    @Override
    void setParent(TypeSolver parent) {
        nestedSolver.setParent(parent)
    }

    @Override
    SymbolReference<ResolvedReferenceTypeDeclaration> tryToSolveType(String name4) {
        log.info "${name4}"
        SymbolReference<ResolvedReferenceTypeDeclaration> solveType = nestedSolver.tryToSolveType(name4)
        log.info "${name4} ${solveType}"
        return solveType
    }

}
