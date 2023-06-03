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

package com.sylvanaar.idea.Lua.lang.luadoc.psi.impl;

import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.impl.psi.LazyParseablePsiElement;
import consulo.language.ast.IElementType;
import consulo.util.lang.CharArrayUtil;
import com.sylvanaar.idea.Lua.lang.luadoc.parser.LuaDocElementTypes;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocTag;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.Lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.ASTNode;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public class LuaDocCommentImpl extends LazyParseablePsiElement implements LuaDocElementTypes, LuaDocComment {
  public LuaDocCommentImpl(CharSequence text) {
    super(LUADOC_COMMENT, text);
  }

  public String toString() {
      LuaDocCommentOwner owner = getOwner();
      return "LuaDoc: " + StringUtil.notNullize(owner != null ? owner.toString() : null, "no owner");
  }

  public IElementType getTokenType() {
    return getElementType();
  }

  public void accept(LuaElementVisitor visitor) {
    visitor.visitDocComment(this);
  }

  public void acceptChildren(LuaElementVisitor visitor) {
    PsiElement child = getFirstChild();
    while (child != null) {
      if (child instanceof LuaPsiElement) {
        ((LuaPsiElement)child).accept(visitor);
      }

      child = child.getNextSibling();
    }
  }

    @Override
    public String getPresentationText() {
        return null;
    }

    public LuaDocCommentOwner getOwner() {
    return LuaDocCommentUtil.findDocOwner(this);
  }

  @Nonnull
  public LuaDocTag[] getTags() {
    final LuaDocTag[] tags = PsiTreeUtil.getChildrenOfType(this, LuaDocTag.class);
    return tags == null ? LuaDocTag.EMPTY_ARRAY : tags;
  }

  @Nullable
  public LuaDocTag findTagByName(@NonNls String name) {
    if (!getText().contains(name)) return null;
    for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof LuaDocTag && ((LuaDocTag)e).getName().equals(name)) {
        return (LuaDocTag)e;
      }
    }
    return null;
  }

  @Nonnull
  public LuaDocTag[] findTagsByName(@NonNls String name) {
    if (!getText().contains(name)) return LuaDocTag.EMPTY_ARRAY;
    ArrayList<LuaDocTag> list = new ArrayList<LuaDocTag>();
    for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof LuaDocTag && CharArrayUtil.regionMatches(((LuaDocTag)e).getName(), 1, name)) {
        list.add((LuaDocTag)e);
      }
    }
    return list.toArray(new LuaDocTag[list.size()]);
  }

  @Nonnull
  public PsiElement[] getDescriptionElements() {
    ArrayList<PsiElement> array = new ArrayList<PsiElement>();
    for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {

      if (child instanceof PsiWhiteSpace)
          continue;
      final ASTNode node = child.getNode();
      if (node == null) continue;
      final IElementType i = node.getElementType();
      if (i == LDOC_TAG) break;
      if (i == LDOC_COMMENT_DATA ) {
        array.add(child);
      }
    }
    return LuaPsiUtils.toPsiElementArray(array);
  }

   // Return the first line of the description
   // up to and including the first '.'
   @Nonnull
   @Override
   public String getSummaryDescription() {
       PsiElement[] elems = getDescriptionElements();

       if (elems.length == 0)
           return "";

       String first = StringUtil.notNullize(elems[0].getText());

       int pos = first.indexOf('.');

       if (pos>0)
           return first.substring(0, pos);

       return first;
   }
}
