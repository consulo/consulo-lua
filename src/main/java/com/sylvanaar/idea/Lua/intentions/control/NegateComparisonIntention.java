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
package com.sylvanaar.idea.lua.intentions.control;

import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.lua.intentions.LuaIntentionsBundle;
import com.sylvanaar.idea.lua.intentions.base.MutablyNamedIntention;
import com.sylvanaar.idea.lua.intentions.base.PsiElementPredicate;
import com.sylvanaar.idea.lua.intentions.utils.ComparisonUtils;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaBinaryExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;


public class NegateComparisonIntention extends MutablyNamedIntention {
  protected String getTextForElement(PsiElement element) {
    final LuaBinaryExpression binaryExpression =
        (LuaBinaryExpression) element;
    final IElementType tokenType = binaryExpression.getOperationTokenType();
    final String comparison = ComparisonUtils.getStringForComparison(tokenType);
    final String negatedComparison = ComparisonUtils.getNegatedComparison(tokenType);

    return LuaIntentionsBundle.message("negate.comparison.intention.name", comparison, negatedComparison);
  }

  @Nonnull
  public PsiElementPredicate getElementPredicate() {
    return new ComparisonPredicate();
  }

  public void processIntention(@Nonnull PsiElement element)
      throws IncorrectOperationException {
    final LuaBinaryExpression exp =
        (LuaBinaryExpression) element;
    final IElementType tokenType = exp.getOperationTokenType();

    final LuaExpression lhs = exp.getLeftOperand();
    final String lhsText = lhs.getText();

    final LuaExpression rhs = exp.getRightOperand();
    final String rhsText = rhs.getText();

    final String negatedComparison = ComparisonUtils.getNegatedComparison(tokenType);

    final String newExpression = lhsText + negatedComparison + rhsText;
    replaceExpressionWithNegatedExpressionString(newExpression, exp);
  }

}
