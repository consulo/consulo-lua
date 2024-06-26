/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.sylvanaar.idea.lua.lang.psi.impl.lists;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.codeStyle.CodeStyleManager;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.Assignable;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.impl.expressions.LuaExpressionImpl;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.lua.util.LuaAtomicNotNullLazyValue;
import javax.annotation.Nonnull;

import static com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes.COMMA;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 8:16:33 AM
 */
public class LuaIdentifierListImpl extends LuaExpressionImpl implements LuaIdentifierList {
    public LuaIdentifierListImpl(ASTNode node) {
        super(node);
    }

    @Override
    public int count() {
        return findChildrenByClass(LuaSymbol.class).length;
    }

    final LuaAtomicNotNullLazyValue<LuaSymbol[]> symbols = new Symbols();

    @Override
    public LuaSymbol[] getSymbols() {
        return symbols.getValue();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        symbols.drop();
    }

    @Override
    public Assignable[] getDeclarations() {
        return findChildrenByClass(LuaDeclarationExpression.class);
    }

    public String toString() {
        return String.format("Identifier List (Count %d)", count());
    }

    @Override
    public LuaReferenceElement[] getReferenceExprs() {
        return findChildrenByClass(LuaReferenceElement.class);
    }

    @Override
    public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place) {
        return LuaPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }


    @Override
    public PsiElement addAfter(@Nonnull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        PsiElement usingElement = element;
        if (getSymbols().length == 0) {
            add(usingElement);
        } else {
            usingElement = super.addAfter(usingElement, anchor);
            final ASTNode astNode = getNode();
            if (anchor != null) {
                astNode.addLeaf(COMMA, ",", usingElement.getNode());
            } else {
                astNode.addLeaf(COMMA, ",", usingElement.getNextSibling().getNode());
            }
            CodeStyleManager.getInstance(getManager().getProject()).reformat(this);
        }

        return usingElement;
    }

    private class Symbols extends LuaAtomicNotNullLazyValue<LuaSymbol[]> {
        @Nonnull
        @Override
        protected LuaSymbol[] compute() {
            return findChildrenByClass(LuaSymbol.class);
        }
    }
}
