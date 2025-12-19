package com.sylvanaar.idea.lua.lang.psi.resolve;

import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.resolve.processors.SymbolResolveProcessor;
import com.sylvanaar.idea.lua.lang.psi.stubs.index.LuaGlobalDeclarationIndex;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaLocal;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.util.LuaAssignmentUtil;
import consulo.language.psi.PsiManager;
import consulo.language.psi.resolve.ResolveCache;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.logging.Logger;
import consulo.project.Project;
import jakarta.annotation.Nullable;

import java.util.Collection;

public class LuaResolver implements ResolveCache.PolyVariantResolver<LuaReferenceElement> {
    public static final Logger log = Logger.getInstance("Lua.LuaResolver");

    @Nullable
    public LuaResolveResult[] resolve(LuaReferenceElement reference, boolean incompleteCode) {
        if (reference.getText() == null) return LuaResolveResult.EMPTY_ARRAY;
        final LuaResolveResult[] results = _resolve(reference, reference.getManager(), incompleteCode);
        if (results.length == 1) {
            final LuaSymbol element = (LuaSymbol) results[0].getElement();
            assert element != null;

            final LuaSymbol referenceElement = (LuaSymbol) reference.getElement();
            assert referenceElement != null;

            LuaAssignmentUtil.transferSingleType(element, referenceElement, element.getLuaType(), referenceElement.getLuaType());
        }
        return results;
    }

    private static LuaResolveResult[] _resolve(LuaReferenceElement ref,
											   PsiManager manager, boolean incompleteCode) {

        if (ref.getName() == null) {
            return LuaResolveResult.EMPTY_ARRAY;
        }
        
        SymbolResolveProcessor processor = new SymbolResolveProcessor(ref, incompleteCode);

        ResolveUtil.treeWalkUp(ref, processor);

        if (processor.hasCandidates() || ref.getElement() instanceof LuaLocal) {
            if (!processor.hasCandidates())
                return LuaResolveResult.EMPTY_ARRAY;

            return new LuaResolveResult[]{ processor.getCandidates()[0] };
        }

        // Search the Project Files
        final Project project = manager.getProject();
        final GlobalSearchScope sc = ref.getResolveScope();
//        final LuaPsiFile currentFile = (LuaPsiFile) ref.getContainingFile();
        final String globalRefName = ref.getCanonicalText();

        LuaGlobalDeclarationIndex index = LuaGlobalDeclarationIndex.getInstance();
        Collection<LuaDeclarationExpression> names = index.get(globalRefName, project, sc);
        for (LuaDeclarationExpression name : names) {
//            log.debug(name + " --> ");
            name.processDeclarations(processor, ResolveState.initial(), ref, ref);
        }

        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }

        return LuaResolveResult.EMPTY_ARRAY;
    }
}