/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.sylvanaar.idea.lua.lang.luadoc.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.ResolveResult;
import consulo.util.collection.ArrayUtil;
import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocParameterReference;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTagValueToken;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElementFactory;
import com.sylvanaar.idea.lua.lang.psi.resolve.LuaResolveResult;
import com.sylvanaar.idea.lua.lang.psi.resolve.LuaResolveResultImpl;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import consulo.language.psi.PsiReference;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * @author ilyas
 */
public class LuaDocParameterReferenceImpl extends LuaDocReferenceElementImpl implements LuaDocParameterReference {

  public LuaDocParameterReferenceImpl(@Nonnull ASTNode node) {
    super(node);
  }

  public String toString() {
    return "LuaDocParameterReference";
  }

  public PsiReference getReference() {
    return this;
  }

  @Nonnull
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final String name = getName();
    if (name == null) return ResolveResult.EMPTY_ARRAY;
    ArrayList<LuaResolveResult> candidates = new ArrayList<LuaResolveResult>();

    final PsiElement owner = LuaDocCommentUtil.findDocOwner(this);
    if (owner instanceof LuaFunctionDefinition) {
      final LuaFunctionDefinition method = (LuaFunctionDefinition)owner;
      final LuaParameter[] parameters = method.getParameters().getLuaParameters();

      for (LuaParameter parameter : parameters) {
        if (name.equals(parameter.getName())) {
          candidates.add(new LuaResolveResultImpl(parameter, true));
        }
      }
      return candidates.toArray(new ResolveResult[candidates.size()]);
    }

    return ResolveResult.EMPTY_ARRAY;
  }

  public PsiElement getElement() {
    return this;
  }

  public TextRange getRangeInElement() {
    return new TextRange(0, getTextLength());
  }

  @Override
  public String getName() {
    return getReferenceNameElement().getText();
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

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
  {
    PsiElement nameElement = getReferenceNameElement();
    ASTNode node = nameElement.getNode();
    ASTNode newNameNode = LuaPsiElementFactory.getInstance(getProject()).createParameterDocMemberReferenceNameFromText(newElementName).getNode();
    assert node != null;
    node.getTreeParent().replaceChild(node, newNameNode);
    return this;
  }

  public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) return this;
    return null;
  }

  public boolean isReferenceTo(PsiElement element) {
    if (!(element instanceof LuaParameter)) return false;
    return getManager().areElementsEquivalent(element, resolve());
  }

  @Nonnull
  public Object[] getVariants() {
    final PsiElement owner = LuaDocCommentUtil.findDocOwner(this);
    if (owner instanceof LuaFunctionDefinition) {
      return ((LuaFunctionDefinition)owner).getParameters().getLuaParameters();
    }
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public boolean isSoft() {
    return false;
  }

  @Nullable
  public LuaDocTagValueToken getReferenceNameElement() {
    LuaDocTagValueToken token = findChildByClass(LuaDocTagValueToken.class);
   
    return token;
  }
}
