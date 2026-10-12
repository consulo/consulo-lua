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
package com.sylvanaar.idea.lua.lang.psi.impl;

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.impl.ast.CompositeElement;
import consulo.language.impl.ast.SharedImplUtil;
import consulo.language.impl.psi.stub.StubBasedPsiElementBase;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import jakarta.annotation.Nonnull;

/**
 * @author ilyas
 */
public abstract class LuaStubElementBase<T extends StubElement> extends StubBasedPsiElementBase<T> implements LuaPsiElement {
    protected LuaStubElementBase(T stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LuaStubElementBase(ASTNode node) {
        super(node);
    }

    @Nonnull
    @Override
    @RequiredReadAction
    public Language getLanguage() {
        return LuaFileType.LUA_LANGUAGE;
    }

    @Override
    @RequiredReadAction
    public int getTextOffset() {
      return calcTreeElement().getTextOffset();
    }

    @RequiredReadAction
    protected CompositeElement calcTreeElement() {
      return (CompositeElement)getNode();
    }

    @Override
    @RequiredReadAction
    public PsiElement getParent() {
        return getParentByStub();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitElement(this);
    }

    @Override
    @RequiredReadAction
    public void acceptChildren(LuaElementVisitor visitor) {
        //LuaPsiElementImpl.acceptLuaChildren(this, visitor);
        SharedImplUtil.acceptChildren(visitor, calcTreeElement());
    }

    @RequiredReadAction
    protected PsiElement getDefinitionParent() {
        PsiElement candidate = getParentByStub();
        if (candidate instanceof LuaPsiFile) {
            return candidate;
        }

        return SharedImplUtil.getParent(getNode());
    }

    @Override
    @RequiredReadAction
    public LocalizeValue getPresentationText() {
        return LocalizeValue.of(getText());
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProvider.getItemPresentation(this);
    }
}