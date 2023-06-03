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

package com.sylvanaar.idea.Lua.lang.luadoc.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.ast.TokenSet;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.*;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiElementFactory;
import com.sylvanaar.idea.Lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static com.sylvanaar.idea.Lua.lang.luadoc.parser.LuaDocElementTypes.*;


/**
 * @author ilyas
 */
public class LuaDocTagImpl extends LuaDocPsiElementImpl implements LuaDocTag {
    private static final TokenSet VALUE_BIT_SET =
            TokenSet.create(LDOC_TAG_VALUE, LDOC_FIELD_REF, LDOC_PARAM_REF, LDOC_REFERENCE_ELEMENT,
                    LDOC_COMMENT_DATA);

    public LuaDocTagImpl(@Nonnull ASTNode node) {
        super(node);
    }

    public void accept(LuaElementVisitor visitor) {
        visitor.visitDocTag(this);
    }

    public String toString() {
        return "LuaDocTag";
    }

    @Nonnull
    public String getName() {
        return getNameElement().getText().substring(1);
    }

    @Nonnull
    public PsiElement getNameElement() {
        PsiElement element = findChildByType(LDOC_TAG_NAME);
        assert element != null;
        return element;
    }


    public LuaDocComment getContainingComment() {
        return (LuaDocComment) getParent();
    }

    public LuaDocTagValueToken getValueElement() {
        final LuaDocReferenceElement reference = findChildByClass(LuaDocReferenceElement.class);
        if (reference == null) return null;
        return reference.getReferenceNameElement();
    }

    @Nullable
    public LuaDocParameterReference getDocParameterReference() {
        return findChildByClass(LuaDocParameterReference.class);
    }

    @Override
    public LuaDocFieldReference getDocFieldReference() {
        return findChildByClass(LuaDocFieldReference.class);
    }

    @Nonnull
    @Override
    public PsiElement[] getDescriptionElements() {
        final List<PsiElement> list = findChildrenByType(LDOC_COMMENT_DATA);
        return LuaPsiUtils.toPsiElementArray(list);
    }

    public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
        final PsiElement nameElement = getNameElement();
        final LuaPsiElementFactory factory = LuaPsiElementFactory.getInstance(getProject());
        final LuaDocComment comment = factory.createDocCommentFromText("--- @" + name);
        nameElement.replace(comment.getTags()[0].getNameElement());
        return this;
    }

}
