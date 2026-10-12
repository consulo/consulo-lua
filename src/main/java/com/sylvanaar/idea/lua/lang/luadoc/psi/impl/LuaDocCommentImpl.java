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

import com.sylvanaar.idea.lua.lang.luadoc.parser.LuaDocElementTypes;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTag;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.impl.psi.LazyParseablePsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.localize.LocalizeValue;
import consulo.util.lang.CharArrayUtil;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class LuaDocCommentImpl extends LazyParseablePsiElement implements LuaDocElementTypes, LuaDocComment {
    public LuaDocCommentImpl(CharSequence text) {
        super(LUADOC_COMMENT, text);
    }

    @Override
    public String toString() {
        LuaDocCommentOwner owner = getOwner();
        return "LuaDoc: " + StringUtil.notNullize(owner != null ? owner.toString() : null, "no owner");
    }

    public IElementType getTokenType() {
        return getElementType();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitDocComment(this);
    }

    @Override
    @RequiredReadAction
    public void acceptChildren(LuaElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof LuaPsiElement childElem) {
                childElem.accept(visitor);
            }

            child = child.getNextSibling();
        }
    }

    @Override
    public LocalizeValue getPresentationText() {
        return LocalizeValue.empty();
    }

    @Override
    public LuaDocCommentOwner getOwner() {
        return LuaDocCommentUtil.findDocOwner(this);
    }

    @Nonnull
    @Override
    @RequiredReadAction
    public LuaDocTag[] getTags() {
        LuaDocTag[] tags = PsiTreeUtil.getChildrenOfType(this, LuaDocTag.class);
        return tags == null ? LuaDocTag.EMPTY_ARRAY : tags;
    }

    @Nullable
    @Override
    @RequiredReadAction
    public LuaDocTag findTagByName(String name) {
        if (!getText().contains(name)) {
            return null;
        }
        for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e instanceof LuaDocTag docTag && docTag.getName().equals(name)) {
                return docTag;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    @RequiredReadAction
    public LuaDocTag[] findTagsByName(String name) {
        if (!getText().contains(name)) {
            return LuaDocTag.EMPTY_ARRAY;
        }
        List<LuaDocTag> list = new ArrayList<>();
        for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e instanceof LuaDocTag docTag && CharArrayUtil.regionMatches(docTag.getName(), 1, name)) {
                list.add(docTag);
            }
        }
        return list.toArray(new LuaDocTag[list.size()]);
    }

    @Nonnull
    @Override
    @RequiredReadAction
    public PsiElement[] getDescriptionElements() {
        List<PsiElement> array = new ArrayList<>();
        for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {

            if (child instanceof PsiWhiteSpace) {
                continue;
            }
            ASTNode node = child.getNode();
            if (node == null) {
                continue;
            }
            IElementType i = node.getElementType();
            if (i == LDOC_TAG) {
                break;
            }
            if (i == LDOC_COMMENT_DATA) {
                array.add(child);
            }
        }
        return LuaPsiUtils.toPsiElementArray(array);
    }

    // Return the first line of the description
    // up to and including the first '.'
    @Nonnull
    @Override
    @RequiredReadAction
    public String getSummaryDescription() {
        PsiElement[] elems = getDescriptionElements();

        if (elems.length == 0) {
            return "";
        }

        String first = StringUtil.notNullize(elems[0].getText());

        int pos = first.indexOf('.');

        if (pos > 0) {
            return first.substring(0, pos);
        }

        return first;
    }
}
