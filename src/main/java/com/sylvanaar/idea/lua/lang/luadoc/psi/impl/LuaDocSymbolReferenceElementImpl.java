/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.lang.luadoc.psi.impl;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocSymbolReference;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTagValueToken;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFieldIdentifier;
import com.sylvanaar.idea.lua.lang.psi.resolve.LuaResolveResult;
import com.sylvanaar.idea.lua.lang.psi.resolve.processors.ResolveProcessor;
import com.sylvanaar.idea.lua.lang.psi.resolve.processors.SymbolResolveProcessor;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaStatementElement;
import com.sylvanaar.idea.lua.lang.psi.stubs.index.LuaGlobalDeclarationIndex;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ResolveResult;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.IncorrectOperationException;
import consulo.project.content.scope.ProjectScopes;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/25/11
 * Time: 5:20 PM
 */
public class LuaDocSymbolReferenceElementImpl extends LuaDocReferenceElementImpl implements LuaDocSymbolReference {
    public LuaDocSymbolReferenceElementImpl(@Nonnull ASTNode node) {
        super(node);
    }

    public String toString() {
        return "LuaDocSymbolReference: " + StringUtil.notNullize(getName());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Nonnull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
        final String refName = getName();
        if (refName == null)
            return LuaResolveResult.EMPTY_ARRAY;

        ResolveProcessor processor = new SymbolResolveProcessor(refName, this, incompleteCode);

        final LuaDocCommentOwner docOwner = LuaDocCommentUtil.findDocOwner(this);
        if (docOwner != null) {
            final LuaStatementElement statementElement =
                    PsiTreeUtil.getParentOfType(docOwner, LuaStatementElement.class, false);
            if (statementElement != null)
                statementElement.processDeclarations(processor, ResolveState.initial(), this, this);
        }
        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }
        
        LuaGlobalDeclarationIndex index = LuaGlobalDeclarationIndex.getInstance();
        Collection<LuaDeclarationExpression> names = index.get(refName, getProject(),
                ProjectScopes.getAllScope(getProject()));
        for (LuaDeclarationExpression name : names) {
            name.processDeclarations(processor, ResolveState.initial(), this, this);
        }

        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }

        return LuaResolveResult.EMPTY_ARRAY;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
        throw new IncorrectOperationException("not implemented");
    }

    @Nullable
    public PsiElement resolve() {
        final ResolveResult[] results = multiResolve(false);
        if (results.length != 1) return null;
        return results[0].getElement();
    }

    @Nonnull
    public String getCanonicalText() {
        return StringUtil.notNullize(getName());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
	{
        throw new IncorrectOperationException("not implemented");
    }

    @Override
    public LuaDocTagValueToken getReferenceNameElement() {
        return this;
    }

    public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
        if (isReferenceTo(element)) return this;
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        if (!(element instanceof LuaFieldIdentifier)) return false;
        return getManager().areElementsEquivalent(element, resolve());
    }
    
    @Nonnull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false; 
    }
}
