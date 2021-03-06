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

package com.sylvanaar.idea.Lua.lang.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.ChangeUtil;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.IncorrectOperationException;
import com.sylvanaar.idea.Lua.LuaFileType;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;

public class LuaPsiElementImpl extends ASTWrapperPsiElement implements LuaPsiElement  {
    private static final Logger log = Logger.getInstance("Lua.LuaPsiElementImpl");


    public LuaPsiElementImpl(ASTNode node) {
        super(node);
    }

    protected void log(String text) {
        log.debug(this + ": " + text);
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitElement(this);
    }

    @Override
    public void acceptChildren(LuaElementVisitor visitor) {
        acceptLuaChildren(this, visitor);
    }

    @Override
    public String getPresentationText() {
        return null;
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitElement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public String toString() {
        return getNode().getElementType().toString();
    }

    @Nonnull
    public Language getLanguage() {
        return LuaFileType.LUA_LANGUAGE;
    }

//    @NotNull
//    public SearchScope getUseScope() {
//        //This is true as long as we have no inter-file references
//        return new ProjectAndLibrariesScope(getProject());
//    }

//    @NotNull @Override public GlobalSearchScope getResolveScope() {
//        return GlobalSearchScope.allScope(getProject());
//    }

    //    @Override
//    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state,
// PsiElement lastParent, @NotNull PsiElement place) {
//        return LuaPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
//    }

    public PsiElement replace(@Nonnull PsiElement newElement) throws IncorrectOperationException {
        CompositeElement treeElement = calcTreeElement();
        assert treeElement.getTreeParent() != null;
        CheckUtil.checkWritable(this);
        TreeElement elementCopy = ChangeUtil.copyToElement(newElement);
        treeElement.getTreeParent().replaceChildInternal(treeElement, elementCopy);
        elementCopy = ChangeUtil.decodeInformation(elementCopy);
        return SourceTreeToPsiMap.treeElementToPsi(elementCopy);
    }

    protected CompositeElement calcTreeElement() {
        return (CompositeElement) getNode();
    }


    public static void acceptLuaChildren(PsiElement parent, LuaElementVisitor visitor) {
        PsiElement child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof LuaPsiElement) {
                ((LuaPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }
    
    
}
