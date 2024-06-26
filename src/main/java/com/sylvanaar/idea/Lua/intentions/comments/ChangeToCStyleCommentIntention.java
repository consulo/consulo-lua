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
package com.sylvanaar.idea.lua.intentions.comments;

import consulo.language.ast.IElementType;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.lua.intentions.base.Intention;
import com.sylvanaar.idea.lua.intentions.base.PsiElementPredicate;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElementFactory;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChangeToCStyleCommentIntention extends Intention {


  @Nonnull
  protected PsiElementPredicate getElementPredicate() {
    return new EndOfLineCommentPredicate();
  }

  public void processIntention(@Nonnull PsiElement element)
      throws IncorrectOperationException {
    final PsiComment selectedComment = (PsiComment) element;
    PsiComment firstComment = selectedComment;

    while (true) {
      final PsiElement prevComment =
          getPrevNonWhiteSpace(firstComment);
      if (!isEndOfLineComment(prevComment)) {
        break;
      }
      firstComment = (PsiComment) prevComment;
    }

    final LuaPsiElementFactory factory = LuaPsiElementFactory.getInstance(element.getProject());
      
    String text = getCommentContents(firstComment);
    final List<PsiElement> commentsToDelete = new ArrayList<PsiElement>();
    PsiElement nextComment = firstComment;
    while (true) {
      nextComment = getNextNonWhiteSpace(nextComment);
      if (!isEndOfLineComment(nextComment)) {
        break;
      }
      text += nextComment.getPrevSibling().getText() 
          + getCommentContents((PsiComment) nextComment);
      commentsToDelete.add(nextComment);
    }
    final PsiComment newComment =
        factory.createCommentFromText("--[[\n" + text + "\n]]", selectedComment.getParent());
    firstComment.replace(newComment);
    for (PsiElement commentToDelete : commentsToDelete) {
      commentToDelete.delete();
    }
  }

  @Nullable
  private PsiElement getNextNonWhiteSpace(PsiElement nextComment) {
    PsiElement elementToCheck = nextComment;
    while (true) {
      final PsiElement sibling = elementToCheck.getNextSibling();
      if (sibling == null) {
        return null;
      }
      if (sibling.getText().trim().replace("\n", "").length() == 0) {
        elementToCheck = sibling;
      } else {
        return sibling;
      }
    }
  }

  @Nullable
  private PsiElement getPrevNonWhiteSpace(PsiElement nextComment) {
    PsiElement elementToCheck = nextComment;
    while (true) {
      final PsiElement sibling = elementToCheck.getPrevSibling();
      if (sibling == null) {
        return null;
      }
      if (sibling.getText().trim().replace("\n", "").length() == 0) {
        elementToCheck = sibling;
      } else {
        return sibling;
      }
    }
  }

  private boolean isEndOfLineComment(PsiElement element) {
    if (!(element instanceof PsiComment)) {
      return false;
    }
    final PsiComment comment = (PsiComment) element;
    final IElementType tokenType = comment.getTokenType();
    return LuaTokenTypes.SHORTCOMMENT.equals(tokenType);
  }

  private static String getCommentContents(PsiComment comment) {
    final String text = comment.getText();
    return text.substring(2);
  }
}
