/*
 * Copyright 2009 Max Ishchenko
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

package com.sylvanaar.idea.lua.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.CodeDocumentationAwareCommenterEx;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocTokenTypes;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import consulo.language.psi.PsiComment;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 14:12:45
 */
@ExtensionImpl
public class LuaCommenter implements CodeDocumentationAwareCommenterEx
{

    public String getLineCommentPrefix() {
        return "--";
    }

    public String getBlockCommentPrefix() {
        return "--[[";
    }

    public String getBlockCommentSuffix() {
        return "]]";

    }

    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }


  public boolean isDocumentationCommentText(final PsiElement element) {
    if (element == null) return false;
    final ASTNode node = element.getNode();
    return node != null && node.getElementType() == LuaDocTokenTypes.LDOC_COMMENT_DATA;
  }

    @Override
    public IElementType getLineCommentTokenType() {
        return LuaTokenTypes.SHORTCOMMENT;
    }

    @Override
    public IElementType getBlockCommentTokenType() {
        return LuaTokenTypes.LONGCOMMENT;
    }

    @Override
    public IElementType getDocumentationCommentTokenType() {
        return LuaTokenTypes.LUADOC_COMMENT;
    }

    @Override
    public String getDocumentationCommentPrefix() {
        return "---";
    }

    @Override
    public String getDocumentationCommentLinePrefix() {
        return "--";
    }

    @Override
    public String getDocumentationCommentSuffix() {
        return null;
    }

    @Override
    public boolean isDocumentationComment(PsiComment element) {
        return element instanceof LuaDocComment;
    }


    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
