/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sylvanaar.idea.lua.lang.psi.impl.symbols;

import consulo.language.ast.ASTNode;
import consulo.language.psi.scope.GlobalSearchScope;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiElementFactoryImpl;
import com.sylvanaar.idea.lua.lang.psi.symbols.*;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/11
 * Time: 1:29 AM
 */
public class LuaLocalIdentifierImpl extends LuaIdentifierImpl implements LuaLocalIdentifier {
    public LuaLocalIdentifierImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@Nonnull String s) throws IncorrectOperationException
	{
        LuaIdentifier node = LuaPsiElementFactoryImpl.getInstance(getProject()).createLocalNameIdentifier(s);
        replace(node);

        final PsiReference reference = node.getReference();
        if (reference != null) reference.resolve();

        return this;
    }

    @Override
    public boolean isSameKind(LuaSymbol identifier) {
        return identifier instanceof LuaLocalDeclaration || identifier instanceof LuaParameter;
    }

    @Override
    public boolean isAssignedTo() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public GlobalSearchScope getResolveScope() {
        return GlobalSearchScope.fileScope(this.getContainingFile());
    }

    @Override
    public PsiReference getReference() {
        if (getParent() instanceof PsiReference && ((PsiReference) getParent()).getElement().equals(this))
            return (PsiReference) getParent();

        return super.getReference();
    }


    @Override
    public String toString() {
        return "Local: " + getText();
    }

//    @Override
//    public PsiElement getAliasElement() {
//        PsiReference ref = (PsiReference) getParent();
//        if (ref == null) return null;
//        PsiElement def = ref.resolve();
//        if (def == null) return null;
//
//        assert def instanceof LuaLocalDeclaration;
//
//        return ((LuaLocalDeclaration) def).getAliasElement();
//    }


}
